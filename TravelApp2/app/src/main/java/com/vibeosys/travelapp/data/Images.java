package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Images extends UserDetails {
    private String imageId;
    private String imagePath;
    private int destId;
    private boolean imageSeen;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    int likeCount;

    Images() {

    }


    public Images(String userName, int likeCount, int destId, String userId) {
        this.Username = userName;
        this.likeCount = likeCount;
        this.UserId = userId;
        this.destId = destId;
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
}
