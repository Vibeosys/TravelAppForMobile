package com.vibeosys.travelapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.vibeosys.travelapp.Adaptors.UserLikeAdaptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class UserLikes extends AppCompatActivity {
    ListView mUserLikesListView;
    List<CommentsAndLikes> mUserLikesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlistview);
        mUserLikesListView = (ListView) findViewById(R.id.userlistview);

        mUserLikesList = new ArrayList<>();
        CommentsAndLikes commentsAndLikes = new CommentsAndLikes();
        commentsAndLikes.setUserId("101");
        commentsAndLikes.setUsername("Mahesh");
        commentsAndLikes.setmLikeCount(8);
        CommentsAndLikes commentsAndLikes1 = new CommentsAndLikes();
        commentsAndLikes1.setUserId("102");
        commentsAndLikes1.setUsername("Anand");
        commentsAndLikes1.setmLikeCount(10);
        CommentsAndLikes commentsAndLikes2 = new CommentsAndLikes();
        commentsAndLikes2.setUserId("103");
        commentsAndLikes2.setUsername("Niteen");
        commentsAndLikes2.setmLikeCount(12);
        mUserLikesList.add(commentsAndLikes);
        mUserLikesList.add(commentsAndLikes1);
        mUserLikesList.add(commentsAndLikes2);

        mUserLikesListView.setAdapter(new UserLikeAdaptor(getApplicationContext(), mUserLikesList));

    }


}
