package com.vibeosys.travelapp.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.Answer;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.Destination;
import com.vibeosys.travelapp.data.Download;
import com.vibeosys.travelapp.data.Images;
import com.vibeosys.travelapp.data.Like;
import com.vibeosys.travelapp.data.Option;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.util.ImageFileUploader;
import com.vibeosys.travelapp.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Activity will give the basic implementation with async task support and other things
 */
public abstract class BaseActivity extends AppCompatActivity
        implements BackgroundTaskCallback, ImageFileUploader.OnUploadCompleteListener,
        ImageFileUploader.OnUploadErrorListener {

    protected NewDataBase newDataBase = null;
    protected static SessionManager mSessionManager = null;

    private List<Comment> commentList = null;
    private List<Like> likeList = null;
    private List<Destination> destinationList = null;
    private List<User> usersList = null;
    private List<Answer> answerList = null;
    private List<Images> imagesList = null;
    private List<com.vibeosys.travelapp.data.Question> questionsList = null;
    private List<Option> optionsList = null;
    //private int id;
    protected ProgressDialog mProgressDialog = null;

    protected final static String TAG = "com.vibeosys";


    public void fetchData(String userId, final boolean aShowProgressDlg) {
        Log.d("BaseActivity", "IN Base");
        String downloadUrl = mSessionManager.getDownloadUrl(userId);
        //this.id = id;
        new BackgroundTask(aShowProgressDlg).execute(downloadUrl);
    }

    public void uploadToServer(String uploadData, Context context) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Please Wait ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        Log.d("Upload To Server", "");
        progress.show();
        RequestQueue rq = Volley.newRequestQueue(this);
        String uploadURL = mSessionManager.getUploadUrl();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST,
                uploadURL, uploadData, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Upload Response", "" + response.toString());
                try {
                    if (response.getInt("errorCode") == 0) {
                        Toast.makeText(getApplicationContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getApplicationContext(),"Error Occoured Please try again",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UPLOADERROR", "Error [" + error.getMessage() + "]");
                progress.dismiss();

            }

        });

        rq.add(jsonArrayRequest);
    }

    @Override
    public void onSuccess(String aData) {
        newDataBase = new NewDataBase(getApplicationContext());

        //if (activityStatusType == ActivityStatusTypes.DOWNLOAD_ACTIVITY_STATUS) {
        Log.d("Calling Downloading", "" + aData);
        Gson gson = new Gson();
        BufferedReader bufferedReader = null;
        try {
            Download downloadDTO = gson.fromJson(aData, Download.class);
            Log.d("TableDataDTO", "" + downloadDTO.toString());
            Log.d("TableDataDTO Size", "" + downloadDTO.getData().size());
            HashMap<String, ArrayList<String>> theTableData = new HashMap<>();
            for (int i = 0; i < downloadDTO.getData().size(); i++) {
                String theTableName = downloadDTO.getData().get(i).getTableName();
                String theTableValue = downloadDTO.getData().get(i).getTableData().replaceAll("\\\\", "");
                if (!theTableData.containsKey(theTableName))
                    theTableData.put(theTableName, new ArrayList<>(Arrays.asList(theTableValue)));
                else {
                    theTableData.get(theTableName).add(theTableValue);
                }
            }
            commentList = new ArrayList<>();
            likeList = new ArrayList<>();
            destinationList = new ArrayList<>();
            usersList = new ArrayList<>();
            answerList = new ArrayList<>();
            imagesList = new ArrayList<>();
            questionsList = new ArrayList<>();
            optionsList = new ArrayList<>();
            for (Map.Entry<String, ArrayList<String>> entry : theTableData.entrySet()) {
                String tableName = entry.getKey();

                if (entry.getKey().equals("Comment")) {
                    Log.d("Comment Found", "");
                    commentList = Comment.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Like")) {
                    Log.d("Like Found", "");
                    likeList = Like.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Images")) {
                    Log.d("images Found", "");
                    imagesList = Images.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Option")) {
                    Log.d("Optons Found", "");
                    optionsList = Option.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Answer")) {
                    Log.d("Answers Found", "");
                    answerList = Answer.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Destination")) {
                    Log.d("Destination Found", "");
                    destinationList = Destination.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Question")) {
                    Log.d("Question Found", "");
                    questionsList = com.vibeosys.travelapp.data.Question.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("User")) {
                    Log.d("User Found", "");
                    usersList = User.deserializeSting(entry.getValue());
                }
            }

            if (!commentList.isEmpty()) {
                newDataBase.insertComment(commentList);
            }
            if (!likeList.isEmpty()) {
                newDataBase.insertLikes(likeList);
            }
            if (!destinationList.isEmpty()) {
                newDataBase.insertDestination(destinationList);
            }
            if (!usersList.isEmpty()) {
                newDataBase.insertUsers(usersList);
            }
            if (!answerList.isEmpty()) {
                newDataBase.insertAnswers(answerList);
            }
            if (!imagesList.isEmpty()) {
                newDataBase.insertImages(imagesList);
            }
            if (!questionsList.isEmpty()) {
                newDataBase.insertQuestions(questionsList);
            }
            if (!optionsList.isEmpty()) {
                newDataBase.insertOptions(optionsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(String aData) {
        Log.d("BaseActivity", "IN Base");
    }

    protected static void setProfileInfoInNavigationBar(View view) {
        // After successful Login

        TextView userName = (TextView) view.findViewById(R.id.userName);
        TextView userEmail = (TextView) view.findViewById(R.id.userEmailID);
        ImageView userProfileImage = (ImageView) view.findViewById(R.id.userProfileImage);

        //Setting values from JSON Object
        userName.setText(SessionManager.Instance().getUserName());
        userEmail.setText(SessionManager.Instance().getUserEmailId());

        if (SessionManager.Instance().getUserPhotoUrl() != null || SessionManager.Instance().getUserPhotoUrl() == "") {
            downloadImageAsync(SessionManager.Instance().getUserPhotoUrl(), userProfileImage);
        }
    }

    protected void downloadImgFromFbGPlusAndUploadToAws(final String url) {
        URL fbAvatarUrl = null;
        Bitmap fbAvatarBitmap = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            fbAvatarUrl = new URL(url);
            fbAvatarBitmap = BitmapFactory.decodeStream(fbAvatarUrl.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            Log.e("DownloadImageEx", "Exception while downloading AWS image " + e.toString());
        } catch (IOException e) {
            Log.e("DownloadImageEx", "Exception while downloading AWS image " + e.toString());
        }

        ImageFileUploader imageFileUploader = new ImageFileUploader(getApplicationContext());
        imageFileUploader.setOnUploadCompleteListener(this);
        imageFileUploader.setOnUploadErrorListener(this);
        imageFileUploader.uploadUserProfileImage(fbAvatarBitmap);
    }

    protected static synchronized void downloadImageAsync(final String url, final ImageView imageView) {
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {

            @Override
            public Bitmap doInBackground(Void... params) {
                URL fbAvatarUrl = null;
                Bitmap fbAvatarBitmap = null;
                try {
                    fbAvatarUrl = new URL(url);
                    fbAvatarBitmap = BitmapFactory.decodeStream(fbAvatarUrl.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    Log.e("DownloadImgBkgErr", "Error occurred while downloading profile image in background " + e.toString());
                } catch (IOException e) {
                    Log.e("DownloadImgBkgErr", "Error occurred while downloading profile image in background " + e.toString());
                } catch (Exception e) {
                    Log.e("DownloadImgBkgErr", "Error occurred while downloading profile image in background " + e.toString());
                }
                return fbAvatarBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
            }

        };
        task.execute();
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        private boolean mShowProgressDlg;
        private ProgressDialog mProgressDialog = new ProgressDialog(BaseActivity.this);

        public BackgroundTask(boolean aShowProgressDlg) {
            mShowProgressDlg = aShowProgressDlg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (s != null) {
                onSuccess(s);
            } else {
                onFailure(null);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String dataLine = null;

                StringBuffer dataBuffer = new StringBuffer();
                while ((dataLine = br.readLine()) != null) {
                    dataBuffer.append(dataLine);
                }
                return dataBuffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mShowProgressDlg) {
                mProgressDialog.show();
            }
        }
    }

    @Override
    public void onUploadComplete(String uploadJsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(uploadJsonResponse);
            String imageUrl = jsonObject.getString("message");
            SessionManager.Instance().setUserPhotoUrl(imageUrl);
        } catch (JSONException e) {
            Log.e("ProfileImgUpErr", "JSON exception while uploading the image." + e.toString());
        }

        this.finish();
    }

    @Override
    public void onUploadError(VolleyError error) {

        Log.e("ProfileUploadError", error.toString());
        this.finish();
    }

}
