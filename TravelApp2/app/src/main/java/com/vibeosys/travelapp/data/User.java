package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class User {
private String UserId;
private String UserName;
private String PhotoURL;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.PhotoURL = photoURL;
    }

    public static List<User> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<User> userssList=new ArrayList<>();
        for(String serializedString: serializedStringList){
            User deserizeedUsers= gson.fromJson(serializedString, User.class);
            userssList.add(deserizeedUsers);
        }
        return userssList;
    }

}
