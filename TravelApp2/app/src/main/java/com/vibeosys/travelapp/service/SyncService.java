package com.vibeosys.travelapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SyncService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncService(String name) {
        super(name);
    }

    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //msg.obj;
        }
    };

    @Override
    protected void onHandleIntent(Intent intent) {


        Thread theThread = new Thread(new Runnable() {

            @Override
            public void run() {

                URL url = null;
                try {
                    //mIsSyncRunning = true;
                    url = new URL("");
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.getInputStream();
                    JSONObject theJson = new JSONObject("");
                    Message msg = new Message();
                    msg.obj = "";
                    mHandler.sendMessage(msg);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e){

                }
                catch (JSONException e){

                }



            }
        });

    }

}
