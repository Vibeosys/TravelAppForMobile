package com.vibeosys.travelapp;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mahesh on 10/14/2015.
 */
public class MyApp extends Application {
static final String URL="http://192.168.1.142:80/mysql2sqlite/createsqlite";
    protected static String DB_NAME = "TravelApp";
    static final String DB_PATH="/data/data/com.vibeosys.travelapp/databases/";
    @Override
    public void onCreate() {
        super.onCreate();
        copyDatabase();
    }

    private void copyDatabase() {
        URL url=null;
        HttpURLConnection urlConnection=null;
        OutputStream myOutput=null;
        byte[] buffer =null;
        InputStream inputStream=null;
        BufferedReader bufferedReader;
        try {
            url = new URL(URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("STATUS", "Request Sended...");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            //  urlConnection.setRequestProperty("Content-Type", "application/");
                urlConnection.setConnectTimeout(10000);
                 urlConnection.setReadTimeout(10000);
            urlConnection.connect();
               /* outputStreamWriter=new OutputStreamWriter(urlConnection.getOutputStream());
              //  outputStreamWriter.write();
                outputStreamWriter.flush();
                outputStreamWriter.close();*/
            int Http_Result=urlConnection.getResponseCode();
            Log.d("RESPONSE CODE", String.valueOf(Http_Result));
            switch (Http_Result){
                case  HttpURLConnection.HTTP_OK:
                    inputStream=urlConnection.getInputStream();
                    buffer = new byte[1024];
                    myOutput = new FileOutputStream(DB_PATH + DB_NAME);
                    int length;
                    while ((length = inputStream.read(buffer)) > 0){
                        myOutput.write(buffer,0,length);
                    }

                    myOutput.flush();
                    myOutput.close();
                    inputStream.close();
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    Log.d("STATUS ","Time Out Occours During Connecting to server..");
                    break;
                case HttpURLConnection.HTTP_BAD_GATEWAY:
                    Log.d("STATUS ","BAD GATEWAY REQUEST ...");
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    Log.d("STATUS ","HTTP INTERNAL ERROR");
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    Log.d("STATUS ","HTTP UNAUTHORIZED.");
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Log.d("STATUS","HTTP NOT FOUND..");
                    break;
                case HttpURLConnection.HTTP_BAD_METHOD:
                    Log.d("STATUS","HTTP_BAD_METHOD");
                    break;

            }


            //  content=stringBuilder.toString();

        } catch (MalformedURLException e1){
            e1.printStackTrace();
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         /*catch (JSONException e) {
            e.printStackTrace();
     /*   }*//* catch (JSONException e) {
                e.printStackTrace();
            }
*/

    }

}

