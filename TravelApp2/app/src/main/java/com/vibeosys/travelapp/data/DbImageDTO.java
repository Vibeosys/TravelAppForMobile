package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anand on 09-11-2015.
 */
public class DbImageDTO extends BaseDTO {
    private String imageId;
    private String imagePath;
    private int destId;
    private String userId;
    private boolean imageSeen;

    public DbImageDTO() {
    }

    public Boolean getImageSeen() {
        return imageSeen;
    }

    public void setImageSeen(Boolean imageSeen) {
        this.imageSeen = imageSeen;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getDestId() {
        return this.destId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static List<DbImageDTO> deserializeImages(List<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<DbImageDTO> imageList = new ArrayList<>();

        for (String serializedString : serializedStringList) {
            DbImageDTO deserializedImage = gson.fromJson(serializedString, DbImageDTO.class);
            imageList.add(deserializedImage);
        }
        return imageList;
    }
}
