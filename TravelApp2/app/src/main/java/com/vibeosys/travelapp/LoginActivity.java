package com.vibeosys.travelapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * Created by mahesh on 10/2/2015.
 */
public class LoginActivity extends AppCompatActivity {
    Button login_button;
    static final String URL = "http://192.168.1.142:80/mysql2sqlite/createsqlite";
    protected static String DB_NAME = "TravelApp";
    static final String DB_PATH = "databases";

    // static final String DB_PATH="/data/data/com.vibeosys.travelapp/testdata/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        login_button = (Button) findViewById(R.id.btnLogin);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextWrapper ctw =  new ContextWrapper(getApplicationContext());
                File directory  =  ctw.getDir(DB_PATH, Context.MODE_PRIVATE);
                File internalfile  = new File(directory,DB_NAME);

                if (internalfile.exists()) {
                    Log.d("DBFILE", "Exists" + internalfile.getPath());
                } else {
                    Log.d("DBFILE", "Not Exists");
                }

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}
