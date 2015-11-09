package com.vibeosys.travelapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vibeosys.travelapp.data.ServerSync;
import com.vibeosys.travelapp.data.Sync;
import com.vibeosys.travelapp.data.SyncDataDTO;
import com.vibeosys.travelapp.data.TableDataDTO;
import com.vibeosys.travelapp.data.TableJsonCollectionDTO;
import com.vibeosys.travelapp.data.TravelAppError;
import com.vibeosys.travelapp.data.Upload;
import com.vibeosys.travelapp.data.UploadUser;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.tasks.BackgroundTaskCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by anand on 07-11-2015.
 */
public class ServerSyncManager
        implements BackgroundTaskCallback {

    private NewDataBase mNewDataBase;
    private SessionManager mSessionManager;
    private Context mContext;
    private boolean mIsWorkInProgress;

    public ServerSyncManager() {

    }

    public ServerSyncManager(Context context, SessionManager sessionManager) {
        mContext = context;
        mSessionManager = sessionManager;
        mNewDataBase = new NewDataBase(mContext, mSessionManager);
    }

    public void syncDataWithServer(final boolean aShowProgressDlg) {
        Log.d("BaseActivity", "IN Base");
        String downloadUrl = mSessionManager.getDownloadUrl(mSessionManager.getUserId());
        String uploadJson = getUploadSyncJson();
        SyncDataDTO syncData = new SyncDataDTO();
        syncData.setDownloadUrl(downloadUrl);
        syncData.setUploadUrl(mSessionManager.getUploadUrl());
        syncData.setUploadJson(uploadJson);
        mIsWorkInProgress = true;
        //this.id = id;
        new BackgroundTask(aShowProgressDlg).execute(syncData);
    }

    public void downloadDataFromServer(final boolean aShowProgressDlg) {
        Log.d("BaseActivity", "IN Base");

        String downloadUrl = mSessionManager.getDownloadUrl(mSessionManager.getUserId());
        SyncDataDTO syncData = new SyncDataDTO();
        syncData.setDownloadUrl(downloadUrl);
        mIsWorkInProgress = true;
        //this.id = id;
        new BackgroundTask(aShowProgressDlg).execute(syncData);
    }

    public void uploadDataToServer(TableDataDTO... params) {
        if (params == null || params.length <= 0) {
            Log.e("UploadNoData", "No data for upload was given by the respective method");
            return;
        }

        final ProgressDialog progress = new ProgressDialog(mContext);
        String uploadJson = prepareUploadJsonFromData(params);
        uploadJsonToServer(uploadJson, progress);
    }

    public void uploadDataToServer(List<TableDataDTO> tableDataList) {
        if (tableDataList == null || tableDataList.size() <= 0) {
            Log.e("UploadNoData", "No data for upload was given by the respective method");
            return;
        }
        TableDataDTO[] tableDataArray = Arrays.copyOf(tableDataList.toArray(), tableDataList.size(), TableDataDTO[].class);
        uploadDataToServer(tableDataArray);
    }

    public boolean isDownloadInProgress() {
        return mIsWorkInProgress;
    }

    private String prepareUploadJsonFromData(TableDataDTO... params) {
        Upload uploadToServer = new Upload();
        uploadToServer.setUser(new UploadUser(
                mSessionManager.getUserId(),
                mSessionManager.getUserEmailId(),
                mSessionManager.getUserName()));
        uploadToServer.setData(Arrays.asList(params));
        String uploadJson = uploadToServer.serializeString();
        return uploadJson;
    }

    private void uploadJsonToServer(String uploadJson, final ProgressDialog progress) {
        RequestQueue vollyRequest = Volley.newRequestQueue(mContext);
        String uploadURL = mSessionManager.getUploadUrl();
        JsonObjectRequest uploadRequest = new JsonObjectRequest(Request.Method.POST,
                uploadURL, uploadJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Upload Response", "" + response.toString());
                try {
                    if (response.getInt("errorCode") == 0) {
                        Toast.makeText(mContext, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getApplicationContext(),"TravelAppError Occoured Please try again",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UPLOADERROR", "TravelAppError [" + error.getMessage() + "]");

                if (progress != null)
                    progress.dismiss();
            }
        });
        uploadRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        vollyRequest.add(uploadRequest);
    }

    protected String getUploadSyncJson() {
        //mNewDataBase = new NewDataBase(mContext);
        Upload uploadToServer = new Upload();
        uploadToServer.setUser(new UploadUser(
                SessionManager.Instance().getUserId(),
                SessionManager.Instance().getUserEmailId()));
        List<Sync> syncRecordsInDb = mNewDataBase.getPendingSyncRecords();
        ArrayList<TableDataDTO> tableDataList = new ArrayList<>();

        for (Sync syncEntry : syncRecordsInDb) {
            tableDataList.add(new TableDataDTO(syncEntry.getTableName(), syncEntry.getJsonSync(), null));
        }
        uploadToServer.setData(tableDataList);

        if (tableDataList.isEmpty()) {
            return null;
        } else {
            String uploadData = new Gson().toJson(uploadToServer);
            return uploadData;
        }
    }

    @Override
    public void onResultReceived(String downloadedJson) {
        //mNewDataBase = new NewDataBase(mContext, SessionManager.getInstance(mContext));
        ServerSync downloadData = null;
        TravelAppError travelAppError = null;
        try {
            //if(new JSONObject(downloadedJson).getString("error"))
            downloadData = new Gson().fromJson(downloadedJson, ServerSync.class);
            travelAppError = new Gson().fromJson(downloadedJson, TravelAppError.class);
        } catch (JsonSyntaxException e) {
            Log.e("JsonSyntaxDwnld", "ServerSync object could not be deserialized, nothing to download" + e.toString());
        }


        if (downloadData == null)
            return;

        if (downloadData.getTableData() == null) {
            //if (travelAppError != null)
            //    Log.e("DownloadErr", "err " + travelAppError.getErrorCode() + " msg " + travelAppError.getMessage());
            return;
        }

        Log.d("TableDataDTO", "" + downloadData.toString());
        //Log.d("TableDataDTO Size", "" + downloadDTO.getTableData().size());
        Hashtable<String, TableJsonCollectionDTO> theTableData = new Hashtable<>();
        for (TableDataDTO tableData : downloadData.getTableData()) {
            String theTableName = tableData.getTableName();
            String theTableValue = tableData.getTableData().replaceAll("\\\\", "");

            TableJsonCollectionDTO tableJsonCollectionDTO;
            if (!theTableData.containsKey(theTableName)) {
                tableJsonCollectionDTO = new TableJsonCollectionDTO();
                theTableData.put(theTableName, tableJsonCollectionDTO);
            }

            if (tableData.getOperation().compareToIgnoreCase("insert") == 0)
                theTableData.get(theTableName).getInsertJsonList().add(theTableValue);

            if (tableData.getOperation().compareToIgnoreCase("update") == 0)
                theTableData.get(theTableName).getUpdateJsonList().add(theTableValue);
        }

        DbOperations dbOperations = new DbOperations(mNewDataBase);

        if (theTableData.containsKey(DbTableNameConstants.DESTINATION)) {
            TableJsonCollectionDTO tableValue = theTableData.get(DbTableNameConstants.DESTINATION);
            dbOperations.addOrUpdateDestinations(tableValue.getInsertJsonList(), tableValue.getUpdateJsonList());
        }

        if (theTableData.containsKey(DbTableNameConstants.USER)) {
            TableJsonCollectionDTO tableValue = theTableData.get(DbTableNameConstants.USER);
            dbOperations.addOrUpdateUsers(tableValue.getInsertJsonList(), tableValue.getUpdateJsonList());
        }

        if (theTableData.containsKey(DbTableNameConstants.QUESTION)) {
            TableJsonCollectionDTO tableValue = theTableData.get(DbTableNameConstants.QUESTION);
            dbOperations.addOrUpdateQuestions(tableValue.getInsertJsonList(), tableValue.getUpdateJsonList());
        }

        if (theTableData.containsKey(DbTableNameConstants.OPTIONS)) {
            TableJsonCollectionDTO tableValue = theTableData.get(DbTableNameConstants.OPTIONS);
            dbOperations.addOrUpdateOptions(tableValue.getInsertJsonList(), tableValue.getUpdateJsonList());
        }

        if (theTableData.containsKey(DbTableNameConstants.IMAGES)) {
            TableJsonCollectionDTO tableValue = theTableData.get(DbTableNameConstants.IMAGES);
            dbOperations.addOrUpdateImages(tableValue.getInsertJsonList());
        }

        if (theTableData.containsKey(DbTableNameConstants.LIKE)) {
            TableJsonCollectionDTO tableValue = theTableData.get(DbTableNameConstants.LIKE);
            dbOperations.addOrUpdateLikes(tableValue.getInsertJsonList(), tableValue.getUpdateJsonList());
        }

        if (theTableData.containsKey(DbTableNameConstants.COMMENT)) {
            TableJsonCollectionDTO tableValue = theTableData.get(DbTableNameConstants.COMMENT);
            dbOperations.addOrUpdateComments(tableValue.getInsertJsonList(), tableValue.getUpdateJsonList());
        }

        if (theTableData.containsKey(DbTableNameConstants.ANSWER)) {
            TableJsonCollectionDTO tableValue = theTableData.get(DbTableNameConstants.ANSWER);
            dbOperations.addOrUpdateAnswers(tableValue.getInsertJsonList());
        }

        mIsWorkInProgress = false;
    }


    class BackgroundTask extends android.os.AsyncTask<SyncDataDTO, Void, String> {

        private boolean mShowProgressDlg;
        private ProgressDialog mProgressDialog = new ProgressDialog(mContext);

        public BackgroundTask(boolean aShowProgressDlg) {
            mShowProgressDlg = aShowProgressDlg;
        }

        @Override
        protected void onPostExecute(String downloadedJson) {
            super.onPostExecute(downloadedJson);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (downloadedJson != null) {
                onResultReceived(downloadedJson);
            }

        }

        @Override
        protected String doInBackground(SyncDataDTO... params) {

            String downloadedJson = null;

            if (params == null || params.length <= 0)
                return downloadedJson;

            try {
                SyncDataDTO syncDataParam = params[0];
                String downloadUrl = syncDataParam.getDownloadUrl();
                URL url = new URL(downloadUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String dataLine = null;

                StringBuffer dataBuffer = new StringBuffer();
                while ((dataLine = br.readLine()) != null) {
                    dataBuffer.append(dataLine);
                }
                uploadJsonToServer(syncDataParam.getUploadJson(), null);
                downloadedJson = dataBuffer.toString();

            } catch (Exception e) {
                Log.e("DownloadUploadErr", "Error in background thread" + e.toString());
            }
            return downloadedJson;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if (mShowProgressDlg) {
                mProgressDialog.show();
            }*/
        }
    }
}
