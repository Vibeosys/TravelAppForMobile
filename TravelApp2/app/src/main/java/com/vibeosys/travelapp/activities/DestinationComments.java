package com.vibeosys.travelapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

    int DestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_comment_listview);
        mDestinationCommentListView = (ListView) findViewById(R.id.commenntlistview);
        submitBtn = (Button) findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(this);
        mListDestination = new ArrayList<>();
        setTitle("Comments By User");
        DestId = getIntent().getExtras().getInt("DestId");
        Log.d("DestId Comments", "" + DestId);
        editTextCommentByUser = (EditText) findViewById(R.id.commnetbyUser);
        newDataBase = new NewDataBase(this);
        mListDestination = new ArrayList<>();
        mListDestination = newDataBase.DestinationComments(DestId);
        Log.d("DestinationComment", String.valueOf(mListDestination.size()));
        mDestinationCommentListView.setAdapter(new ShowDestinationCommentsAdaptor(getApplicationContext(), mListDestination, DestId));
    }

    @Override
    public void onClick(View v) {
        int Destinatinid = DestId;
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String UserId = sharedPref.getString("UserId", null);
        if (editTextCommentByUser.getText().toString().length() > 0 && editTextCommentByUser.getText().toString() != null) {
            String comment = editTextCommentByUser.getText().toString();
            UploadComment(Destinatinid, UserId, comment);
            finish();
           // Toast.makeText(getApplicationContext(), "Enterted comement" + editTextCommentByUser.getText().toString(), Toast.LENGTH_SHORT).show();
        } else {
            editTextCommentByUser.setError("Please Enter some Text");
        }

    }

    private void UploadComment(int destinatinid, String userId, String comment) {
        Comment comment1 = new Comment();
        comment1.setCommentText(comment);
        comment1.setDestId(destinatinid);
        comment1.setUserId(userId);
        Gson gson = new Gson();

        String SerializedJsonString = gson.toJson(comment1);

        ArrayList<TableDataDTO> tableDataList = new ArrayList<TableDataDTO>();

        tableDataList.add(new TableDataDTO("Comment", SerializedJsonString));

        String uploadData=gson.toJson(new Upload(new UploadUser(userId, "abc@ab.com"), tableDataList));

        Log.d("Uploading",uploadData.toString());

        if (NetworkUtils.isActiveNetworkAvailable(this)) {
            String url = getResources().getString(R.string.URL);
            super.uploadToServer(url + "upload",uploadData );//id 1=>download 2=>upload
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
