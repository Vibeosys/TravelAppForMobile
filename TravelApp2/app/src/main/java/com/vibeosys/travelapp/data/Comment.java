package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Comment {
private String mUserId[];
private String mDestId[];
private String mCommnetText[];

    public String[] getmUserId() {
        return mUserId;
    }

    public void setmUserId(String[] mUserId) {
        this.mUserId = mUserId;
    }

    public String[] getmDestId() {
        return mDestId;
    }

    public void setmDestId(String[] mDestId) {
        this.mDestId = mDestId;
    }

    public String[] getmCommnetText() {
        return mCommnetText;
    }

    public void setmCommnetText(String[] mCommnetText) {
        this.mCommnetText = mCommnetText;
    }
}
