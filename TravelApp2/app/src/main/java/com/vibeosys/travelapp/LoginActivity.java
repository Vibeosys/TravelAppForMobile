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

     public LoginActivity() {
        // Required empty public constructor

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


    }

    public void OnClickSkipLogin(View view)
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("Profiledetails", "");
        intent.putExtra("ProfileImg", "");
        startActivity(intent);
    }

}
