package com.vibeosys.travelapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vibeosys.travelapp.Adaptors.ShowDestinationCommentsAdaptor;
import com.vibeosys.travelapp.CommentsAndLikes;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.TableDataDTO;
import com.vibeosys.travelapp.data.Upload;
import com.vibeosys.travelapp.data.UploadUser;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class DestinationComments extends BaseActivity implements View.OnClickListener {
    ListView mDestinationCommentListView;
    public static final String MyPREFERENCES = "MyPrefs";
    List<CommentsAndLikes> mListDestination = null;
    NewDataBase newDataBase = null;
    EditText editTextCommentByUser;
    Button submitBtn;
    SharedPreferences sharedPref;
    List<Comment> listComment;
    int DestId;
    String UserId;
    String UserName;
    ShowDestinationCommentsAdaptor showDestinationCommentsAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_comment_listview);
        mDestinationCommentListView = (ListView) findViewById(R.id.commenntlistview);
        submitBtn = (Button) findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(this);
        mListDestination = new ArrayList<>();
        String destName = getIntent().getExtras().getString("DestName");
        setTitle("Comments about " + destName + " by travellers");
        DestId = getIntent().getExtras().getInt("DestId");
        Log.d("DestId Comments", "" + DestId);
        editTextCommentByUser = (EditText) findViewById(R.id.commnetbyUser);
        newDataBase = new NewDataBase(this);
        mListDestination = new ArrayList<>();
        listComment = new ArrayList<>();
        mListDestination = newDataBase.DestinationComments(DestId);

        Log.d("DestinationComment", String.valueOf(mListDestination.size()));
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        UserId = sharedPref.getString("UserId", null);
        UserName = sharedPref.getString("UserName", null);
        showDestinationCommentsAdaptor=new ShowDestinationCommentsAdaptor(getApplicationContext(), mListDestination, DestId);
        mDestinationCommentListView.setAdapter(showDestinationCommentsAdaptor);


    }

    @Override
    public void onClick(View v) {
        int Destinatinid = DestId;
        if (editTextCommentByUser.getText().toString().length() > 0 && editTextCommentByUser.getText().toString() != null) {
            String comment = editTextCommentByUser.getText().toString();
            UploadComment(Destinatinid, UserId, comment);

            Comment comment1 = new Comment();
            comment1.setUserId(UserId);
            comment1.setCommentText(comment);
            comment1.setDestId(DestId);
            listComment.add(comment1);
            newDataBase.insertComment(listComment);
            mListDestination.clear();
            mListDestination = newDataBase.DestinationComments(DestId);
            showDestinationCommentsAdaptor.updateResults(mListDestination);
            editTextCommentByUser.clearComposingText();
           editTextCommentByUser.clearFocus();
            // Toast.makeText(getApplicationContext(), "Enterted comement" + editTextCommentByUser.getText().toString(), Toast.LENGTH_SHORT).show();
        } else {
            editTextCommentByUser.setError("Please Enter some Text");
        }

    }

    public void LoginDialog() {
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        EditText userNameEditText;
        EditText emailidEditText;
        Button loginButton;
        dialog.setContentView(R.layout.login_layout);
        // Set dialog title
        dialog.setTitle("Login");
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        userNameEditText = (EditText) dialog.findViewById(R.id.username);
        emailidEditText = (EditText) dialog.findViewById(R.id.emailid);
        loginButton = (Button) dialog.findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(DestinationComments.this);
    }


    private void UploadComment(int destinatinid, String userId, String comment) {
        Comment comment1 = new Comment();
        comment1.setCommentText(comment);
        comment1.setDestId(destinatinid);
        comment1.setUserId(userId);
        Gson gson = new Gson();

        String SerializedJsonString = gson.toJson(comment1);

        ArrayList<TableDataDTO> tableDataList = new ArrayList<TableDataDTO>();

        tableDataList.add(new TableDataDTO("comment", SerializedJsonString));
        String EmailId = sharedPref.getString("EmailId", null);
        Log.d("EmailId", "" + EmailId);
        String uploadData = gson.toJson(new Upload(new UploadUser(userId, EmailId), tableDataList));

        Log.d("Uploading", uploadData.toString());

        if (NetworkUtils.isActiveNetworkAvailable(this)) {
            super.UploadUserDetails();
            String url = getResources().getString(R.string.URL);
            super.uploadToServer(url + "upload", uploadData, DestinationComments.this);//id 1=>download 2=>upload
            Log.d("Download Calling..", "DownloadUrl:-" + url);

        } else {
            newDataBase.addDataToSync("Comment_and_like", userId, SerializedJsonString);
            LayoutInflater
                    layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.cust_toast, null);
            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setView(view);//setting the view of custom toast layout
            toast.show();

        }

    }
}
