package com.vibeosys.travelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by mahesh on 10/20/2015.
 */
public class DBDownload extends AsyncTask<String, Void, String>{
private Context mContext;
UUID uuid;
    DBDownload(Context context){
        mContext=context;
    }
        private boolean mShowProgressDlg;
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if(s != null) {
                onSuccess(s);
            }
            else {
                onFailure(null);
            }

        }

    private void onFailure(String o) {

    }

    private void onSuccess(String s) {

    }

    @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                uuid= UUID.randomUUID();
                String data = URLEncoder.encode("UserId", "UTF-8")
                        + "=" + URLEncoder.encode(String.valueOf(uuid), "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write( data );
                wr.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String dataLine = null;
                StringBuffer dataBuffer = new StringBuffer();
                while ((dataLine = br.readLine()) != null){
                    dataBuffer.append(dataLine);
                }
                return  dataBuffer.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.show();


    }
}
