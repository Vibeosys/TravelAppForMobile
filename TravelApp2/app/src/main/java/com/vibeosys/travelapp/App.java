package com.vibeosys.travelapp;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mahesh on 10/9/2015.
 */
public class App extends MultiDexApplication {
    private static App mInstance;

    protected static String DB_NAME = "TravelApp";

    public App() {
    }

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        mInstance = this;
        copyDataBase();

    }

    private void copyDataBase() {

        try {


            // Open your local db as the input stream
            InputStream myInput = getAssets().open(DB_NAME);
            // Path to the just created empty db

            File outFileName = getDatabasePath(DB_NAME);
            Log.d("TTTTGA", outFileName + " " + outFileName.list());
            if (!outFileName.exists()) {
                outFileName.getParentFile().mkdirs();
                outFileName.createNewFile();
                // Open the empty db as the output stream
                OutputStream myOutput = new FileOutputStream(outFileName);
                // transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);

                }
                // Close the streams
                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        } catch (IOException e) {
            Log.d("TTTTGA", e + "");
        }
    }

}
