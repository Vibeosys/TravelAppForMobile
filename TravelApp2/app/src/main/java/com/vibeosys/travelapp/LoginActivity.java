package com.vibeosys.travelapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.RegistrationSourceTypes;
import com.vibeosys.travelapp.util.SessionManager;
import com.vibeosys.travelapp.util.UserAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


/**
 * Created by mahesh on 10/2/2015.
 */
public class LoginActivity
        extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    //Facebook
    protected CallbackManager callbackManager;

    //Google Plus
    GoogleApiClient mGoogleApiClient;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 1;
    private Activity mFromactivitycall;


    public LoginActivity() {
        // Required empty public constructor

    }

    @Override
    protected void onNewIntent(Intent intent) {

        Intent myIntent = getIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Facebook SDK Initialization
        FacebookLoginAPIInit(LoginActivity.this);

        //Google Plus API Initialization
        GooglePlusAPIInit();

        setContentView(R.layout.login_layout);

        //GooglePlus Authentication after clicking button
        findViewById(R.id.btn_GPLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GooglePlusLogin(LoginActivity.this);
            }
        });

        //Facebook Authentication after clicking button
        Button btn_fb_login = (Button) findViewById(R.id.btn_fbLogin);
        btn_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacebookLogin(LoginActivity.this);
            }
        });

        mProgressDialog = new ProgressDialog(this.getApplicationContext());
    }

    //-------------Facebook API calls--------------------------------//
    public void FacebookLogin(final Activity act) {
        //Facebook call authentication
        LoginManager.getInstance().logInWithReadPermissions(act, Arrays.asList("public_profile", "user_friends", "email"));
    }

    public void FacebookLogout() {
        try {
            LoginManager.getInstance().logOut();

            //Clean Authentication data from share preference
            UserAuth.CleanAuthenticationInfo();

            Toast.makeText(getApplicationContext(), "You have successfully logged out", Toast.LENGTH_SHORT);
        } catch (Exception ex) {
            Log.e("Facebook Logout:", ex.getMessage());
        }
    }


    public void FacebookLoginAPIInit(final Context cx) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                final AccessToken facebookAppToken = loginResult.getAccessToken();
                //Getting all details in one short, no need to call individual methods to get details from Facebook
                GraphRequest request = GraphRequest.newMeRequest(facebookAppToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Profile facebookProfile = Profile.getCurrentProfile();
                        if (facebookProfile == null) {
                            Log.e("Login", "facebook Profile is null");
                            Toast.makeText(getApplicationContext(), "Facebook application is not installed", Toast.LENGTH_SHORT);
                            return;
                        }

                        final Uri profilePic = facebookProfile.getProfilePictureUri(150, 150);
                        User theUser = null;

                        try {
                            theUser = new User(SessionManager.Instance().getUserId(),
                                    object.getString("name"),
                                    object.getString("email"),
                                    RegistrationSourceTypes.FACEBOOK,
                                    facebookAppToken.getToken(),
                                    object.getString("id"));
                        } catch (JSONException ex) {
                            Log.e("LoginFacebook", "JSON exception from facebook data deserialization");
                        }

                        boolean userAdded = UserAuth.saveAuthenticationInfo(theUser, getApplicationContext());
                        if (!userAdded) {
                            Toast.makeText(getApplicationContext(), "User is not Added successfully", Toast.LENGTH_SHORT);
                            Log.e("LoginFacebook", "User is not Added successfully " + object.toString());
                        }
                        downloadAvatar(profilePic.toString());

                        //Intent intent = new Intent(cx, MainActivity.class);
                        /*intent.putExtra("Profiledetails", object.toString());
                        intent.putExtra("ProfileImg", profilePic.toString());*/
                        //startActivity(intent);

                        Log.d("JSON Data", object.toString());
                        //finish();
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,birthday,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

//-------------Google API Call----------------------------------//

    protected void GooglePlusLogin(Activity act) {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();
        mFromactivitycall = act;

        // Show a message to the user that we are signing in.
        Toast.makeText(this, "Signing in", Toast.LENGTH_SHORT).show();
    }

    protected void GooglePlusLogout() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        try {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();

                //Clean Authentication data from share preference
                UserAuth.CleanAuthenticationInfo();

                Toast.makeText(getApplicationContext(), "You have successfully logged out", Toast.LENGTH_SHORT);
            }
        } catch (Exception ex) {
            Log.e("Google Logout:", ex.getMessage());
        }


        // Show a message to the user that we are signing in.
        Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        try {

            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson == null) {
                Log.e("LoginGoogle+", "Error while getting data from Google+");
                return;
            }
            String name = currentPerson.getDisplayName();
            String imageURL = currentPerson.getImage().getUrl();

            //changing the default size of image which API return i.e 50 X 50
            imageURL = imageURL.substring(0,
                    imageURL.length() - 2)
                    + 150;

            String googleEmailId = Plus.AccountApi.getAccountName(mGoogleApiClient);

            User theUser = new User(SessionManager.Instance().getUserId(),
                    currentPerson.getDisplayName(),
                    googleEmailId,
                    RegistrationSourceTypes.GOOGLE_PLUS,
                    null,
                    currentPerson.getId());

            Boolean userAdded = UserAuth.saveAuthenticationInfo(theUser, getApplicationContext());
            downloadAvatar(currentPerson.getImage().getUrl());

            if (!userAdded) {
                Toast.makeText(getApplicationContext(), "User is not Added successfully", Toast.LENGTH_SHORT);
                Log.e("LoginGoogle+", "User is not Added successfully ");
            }
            JSONObject object = new JSONObject();


            object.put("name", name);
            object.put("email", googleEmailId);


            Log.d(TAG, ":" + name);
            Log.d(TAG, ":" + imageURL);

            //finish();

            //Redirect to Page once Authenticatied

            //Intent intent = new Intent(mFromactivitycall, MainActivity.class);
            //Send Data or Save Data
            //intent.putExtra("Profiledetails", object.toString());
            //intent.putExtra("ProfileImg", imageURL);

            //startActivity(intent);

        } catch (JSONException ex) {
            Log.d(TAG, "Error:" + ex.getMessage());
        }


        // Show a message to the user that we are signing in.
        //Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, "Signout", Toast.LENGTH_SHORT).show();
        }

    }

    //------------------------------------------------------------------------//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Facebook call to get Data
        callbackManager.onActivityResult(requestCode, resultCode, data);


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

    public void GooglePlusAPIInit() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    @Override
    public void finish() {
        setProfileInfoInNavigationBar();
        super.finish();
    }


}
