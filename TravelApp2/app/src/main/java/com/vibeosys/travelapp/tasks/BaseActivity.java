package com.vibeosys.travelapp.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.util.ImageFileUploader;
import com.vibeosys.travelapp.util.RegistrationSourceTypes;
import com.vibeosys.travelapp.util.ServerSyncManager;
import com.vibeosys.travelapp.util.SessionManager;
import com.vibeosys.travelapp.util.UserAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Base Activity will give the basic implementation with async task support and other things
 */
public abstract class BaseActivity extends AppCompatActivity
        implements ImageFileUploader.OnUploadCompleteListener,
        ImageFileUploader.OnUploadErrorListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int RC_SIGN_IN = 1;

    protected ServerSyncManager mServerSyncManager = null;
    protected NewDataBase mNewDataBase = null;
    protected static SessionManager mSessionManager = null;
    protected GoogleApiClient mGoogleApiClient;
    protected CallbackManager mCallbackManager;
    //Google Plus
    protected boolean mIsResolving = false;
    protected boolean mShouldResolve = false;
    protected final static String TAG = "com.vibeosys";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSessionManager = SessionManager.getInstance(getApplicationContext());
        mServerSyncManager = new ServerSyncManager(getApplicationContext(), mSessionManager);
        mNewDataBase = new NewDataBase(getApplicationContext(), mSessionManager);
    }

    protected static void setProfileInfoInNavigationBar(View view) {
        // After successful Login

        TextView userName = (TextView) view.findViewById(R.id.userName);
        TextView userEmail = (TextView) view.findViewById(R.id.userEmailID);
        ImageView userProfileImage = (ImageView) view.findViewById(R.id.userProfileImage);

        //Setting values from JSON Object
        userName.setText(SessionManager.Instance().getUserName());
        userEmail.setText(SessionManager.Instance().getUserEmailId());

        if (mSessionManager.getUserPhotoUrl() != null && !mSessionManager.getUserPhotoUrl().isEmpty()) {
            downloadImageAsync(mSessionManager.getUserPhotoUrl(), userProfileImage);
        }
    }

    protected void downloadImgFromFbGPlusAndUploadToAws(final String url) {
        URL fbAvatarUrl = null;
        Bitmap thirdPartyImage = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            fbAvatarUrl = new URL(url);
            thirdPartyImage = BitmapFactory.decodeStream(fbAvatarUrl.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            Log.e("DownloadImageEx", "Exception while downloading AWS image " + e.toString());
        } catch (IOException e) {
            Log.e("DownloadImageEx", "Exception while downloading AWS image " + e.toString());
        }

        ImageFileUploader imageFileUploader = new ImageFileUploader(getApplicationContext());
        imageFileUploader.setOnUploadCompleteListener(this);
        imageFileUploader.setOnUploadErrorListener(this);
        imageFileUploader.uploadUserProfileImage(thirdPartyImage);
    }

    protected static synchronized void downloadImageAsync(final String url, final ImageView imageView) {
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {

            @Override
            public Bitmap doInBackground(Void... params) {
                URL fbAvatarUrl = null;
                Bitmap fbAvatarBitmap = null;
                try {
                    fbAvatarUrl = new URL(url);
                    fbAvatarBitmap = BitmapFactory.decodeStream(fbAvatarUrl.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    Log.e("DownloadImgBkgErr", "TravelAppError occurred while downloading profile image in background " + e.toString());
                } catch (IOException e) {
                    Log.e("DownloadImgBkgErr", "TravelAppError occurred while downloading profile image in background " + e.toString());
                } catch (Exception e) {
                    Log.e("DownloadImgBkgErr", "TravelAppError occurred while downloading profile image in background " + e.toString());
                }
                return fbAvatarBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null)
                    imageView.setImageBitmap(result);
            }

        };
        task.execute();
    }

    protected void googlePlusLogout() {
        googlePlusAPIInit();
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        try {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            } else {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception ex) {
            Log.e("Google Logout:", ex.getMessage());
        }

        // Show a message to the user that we are signing out.
        Toast.makeText(getApplicationContext(), "You have successfully logged out", Toast.LENGTH_SHORT);
    }

    protected void googlePlusAPIInit() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    protected void facebookLogout() {
        try {
            LoginManager.getInstance().logOut();

            Toast.makeText(getApplicationContext(), "You have successfully logged out", Toast.LENGTH_SHORT);
        } catch (Exception ex) {
            Log.e("Facebook Logout:", ex.getMessage());
        }
    }


    @Override
    public void onUploadComplete(String uploadJsonResponse, Map<String, String> inputParameters) {
        try {
            JSONObject jsonObject = new JSONObject(uploadJsonResponse);
            String imageUrl = jsonObject.getString("message");
            SessionManager.Instance().setUserPhotoUrl(imageUrl);
        } catch (JSONException e) {
            Log.e("ProfileImgUpErr", "JSON exception while uploading the image." + e.toString());
        }

        this.finish();
    }

    @Override
    public void onUploadError(VolleyError error) {

        Log.e("ProfileUploadError", error.toString());
        this.finish();
    }


    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting up your account");
        progressDialog.show();

        User theUser = new User();
        theUser.setUserId(mSessionManager.getUserId());
        theUser.setLoginSource(RegistrationSourceTypes.GOOGLE_PLUS);

        try {

            String userEmailId = Plus.AccountApi.getAccountName(mGoogleApiClient);
            theUser.setEmailId(userEmailId);

            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

            if (currentPerson != null) {
                String name = currentPerson.getDisplayName();
                String imageURL = currentPerson.getImage().getUrl();
                String gId = currentPerson.getId();
                /*imageURL = imageURL.substring(0,
                        imageURL.length() - 2)
                        + 150;*/

                downloadImgFromFbGPlusAndUploadToAws(imageURL);

                theUser.setUserName(name);
                theUser.setThirdPartyUserId(gId);

            } else {
                theUser.setUserName(userEmailId.substring(0, userEmailId.indexOf("@")));

            }
            UserAuth userAuth = new UserAuth();
            userAuth.saveAuthenticationInfo(theUser, getApplicationContext());
            userAuth.setOnUpdateUserResultReceived(new UserAuth.OnUpdateUserResultReceived() {
                @Override
                public void onUpdateUserResult(int errorCode) {
                    //if(errorCode == 0)
                    //{
                    finish();
                    //}
                    progressDialog.dismiss();
                }
            });


        } catch (Exception ex) {
            Log.d(TAG, "TravelAppError:" + ex.getMessage());
        } finally {

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(this, connectionResult.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show the signed-out UI
            Toast.makeText(this, "Authentication failed.Please check the Internet connection!", Toast.LENGTH_SHORT).show();
        }

    }

    //------------------------------------------------------------------------//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Facebook call to get Data
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        //Google-Plus login Data
        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }

        Log.d("Vibeosys", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

    }

    protected void logOut() {
        if (SessionManager.Instance().getUserLoginRegdSoure() == RegistrationSourceTypes.FACEBOOK) {
            facebookLogout();
        } else if (SessionManager.Instance().getUserLoginRegdSoure() == RegistrationSourceTypes.GOOGLE_PLUS) {
            googlePlusLogout();
        }

        //Clean Authentication data from share preference
        UserAuth.CleanAuthenticationInfo();
    }

    protected void createAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // whatever...
                    }
                }).create().show();
    }

}
