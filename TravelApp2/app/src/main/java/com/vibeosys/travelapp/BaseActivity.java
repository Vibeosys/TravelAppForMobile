package com.vibeosys.travelapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Base Activity will give the basic implementation with async task support and other things
 */
public class BaseActivity extends AppCompatActivity implements BackgroundTaskCallback{
int id;
    protected void fetchData(final String aServiceUrl, final boolean aShowProgressDlg,int id){
        Log.d("BaseActivity","IN Base");
        this.id=id;
        new BackgroundTask(aShowProgressDlg).execute(aServiceUrl);
    }
    
    @Override
    public void onSuccess(String aData,int id) {
        Log.d("BaseActivity","IN Base");
    }

    @Override
    public void onFailure(String aData,int id) {
        Log.d("BaseActivity","IN Base");
    }


    class BackgroundTask extends AsyncTask<String, Void, String> {

        private boolean mShowProgressDlg;
        private ProgressDialog mProgressDialog;
        public BackgroundTask(boolean aShowProgressDlg){
            mShowProgressDlg = aShowProgressDlg;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if(s != null) {
                onSuccess(s,id);
            }
            else {
                onFailure(null,id);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
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
            if(mShowProgressDlg) {
                mProgressDialog = new ProgressDialog(BaseActivity.this);
                mProgressDialog.show();
            }
        }
    }
}
