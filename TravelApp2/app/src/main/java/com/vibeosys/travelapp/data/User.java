package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class User {
private String userId;
private String userName;
private String emailId;
private String PhotoURL;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
