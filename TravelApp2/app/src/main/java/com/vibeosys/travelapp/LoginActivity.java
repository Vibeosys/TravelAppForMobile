package com.vibeosys.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mahesh on 10/2/2015.
 */
public class LoginActivity extends AppCompatActivity{
Button login_button;
    static final String URL="http://192.168.1.142:80/mysql2sqlite/createsqlite";
    protected static String DB_NAME = "TravelApp";
    static final String DB_PATH="/data/data/com.vibeosys.travelapp/databases/";
   // static final String DB_PATH="/data/data/com.vibeosys.travelapp/testdata/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        login_button=(Button)findViewById(R.id.btnLogin);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          UserDetails userDetails=new UserDetails();
               /* userDetails.setUserId(101);
                userDetails.setUsername("Mahesh");*/


File file=new File(DB_PATH+DB_NAME);
     if(file.exists()){
         Log.d("DBFILE","Exists");
     }

                else {
         Log.d("DBFILE","Not Exists");
     }

                Intent i= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }
}
