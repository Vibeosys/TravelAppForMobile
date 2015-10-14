package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/14/2015.
 */
public class MyImages {
protected int mImageId;
    protected String mImagePath;
    protected String mCreatedDate;
    protected int mUserId;
    protected int mDestId;
    protected boolean mImageSeen;

    public int getmUserId() {
        return mUserId;
    }

    public void setmUserId(int mUserId) {
        this.mUserId = mUserId;
    }

    public int getmDestId() {
        return mDestId;
    }

    public void setmDestId(int mDestId) {
        this.mDestId = mDestId;
    }

    public boolean ismImageSeen() {
        return mImageSeen;
    }

    public void setmImageSeen(boolean mImageSeen) {
        this.mImageSeen = mImageSeen;
    }

    public int getmImageId() {
        return mImageId;
    }

    public void setmImageId(int mId) {
        this.mImageId = mId;
    }

    public String getmImagePath() {
        return mImagePath;
    }

    public void setmImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public String getmCreatedDate() {
        return mCreatedDate;
    }

    public void setmCreatedDate(String mCreatedDate) {
        this.mCreatedDate = mCreatedDate;
    }
}
