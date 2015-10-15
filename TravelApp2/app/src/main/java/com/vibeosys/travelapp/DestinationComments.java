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
List<DestinationCommentsODA> mListDestination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_comment_listview);
        mDestinationCommentListView=(ListView)findViewById(R.id.commenntlistview);
        mListDestination=new ArrayList<>();
        setTitle("Comments By User");
        DestinationCommentsODA destinationCommentsODA=new DestinationCommentsODA();
         destinationCommentsODA.setmUserCommnet("It was nice experience");
         destinationCommentsODA.setUsername("Anand");
         destinationCommentsODA.setUserId(101);
        DestinationCommentsODA destinationCommentsODA1=new DestinationCommentsODA();
        destinationCommentsODA1.setmUserCommnet("It was bad experience");
        destinationCommentsODA1.setUsername("Mahesh");
        destinationCommentsODA1.setUserId(102);
        DestinationCommentsODA destinationCommentsODA2=new DestinationCommentsODA();
        destinationCommentsODA2.setmUserCommnet("Amaze. experience");
        destinationCommentsODA2.setUsername("Niteen");
        destinationCommentsODA2.setUserId(103);
        mListDestination.add(destinationCommentsODA);
        mListDestination.add(destinationCommentsODA1);
        mListDestination.add(destinationCommentsODA2);
        Log.d("DestinationComment",String.valueOf(mListDestination.size()));
        mDestinationCommentListView.setAdapter(new ShowDestinationCommentsAdaptor(getApplicationContext(),mListDestination));

    }

}
