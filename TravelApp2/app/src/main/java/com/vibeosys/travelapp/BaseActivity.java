package com.vibeosys.travelapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mahesh on 10/15/2015.
 */
public class BaseActivity  extends AsyncTask<String,Void,String> {
    protected static String DB_NAME = "TravelApp";



        @Override
        protected void onPostExecute(String s) {

            if(s!=null) Log.d("RESPONSE RECEIVED...", s);



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection;
            StringBuilder stringBuilder=new StringBuilder();

            BufferedReader bufferedReader;
            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                Log.d("STATUS", "Request Sended...");
               urlConnection.setDoOutput(true);
                //urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                //urlConnection.setRequestMethod("POST");
              //  urlConnection.setRequestProperty("Content-Type", "application/");
            //    urlConnection.setConnectTimeout(10000);
           //     urlConnection.setReadTimeout(10000);
                urlConnection.connect();
               /* outputStreamWriter=new OutputStreamWriter(urlConnection.getOutputStream());
              //  outputStreamWriter.write();
                outputStreamWriter.flush();
                outputStreamWriter.close();*/
                int Http_Result=urlConnection.getResponseCode();
                Log.d("RESPONSE CODE", String.valueOf(Http_Result));
                switch (Http_Result){
                    case  HttpURLConnection.HTTP_OK:
                        bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line=null;
                        while ((line=bufferedReader.readLine())!=null){
                            stringBuilder.append(line);
                        }
                        bufferedReader.close();
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
            } catch (IOException e) {
                e.printStackTrace();
            } /*catch (JSONException e) {
            e.printStackTrace();
     /*   }*//* catch (JSONException e) {
                e.printStackTrace();
            }
*/

            return null;
        }
    }


