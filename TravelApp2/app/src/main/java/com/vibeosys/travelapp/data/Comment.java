package com.vibeosys.travelapp.data;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Comment {
    private String userId;
    private int destId;
    private String commentText;

public Comment(){

}




    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDestId() {
        return destId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }


    public static List<Comment> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Comment> commentList=new ArrayList<>();

        for(String serializedString: serializedStringList){
            Comment deserizeedComment= gson.fromJson(serializedString, Comment.class);
            commentList.add(deserizeedComment);
        }
        return commentList;
    }
    public static String serializeString(Comment comment){
        Gson gson = new Gson();
        Comment comment1=comment;
        String serializedString = gson.toJson(comment1);
        Log.d("Comment Serialized ", serializedString);
        return serializedString;
    }

}
