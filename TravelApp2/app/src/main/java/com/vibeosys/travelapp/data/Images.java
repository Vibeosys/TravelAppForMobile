package com.vibeosys.travelapp.data;

import com.google.gson.Gson;
import com.vibeosys.travelapp.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Images extends UserDetails{
String ImageId;
String ImagePath;
int DestId;
Boolean ImageSeen;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    int likeCount;
Images(){

}



    public Images(String userName, int likeCount,int destId,String userId){
        this.Username=userName;
        this.likeCount=likeCount;
        this.UserId=userId;
        this.DestId=destId;
    }

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

    public int getDestId() {
        return this.DestId;
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
