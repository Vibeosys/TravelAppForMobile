package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/16/2015.
 */
public class usersImages extends UserDetails {
    String mImageId;
    String mImagePaths;

    public usersImages(String cImageId, String cImagePath, int cDestId, String cUserId, String userName) {
        this.mImageId = cImageId;
        this.mImagePaths = cImagePath;
        this.UserId = cUserId;
        this.DestId = cDestId;
        this.Username = userName;
    }

    public String getmImageId() {
        return mImageId;
    }

    public void setmImageId(String mImageId) {
        this.mImageId = mImageId;
    }

    public String getmImagePaths() {
        return mImagePaths;
    }

    public void setmImagePaths(String mImagePaths) {
        this.mImagePaths = mImagePaths;
    }
}
