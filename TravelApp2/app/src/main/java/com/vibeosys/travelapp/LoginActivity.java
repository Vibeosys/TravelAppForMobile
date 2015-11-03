package com.vibeosys.travelapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vibeosys.travelapp.tasks.BaseActivity;


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



}
