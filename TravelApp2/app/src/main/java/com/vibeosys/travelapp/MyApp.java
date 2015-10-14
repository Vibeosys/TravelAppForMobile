package com.vibeosys.travelapp;

import android.app.Application;

/**
 * Created by mahesh on 10/14/2015.
 */
public class MyApp extends Application
{@Override
        public void onCreate() {
            super.onCreate();
    NewDataBase newDataBase = new NewDataBase(getApplicationContext());
    newDataBase.DeleteTempMaps();
        }
    }

