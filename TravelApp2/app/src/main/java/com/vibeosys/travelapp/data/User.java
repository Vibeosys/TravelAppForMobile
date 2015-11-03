package com.vibeosys.travelapp.data;

import com.google.gson.Gson;
import com.vibeosys.travelapp.util.RegistrationSourceTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class User {
    private String mUserId;
    private String mUserName;
    private String mEmailId;
    private String mPhotoURL;
    private String mApiKey;
    private RegistrationSourceTypes mLoginSource;
    private String mThirdPartyUserId;

    public String getEmailId() {
        return mEmailId;
    }

    public void setEmailId(String emailId) {
        this.mEmailId = emailId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getPhotoURL() {
        return mPhotoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.mPhotoURL = photoURL;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public void setApiKey(String mApiKey) {
        this.mApiKey = mApiKey;
    }

    public RegistrationSourceTypes getLoginSource() {
        return mLoginSource;
    }

    public void setLoginSource(RegistrationSourceTypes mLoginSource) {
        this.mLoginSource = mLoginSource;
    }

    public String getThirdPartyUserId() {
        return mThirdPartyUserId;
    }

    public void setThirdPartyUserId(String thirdPartyUserId) {
        mThirdPartyUserId = thirdPartyUserId;
    }


    public User() {
    }

    public User(String userId, String userName, String userEmailId,
                String userPhotoUrl, RegistrationSourceTypes userLoginSource,
                String apiKey, String thirdPartyUserId) {
        this.mApiKey = apiKey;
        this.mUserId = userId;
        this.mUserName = userName;
        this.mPhotoURL = userPhotoUrl;
        this.mEmailId = userEmailId;
        this.mLoginSource = userLoginSource;
        this.mThirdPartyUserId = thirdPartyUserId;
    }

    public static List<User> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<User> userssList = new ArrayList<>();
        for (String serializedString : serializedStringList) {
            User deserizeedUsers = gson.fromJson(serializedString, User.class);
            userssList.add(deserizeedUsers);
        }
        return userssList;
    }

}
