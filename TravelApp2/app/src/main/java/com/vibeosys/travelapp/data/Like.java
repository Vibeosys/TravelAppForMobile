package com.vibeosys.travelapp.data;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Like {
    private String userId;
    private int destId;

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

    public static List<Like> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Like> likeList=new ArrayList<>();

        for(String serializedString: serializedStringList){
            Like deserizeedLike= gson.fromJson(serializedString, Like.class);
            likeList.add(deserizeedLike);
        }
        return likeList;
    }
    public static String serializeString(Like likeObj){
        Gson gson = new Gson();
        Like likeObj1=likeObj;
        String serializedString = gson.toJson(likeObj1);
        Log.d("Like Serialized ", serializedString);
        return serializedString;
    }

}
