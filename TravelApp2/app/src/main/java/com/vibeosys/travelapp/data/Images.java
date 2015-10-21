package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Images {
String ImageId;
String ImagePath;
String UserId;
int DestId;
Boolean ImageSeen;

    public Boolean getImageSeen() {
        return ImageSeen;
    }

    public void setImageSeen(Boolean imageSeen) {
        ImageSeen = imageSeen;
    }

    public String getImageId() {
        return ImageId;
    }

    public void setImageId(String imageId) {
        this.ImageId = imageId;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        this.ImagePath = imagePath;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public int getDestId() {
        return DestId;
    }

    public void setDestId(int destId) {
        this.DestId = destId;
    }

    public static List<Images> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Images> imagesList=new ArrayList<>();

        for(String serializedString: serializedStringList){
            Images deserizeedImages= gson.fromJson(serializedString, Images.class);
            imagesList.add(deserizeedImages);
        }
        return imagesList;
    }
}
