package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anand on 09-11-2015.
 */
public class DbUserDTO extends BaseDTO {

    private String userId;
    private String userName;
    private String photoUrl;

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public static List<DbUserDTO> deserializeUsers(List<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<DbUserDTO> userList = new ArrayList<>();

        for (String serializedString : serializedStringList) {
            DbUserDTO deserizedDestionation = gson.fromJson(serializedString, DbUserDTO.class);
            userList.add(deserizedDestionation);
        }
        return userList;
    }
}
