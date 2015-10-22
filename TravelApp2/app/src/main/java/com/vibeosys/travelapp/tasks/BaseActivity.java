package com.vibeosys.travelapp.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.vibeosys.travelapp.data.Answer;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.Destination;
import com.vibeosys.travelapp.data.Download;
import com.vibeosys.travelapp.data.Images;
import com.vibeosys.travelapp.data.Like;
import com.vibeosys.travelapp.data.Option;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;

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
public class BaseActivity extends AppCompatActivity implements BackgroundTaskCallback {
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

    public void fetchData(final String aServiceUrl, final boolean aShowProgressDlg, int id) {
        Log.d("BaseActivity", "IN Base");
        this.id = id;
        new BackgroundTask(aShowProgressDlg).execute(aServiceUrl, String.valueOf(id));
    }


    @Override
    public void onSuccess(String aData, int id) {
        newDataBase = new NewDataBase(getApplicationContext());
        if (id == 1) {
            Log.d("Calling Downloading",""+aData);
            Gson gson = new Gson();
            BufferedReader bufferedReader = null;
            try {
               /* bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("file.json")));*/
            Download downloadDTO = gson.fromJson(aData, Download.class);
            Log.d("TableDataDTO", "" + downloadDTO.toString());
            Log.d("TableDataDTO Size",""+downloadDTO.getData().size());
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
                    Log.d("Comment Found","");
                    commentList = Comment.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Like")) {
                    Log.d("Like Found","");
                    likeList = Like.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Images")) {
                    Log.d("images Found","");
                    imagesList = Images.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Option")) {
                    Log.d("Optons Found","");
                    optionsList = Option.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Answer")) {
                    Log.d("Answers Found","");
                    answerList=Answer.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Destination")) {
                    Log.d("Destination Found","");
                    destinationList=Destination.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("Question")) {
                    Log.d("Question Found","");
                    questionsList=com.vibeosys.travelapp.data.Question.deserializeSting(entry.getValue());
                }
                if (entry.getKey().equals("User")) {
                    Log.d("User Found","");
                    usersList=User.deserializeSting(entry.getValue());
                }
            }

            if(!commentList.isEmpty()) {
                newDataBase.insertComment(commentList);
            }
            if(!likeList.isEmpty()) {
                newDataBase.insertLikes(likeList);
            }
            if(!destinationList.isEmpty()) {
                newDataBase.insertDestination(destinationList);
            }
            if(!usersList.isEmpty()) {
                newDataBase.insertUsers(usersList);
            }
            if(!answerList.isEmpty()) {
                newDataBase.insertAnswers(answerList);
            }
            if(!imagesList.isEmpty()) {
                newDataBase.insertImages(imagesList);
            }
            if(!questionsList.isEmpty()) {
                newDataBase.insertQuestions(questionsList);
            }
            if(!optionsList.isEmpty()) {
                newDataBase.insertOptions(optionsList);
            }
            } catch (Exception e){
                e.printStackTrace();
            }


        }
        if (id == 2) {

        }
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
                String id=params[1];
                 Log.d("Param",id);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                if(id.equals("2")){

                }
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
