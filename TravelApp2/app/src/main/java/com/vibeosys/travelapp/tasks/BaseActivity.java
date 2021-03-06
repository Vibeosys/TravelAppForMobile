package com.vibeosys.travelapp.tasks;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.io.InputStream;
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
    private static ImageView userProfileImage;
    protected static final int PERMISSION_REQUEST_MEDIA_TYPE_IMAGE = 21;
    protected static final int PERMISSION_REQUEST_CAMERA = 22;

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
        userProfileImage = (ImageView) view.findViewById(R.id.userProfileImage);

        //Setting values from JSON Object
        userName.setText(SessionManager.Instance().getUserName());
        userEmail.setText(SessionManager.Instance().getUserEmailId());

        if (mSessionManager.getUserPhotoUrl() != null && !mSessionManager.getUserPhotoUrl().isEmpty()) {
            new LoadProfileImage(userProfileImage).execute(mSessionManager.getUserPhotoUrl());
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
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        //.addScope(new Scope(Scopes.EMAIL))

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
                imageURL = imageURL.substring(0,
                        imageURL.length() - 2)
                        + 150;

                // downloadImgFromFbGPlusAndUploadToAws(imageURL);
                new LoadProfileImage(userProfileImage).execute(imageURL);
                theUser.setUserName(name);
                theUser.setThirdPartyUserId(gId);
                theUser.setPhotoURL(imageURL);
                progressDialog.dismiss();
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

    /**
     * Background Async task to load user profile picture from url
     */
    private static class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    protected void getPermissionsForReadWriteStorage(int requestCode) {
        if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d("Permissions", "Permission has been granted");
            performReadWriteStorageAction(requestCode);
        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, requestCode);
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode);
        }
    }

    protected boolean checkPermission(String strPermission) {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), strPermission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    protected void performReadWriteStorageAction(int requestCode) {
    }

    protected void requestPermission(String strPermission, int perCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, strPermission)) {
            Toast.makeText(getApplicationContext(), "GPS permission allows us to access location data. " +
                    "Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(this, new String[]{strPermission}, perCode);
        }
    }

    protected void getPermissionsForCamera(int requestCode) {
        if (checkPermission(Manifest.permission.CAMERA)
                && checkPermission(Manifest.permission.MEDIA_CONTENT_CONTROL)) {
            Log.d("Permissions", "Permission has been granted");
            captureMyImage();
            //fetchLocationData();
        } else {
            requestPermission(Manifest.permission.CAMERA, requestCode);
            requestPermission(Manifest.permission.MEDIA_CONTENT_CONTROL, requestCode);
        }
    }

    protected void captureMyImage() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_MEDIA_TYPE_IMAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performReadWriteStorageAction(requestCode);
                }
                break;
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    captureMyImage();
                }
                break;
        }
    }
}
