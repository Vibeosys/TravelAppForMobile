package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/15/2015.
 */
public class DestinationCommentsODA extends UserDetails {
String mImagePath;
String mUserCommnet;

    DestinationCommentsODA(int cDestId,int cUserId,String cUserName,String cImagePath,String cUserComment){
this.UserId=cUserId;
this.DestId=cDestId;
this.Username=cUserName;
this.mImagePath=cImagePath;
this.mUserCommnet=cUserComment;
    }
    DestinationCommentsODA(){

    }
    public String getmImagePath() {
        return mImagePath;
    }

    public void setmImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public String getmUserCommnet() {
        return mUserCommnet;
    }

    public void setmUserCommnet(String mUserCommnet) {
        this.mUserCommnet = mUserCommnet;
    }




}
