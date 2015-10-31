package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/31/2015.
 */
public class UserLikeDTO {
    private String userId;
    private String userName;
    private int userLikeCount;

    public int getUserLikeCount() {
        return userLikeCount;
    }

    public void setUserLikeCount(int userLikeCount) {
        this.userLikeCount = userLikeCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
