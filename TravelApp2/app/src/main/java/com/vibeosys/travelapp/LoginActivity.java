package com.vibeosys.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by mahesh on 10/2/2015.
 */
public class LoginActivity extends AppCompatActivity{
Button login_button;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        login_button=(Button)findViewById(R.id.btnLogin);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          UserDetails userDetails=new UserDetails();
                userDetails.setUserId(101);
                userDetails.setUsername("Mahesh");
                Intent i= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }
}
