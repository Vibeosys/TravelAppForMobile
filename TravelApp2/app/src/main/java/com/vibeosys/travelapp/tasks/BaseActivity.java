package com.vibeosys.travelapp.tasks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vibeosys.travelapp.DestinationUsersImages;
import com.vibeosys.travelapp.GridViewPhotos;
import com.vibeosys.travelapp.QuestionSlidingView;
import com.vibeosys.travelapp.QuestionsFromOthers;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.activities.DestinationComments;
import com.vibeosys.travelapp.data.*;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;

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
    int id;
    NewDataBase newDataBase = null;
    List<Comment> commentList = null;
    List<Like> likeList = null;
    List<Destination> destinationList = null;
    List<User> usersList = null;
    List<Answer> answerList = null;
    List<Images> imagesList = null;
    List<com.vibeosys.travelapp.data.Question> questionsList = null;
    List<Option> optionsList = null;
    SharedPreferences sharedPref;
    public static final String MyPREFERENCES = "MyPrefs";


    public void UploadUserDetails() {
        Gson gson = new Gson();
        sharedPref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String EmailId = sharedPref.getString("EmailId", null);
        String UserId = sharedPref.getString("UserId", null);
        String UserName = sharedPref.getString("UserName", null);
        UploadUser uploadUser = new UploadUser(UserId, EmailId, UserName);
        final String encodedString = gson.toJson(uploadUser);
        RequestQueue rq = Volley.newRequestQueue(this);
        final String url = getResources().getString(R.string.URL);
        Log.d("Encoded String", encodedString);
        rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST,
                url + "updateuser", encodedString, new Response.Listener<JSONObject>() {

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
                    Log.d("JSON Exception", e.toString());
                    Toast.makeText(getBaseContext(),
                            "Error while loadin data!",
                            Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "Error [" + error.getMessage() + "]");
                Toast.makeText(getBaseContext(),
                        "Cannot connect to server", Toast.LENGTH_LONG)
                        .show();
            }

        });

        rq.add(jsonArrayRequest);
    }


    public void fetchData(final String aServiceUrl, final boolean aShowProgressDlg, int id) {
        Log.d("BaseActivity", "IN Base");
        this.id = id;
        new BackgroundTask(aShowProgressDlg).execute(aServiceUrl, String.valueOf(id));
    }

    public void uploadToServer(final String aServiceUrl, String uploadData, Context context) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Please Wait ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        Log.d("Upload To Server", "");
        progress.show();
        RequestQueue rq = Volley.newRequestQueue(this);

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST,
                aServiceUrl, uploadData, new Response.Listener<JSONObject>() {

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
                progress.dismiss();


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "Error [" + error.getMessage() + "]");
                progress.dismiss();
                Toast.makeText(getBaseContext(),
                        "Cannot connect to server", Toast.LENGTH_LONG)
                        .show();
            }

        });

        rq.add(jsonArrayRequest);
    }

    @Override
    public void onSuccess(String aData, int id) {
        newDataBase = new NewDataBase(getApplicationContext());
        if (id == 1) {
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
        if (id == 2) {

        }
    }

    protected void showDestinationInfoDialog(String title, final int cDestId) {
        // Create custom dialog object
        final String titlename = title;
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.cust_dialog);
        // Set dialog title
        dialog.setTitle(title);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newDataBase=new NewDataBase(getApplicationContext());
        int imageCount = this.newDataBase.Images(cDestId, false).size();
        int msgCount = this.newDataBase.MsgCount(cDestId);
        dialog.show();
        Log.d("INDialog", "" + cDestId);
        TextView mCountPhotos = (TextView) dialog.findViewById(R.id.photocounttext);
        mCountPhotos.setText(String.valueOf(imageCount));
        TextView mCountMsgs = (TextView) dialog.findViewById(R.id.item_counter);
        mCountMsgs.setText(String.valueOf(msgCount));
        RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.userphoto);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DestinationUsersImages.class);
                intent.putExtra("DestId", cDestId);
                intent.putExtra("DestName", titlename);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "View Photos...", Toast.LENGTH_SHORT).show();
            }
        });

        RelativeLayout relativeLayout1 = (RelativeLayout) dialog.findViewById(R.id.mymessages);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentphoto = new Intent(getApplicationContext(), QuestionsFromOthers.class);
                intentphoto.putExtra("DestId", cDestId);
                intentphoto.putExtra("DestName", titlename);
                startActivity(intentphoto);
                Toast.makeText(getApplicationContext(), "View Messages...", Toast.LENGTH_SHORT).show();
            }
        });
        Button sendphoto_button = (Button) dialog.findViewById(R.id.senduserButton);
        Button sendmsg_button = (Button) dialog.findViewById(R.id.sendbutton);
        Button usercomments = (Button) dialog.findViewById(R.id.usercommentsButton);
        usercomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(getApplicationContext(), DestinationComments.class);
                theIntent.putExtra("DestId", cDestId);
                theIntent.putExtra("DestName", titlename);
                startActivity(theIntent);
            }
        });

        sendmsg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(getApplicationContext(), QuestionSlidingView.class);
                theIntent.putExtra("DestId", cDestId);
                theIntent.putExtra("DestName", titlename);
                startActivity(theIntent);
                Toast.makeText(getApplicationContext(), "View Messages...", Toast.LENGTH_SHORT).show();
            }
        });
        sendphoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, GridViewPhotos.class);
                intent.putExtra("DestId", cDestId);
                intent.putExtra("DestName", titlename);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFailure(String aData, int id) {
        Log.d("BaseActivity", "IN Base");
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
                onSuccess(s, id);
            } else {
                onFailure(null, id);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                String id = params[1];
                Log.d("Param", id);
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
