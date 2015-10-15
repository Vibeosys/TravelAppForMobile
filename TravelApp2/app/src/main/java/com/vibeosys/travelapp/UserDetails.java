package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/9/2015.
 */
public class UserDetails {
    protected int UserId;
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

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }
}
