package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/20/2015.
 */
public class User {
private String mUserId[];
private String mUserName[];
private String mPhotoURL[];

    public String[] getmUserId() {
        return mUserId;
    }

    public void setmUserId(String[] mUserId) {
        this.mUserId = mUserId;
    }

    public String[] getmUserName() {
        return mUserName;
    }

    public void setmUserName(String[] mUserName) {
        this.mUserName = mUserName;
    }

    public String[] getmPhotoURL() {
        return mPhotoURL;
    }

    public void setmPhotoURL(String[] mPhotoURL) {
        this.mPhotoURL = mPhotoURL;
    }
}
