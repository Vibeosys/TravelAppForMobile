package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/16/2015.
 */
public class usersImages extends UserDetails {
    int mImageId;
    String mImagePaths;
 usersImages(int cImageId,String cImagePath,int cDestId,int cUserId){
    this.mImageId= cImageId;
    this.mImagePaths=cImagePath;
    this.UserId=cUserId;
    this.DestId=cDestId;

 }

    public int getmImageId() {
        return mImageId;
    }

    public void setmImageId(int mImageId) {
        this.mImageId = mImageId;
    }

    public String getmImagePaths() {
        return mImagePaths;
    }

    public void setmImagePaths(String mImagePaths) {
        this.mImagePaths = mImagePaths;
    }
}
