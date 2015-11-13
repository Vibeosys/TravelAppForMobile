package com.vibeosys.travelapp.data;

import com.vibeosys.travelapp.util.RegistrationSourceTypes;

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
    private String mPassword;

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

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }


    public User() {
    }

    public User(String userId, String userName, String userEmailId,
                RegistrationSourceTypes userLoginSource,
                String apiKey, String thirdPartyUserId) {
        this(userId, userName, userEmailId, userLoginSource, apiKey, thirdPartyUserId, null);
    }

    public User(String userId, String userName, String userEmailId,
                RegistrationSourceTypes userLoginSource,
                String apiKey, String thirdPartyUserId, String password) {
        this.mApiKey = apiKey;
        this.mUserId = userId;
        this.mUserName = userName;
        //this.mPhotoURL = userPhotoUrl;
        this.mEmailId = userEmailId;
        this.mLoginSource = userLoginSource;
        this.mThirdPartyUserId = thirdPartyUserId;
        this.mPassword = password;
    }

}
