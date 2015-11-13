package com.vibeosys.travelapp.data;

/**
 * Created by anand on 13-11-2015.
 */
public class UploadUserOtp extends UploadUser {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UploadUserOtp(String userId, String emailId, String userName, String password) {
        this.userId = userId;
        this.emailId = emailId;
        this.userName = userName;
        this.password = password;
    }
}
