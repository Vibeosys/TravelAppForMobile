package com.vibeosys.travelapp.activities;

import android.os.Bundle;
import android.util.Log;
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
public class DestinationComments extends BaseActivity {
ListView mDestinationCommentListView;

    List<CommentsAndLikes> mListDestination=null;
    NewDataBase newDataBase=null;
    EditText editTextCommentByUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_comment_listview);
        mDestinationCommentListView=(ListView)findViewById(R.id.commenntlistview);
        mListDestination=new ArrayList<>();
        setTitle("Comments By User");
        int DestId= getIntent().getExtras().getInt("DestId");
        editTextCommentByUser=(EditText)findViewById(R.id.commnetbyUser);
        if(editTextCommentByUser.getText().toString().length()>0&&editTextCommentByUser.getText().toString()!=""){

        }
        newDataBase=new NewDataBase(this);
        mListDestination=new ArrayList<>();
         mListDestination=newDataBase.DestinationComments(DestId);

        Log.d("DestinationComment",String.valueOf(mListDestination.size()));
        mDestinationCommentListView.setAdapter(new ShowDestinationCommentsAdaptor(getApplicationContext(),mListDestination));

    }

}
