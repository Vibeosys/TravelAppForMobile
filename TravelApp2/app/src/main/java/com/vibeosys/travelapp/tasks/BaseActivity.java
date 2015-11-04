package com.vibeosys.travelapp.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.vibeosys.travelapp.MainActivity;
import com.vibeosys.travelapp.data.Answer;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.Destination;
import com.vibeosys.travelapp.data.Download;
import com.vibeosys.travelapp.data.Images;
import com.vibeosys.travelapp.data.Like;
import com.vibeosys.travelapp.data.Option;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.util.RegistrationSourceTypes;
import com.vibeosys.travelapp.util.SessionManager;
import com.vibeosys.travelapp.util.UserAuth;

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
public abstract class BaseActivity extends AppCompatActivity
        implements BackgroundTaskCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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

    //Google Plus
    GoogleApiClient mGoogleApiClient;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 1;
    final static String TAG = "com.vibeosys";
    private Activity mFromactivitycall;





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


    //-------------Facebook API calls--------------------------------//
    public void FacebookLogin(final Activity act) {
        //Facebook call authentication
        LoginManager.getInstance().logInWithReadPermissions(act, Arrays.asList("public_profile", "user_friends", "email"));
    }

    public void FacebookLogout() {
        try {
            LoginManager.getInstance().logOut();

            //Clean Authentication data from share preference
            UserAuth.CleanAuthenticationInfo();

            Toast.makeText(getApplicationContext(), "You have successfully logged out", Toast.LENGTH_SHORT);
        }
        catch (Exception ex) {
            Log.e("Facebook Logout:",ex.getMessage());
        }
    }


    public void FacebookLoginAPIInit(final Context cx) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                final AccessToken facebookAppToken = loginResult.getAccessToken();
                //Getting all details in one short, no need to call individual methods to get details from Facebook
                GraphRequest request = GraphRequest.newMeRequest(facebookAppToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Profile facebookProfile = Profile.getCurrentProfile();
                        if (facebookProfile == null) {
                            Log.e("Login", "facebook Profile is null");
                            Toast.makeText(getApplicationContext(), "Facebook application is not installed", Toast.LENGTH_SHORT);
                            return;
                        }

                        final Uri profilePic = facebookProfile.getProfilePictureUri(150, 150);
                        User theUser = null;

                        try {
                            theUser = new User(SessionManager.Instance().getUserId(),
                                    object.getString("name"),
                                    object.getString("email"),
                                    profilePic.toString(),
                                    RegistrationSourceTypes.FACEBOOK,
                                    facebookAppToken.getToken(),
                                    object.getString("id"));
                        } catch (JSONException ex) {
                            Log.e("LoginFacebook", "JSON exception from facebook data deserialization");
                        }

                        boolean userAdded = UserAuth.saveAuthenticationInfo(theUser, getApplicationContext());
                        if (!userAdded) {
                            Toast.makeText(getApplicationContext(), "User is not Added successfully", Toast.LENGTH_SHORT);
                            Log.e("LoginFacebook", "User is not Added successfully " + object.toString());
                        }
                        Intent intent = new Intent(cx, MainActivity.class);
                        /*intent.putExtra("Profiledetails", object.toString());
                        intent.putExtra("ProfileImg", profilePic.toString());*/
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

    //-------------Google API Call----------------------------------//

    protected void GooglePlusLogin(Activity act) {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();
        mFromactivitycall = act;

        // Show a message to the user that we are signing in.
        Toast.makeText(this, "Signing in", Toast.LENGTH_SHORT).show();
    }

    protected void GooglePlusLogout() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        try {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();

                //Clean Authentication data from share preference
                UserAuth.CleanAuthenticationInfo();

                Toast.makeText(getApplicationContext(), "You have successfully logged out", Toast.LENGTH_SHORT);
            }
        }
        catch (Exception ex) {
            Log.e("Google Logout:",ex.getMessage());
        }


        // Show a message to the user that we are signing in.
        Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        try {

            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson == null) {
                Log.e("LoginGoogle+", "Error while getting data from Google+");
                return;
            }
            String name = currentPerson.getDisplayName();
            String imageURL = currentPerson.getImage().getUrl();

            //changing the default size of image which API return i.e 50 X 50
            imageURL = imageURL.substring(0,
                    imageURL.length() - 2)
                    + 150;

            String googleEmailId = Plus.AccountApi.getAccountName(mGoogleApiClient);

            User theUser = new User(SessionManager.Instance().getUserId(),
                    currentPerson.getDisplayName(),
                    googleEmailId,
                    currentPerson.getImage().getUrl(),
                    RegistrationSourceTypes.GOOGLE_PLUS,
                    null,
                    currentPerson.getId());

            Boolean userAdded = UserAuth.saveAuthenticationInfo(theUser, getApplicationContext());
            if (!userAdded) {
                Toast.makeText(getApplicationContext(), "User is not Added successfully", Toast.LENGTH_SHORT);
                Log.e("LoginGoogle+", "User is not Added successfully ");
            }
            JSONObject object = new JSONObject();


            object.put("name", name);
            object.put("email", googleEmailId);


            Log.d(TAG, ":" + name);
            Log.d(TAG, ":" + imageURL);

            //Redirect to Page once Authenticatied

            Intent intent = new Intent(mFromactivitycall, MainActivity.class);
            //Send Data or Save Data
            intent.putExtra("Profiledetails", object.toString());
            intent.putExtra("ProfileImg", imageURL);

            startActivity(intent);

        } catch (JSONException ex) {
            Log.d(TAG, "Error:" + ex.getMessage());
        }


        // Show a message to the user that we are signing in.
        //Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(this, connectionResult.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show the signed-out UI
            Toast.makeText(this, "Signout", Toast.LENGTH_SHORT).show();
        }

    }

    //------------------------------------------------------------------------//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Facebook call to get Data
        callbackManager.onActivityResult(requestCode, resultCode, data);


        //Google-Plus login Data
        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }

        Log.d("Vibeosys", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

    }

    public void GooglePlusAPIInit() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
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
