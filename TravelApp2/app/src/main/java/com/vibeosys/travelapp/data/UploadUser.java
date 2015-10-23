package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/23/2015.
 */
public class UploadUser {
    private String UserId;
    private String EmailId;
public UploadUser(){

}
    public UploadUser(String userId,String emailId){
        this.UserId =userId;
        this.EmailId =emailId;
    }
    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        this.EmailId = emailId;
    }
}
