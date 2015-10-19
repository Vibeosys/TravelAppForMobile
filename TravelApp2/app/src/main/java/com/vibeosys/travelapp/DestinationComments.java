package com.vibeosys.travelapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class DestinationComments extends AppCompatActivity {
ListView mDestinationCommentListView;

    List<CommentsAndLikes> mListDestination=null;
    NewDataBase newDataBase=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_comment_listview);
        mDestinationCommentListView=(ListView)findViewById(R.id.commenntlistview);
        mListDestination=new ArrayList<>();
        setTitle("Comments By User");
        int DestId= getIntent().getExtras().getInt("DestId");
        newDataBase=new NewDataBase(this);
        mListDestination=new ArrayList<>();
         mListDestination=newDataBase.DestinationComments(DestId);

        Log.d("DestinationComment",String.valueOf(mListDestination.size()));
        mDestinationCommentListView.setAdapter(new ShowDestinationCommentsAdaptor(getApplicationContext(),mListDestination));

    }

}
