package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/22/2015.
 */
public class ImageUploadDTO {
String ImageName;
String ImageData;

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageData() {
        return ImageData;
    }

    public void setImageData(String imageData) {
        ImageData = imageData;
    }
    public static String serializedString(String string){
        return string;
    }
}
