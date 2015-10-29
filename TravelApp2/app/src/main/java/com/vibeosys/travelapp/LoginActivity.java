package com.vibeosys.travelapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.vibeosys.travelapp.tasks.BaseActivity;

import org.json.JSONObject;

import java.util.Arrays;


/**
 * Created by mahesh on 10/2/2015.
 */
public class LoginActivity extends BaseActivity {

    //Facebook
    CallbackManager callbackManager;

    public LoginActivity() {
        // Required empty public constructor

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        //Facebook SDK Initialization
        FacebookLoginAPIInit();

        //Facebook Authentication after clicking button
        Button btn_fb_login = (Button) findViewById(R.id.btn_fbLogin);

        btn_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacebookLogin();
            }
        });


    }

    private void FacebookLogin() {
        //Facebook call authentication
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
    }


    private void FacebookLoginAPIInit() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code


                AccessToken at = loginResult.getAccessToken();

                Profile pf = Profile.getCurrentProfile();
                final Uri ur = pf.getProfilePictureUri(150, 150);

                //Getting all details in one short, no need to call individual methods to get details from Facebook
                GraphRequest request = GraphRequest.newMeRequest(at, new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        //Toast.makeText(getApplicationContext(), "Welcome " + pf.getName(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("Profiledetails", object.toString());
                        intent.putExtra("ProfileImg", ur.toString());
                        startActivity(intent);

                        Log.d("JSON Data", object.toString());


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("Vibeosys", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
    }
}
