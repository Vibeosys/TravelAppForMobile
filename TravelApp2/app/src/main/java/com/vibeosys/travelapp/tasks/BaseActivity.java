package com.vibeosys.travelapp.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.vibeosys.travelapp.MainActivity;
import com.vibeosys.travelapp.data.Answer;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.Destination;
import com.vibeosys.travelapp.data.Download;
import com.vibeosys.travelapp.data.Images;
import com.vibeosys.travelapp.data.Like;
import com.vibeosys.travelapp.data.Option;
import com.vibeosys.travelapp.data.UploadUser;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Activity will give the basic implementation with async task support and other things
 */
public abstract class BaseActivity extends AppCompatActivity implements BackgroundTaskCallback {

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
    //Facebook
    protected CallbackManager callbackManager;

    public void UploadUserDetails() {
        Gson gson = new Gson();
        String UserId = mSessionManager.getUserId();
        String EmailId = mSessionManager.getUserEmailId();

        String UserName = mSessionManager.getUserName();
        UploadUser uploadUser = new UploadUser(UserId, EmailId, UserName);

        final String encodedString = gson.toJson(uploadUser);
        RequestQueue rq = Volley.newRequestQueue(this);
        String updateUsersDetailsUrl = mSessionManager.getUpdateUserDetails();
        Log.d("Encoded String", encodedString);
        rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST,
                updateUsersDetailsUrl, encodedString, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jresponse = response;//.getJSONObject(0);
                    Log.d("UploadUserDetails", jresponse + "");
                    String res = jresponse.getString("message");
                    String code = jresponse.getString("errorCode");
                    if (code.equals("0")) {
                        //  JSONObject json = new JSONObject(response);
                        Toast.makeText(getBaseContext(),
                                "Updated Successfully", Toast.LENGTH_SHORT)
                                .show();
                    }

                    if (code.equals("100")) {
                        Log.e("Error", "User Not Authenticated..");
                    }
                    if (code.equals("101")) {
                        Log.e("Error", "User Id is Blanck");
                    }
                    if (code.equals("102")) {
                        Log.e("Error", "Unknown Error");
                    }


                } catch (JSONException e) {
                    Log.e("JSON Exception", e.toString());

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UPLOADUSERDETAILSERROR", "Error [" + error.getMessage() + "]");
            }

        });

        rq.add(jsonArrayRequest);
    }


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


    public void FacebookLogin(final Activity act) {
        //Facebook call authentication
        LoginManager.getInstance().logInWithReadPermissions(act, Arrays.asList("public_profile", "user_friends", "email"));
    }


    public void FacebookLoginAPIInit(final Context cx) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code


                AccessToken at = loginResult.getAccessToken();

                Profile pf = Profile.getCurrentProfile();
                final Uri ur = pf.getProfilePictureUri(150, 150);

                //Getting all details in one short, no need to call individual methods to get details from Facebook
                GraphRequest request = GraphRequest.newMeRequest(at, new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        //Toast.makeText(getApplicationContext(), "Welcome " + pf.getName(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(cx, MainActivity.class);
                        intent.putExtra("Profiledetails", object.toString());
                        intent.putExtra("ProfileImg", ur.toString());
                        startActivity(intent);

                        Log.d("JSON Data", object.toString());


                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,birthday,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("Vibeosys", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
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
                //String id = params[1];
                //Log.d("Param", id);
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

}
