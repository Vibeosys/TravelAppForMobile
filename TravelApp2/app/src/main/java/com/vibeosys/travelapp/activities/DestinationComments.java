package com.vibeosys.travelapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.vibeosys.travelapp.Adaptors.ShowDestinationCommentsAdaptor;
import com.vibeosys.travelapp.CommentsAndLikes;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.tasks.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class DestinationComments extends BaseActivity implements View.OnClickListener{
ListView mDestinationCommentListView;

    List<CommentsAndLikes> mListDestination=null;
    NewDataBase newDataBase=null;
    EditText editTextCommentByUser;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_comment_listview);
        mDestinationCommentListView=(ListView)findViewById(R.id.commenntlistview);
        submitBtn=(Button)findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(this);
        mListDestination=new ArrayList<>();
        setTitle("Comments By User");
        int DestId= getIntent().getExtras().getInt("DestId");
        editTextCommentByUser=(EditText)findViewById(R.id.commnetbyUser);
        newDataBase=new NewDataBase(this);
        mListDestination=new ArrayList<>();

         mListDestination=newDataBase.DestinationComments(DestId);
        Log.d("DestinationComment",String.valueOf(mListDestination.size()));
        mDestinationCommentListView.setAdapter(new ShowDestinationCommentsAdaptor(getApplicationContext(),mListDestination,DestId));
    }

    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.commnetbyUser:
        if(editTextCommentByUser.getText().toString().length()>0&&editTextCommentByUser.getText().toString()!="")
        {

        }

        break;
}
        }
}
