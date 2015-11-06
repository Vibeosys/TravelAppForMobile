package com.vibeosys.travelapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
        extends BaseActivity {

    //Facebook


    private Activity mFromactivitycall;


    public LoginActivity() {
        // Required empty public constructor

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Login");
        super.onCreate(savedInstanceState);
        //Facebook SDK Initialization
        FacebookLoginAPIInit(LoginActivity.this);

        //Google Plus API Initialization
        googlePlusAPIInit();

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

        //mProgressDialog = new ProgressDialog(this.getApplicationContext());
    }

    //-------------Facebook API calls--------------------------------//
    public void FacebookLogin(final Activity act) {
        //Facebook call authentication
        LoginManager.getInstance().logInWithReadPermissions(act, Arrays.asList("public_profile", "user_friends", "email"));
    }


    public void FacebookLoginAPIInit(final Context cx) {
        FacebookSdk.sdkInitialize(cx);
        mCallbackManager = CallbackManager.Factory.create();

        // Callback registration
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            ProgressDialog progressDialog = new ProgressDialog(cx);

            @Override
            public void onSuccess(LoginResult loginResult) {

                progressDialog.show();

                // App code
                final AccessToken facebookAppToken = loginResult.getAccessToken();
                //Getting all details in one short, no need to call individual methods to get details from Facebook
                GraphRequest request = GraphRequest.newMeRequest(facebookAppToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        progressDialog.setMessage("Setting up your account");
                        progressDialog.show();

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
                            Toast.makeText(getApplicationContext(), "User is not able to login successfully.", Toast.LENGTH_SHORT);
                            Log.e("LoginFacebook", "User is not Added successfully " + object.toString());
                        }
                        downloadImgFromFbGPlusAndUploadToAws(profilePic.toString());

                        //Intent intent = new Intent(cx, MainActivity.class);
                        /*intent.putExtra("Profiledetails", object.toString());
                        intent.putExtra("ProfileImg", profilePic.toString());*/
                        //startActivity(intent);
                        progressDialog.dismiss();
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
                Toast.makeText(getApplicationContext(), "User authentication cancelled!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(), "Authentication failed.Please check the Internet connection!", Toast.LENGTH_LONG).show();
                Log.e("LoginFacebook",exception.getMessage());
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
    }




}
