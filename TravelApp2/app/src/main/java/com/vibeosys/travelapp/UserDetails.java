package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/9/2015.
 */
public class UserDetails {
    protected String UserId;
    protected String Username;
    protected int DestId;

    public int getDestId() {
        return DestId;
    }

    public void setDestId(int destId) {
        DestId = destId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
