package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Like extends BaseDTO {
    private String userId;
    private int destId;
    private int likeCount;

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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public static List<Like> deserializeLikes(List<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Like> likeList = new ArrayList<>();

        for (String serializedString : serializedStringList) {
            Like deserializedComment = gson.fromJson(serializedString, Like.class);
            likeList.add(deserializedComment);
        }
        return likeList;
    }
}
