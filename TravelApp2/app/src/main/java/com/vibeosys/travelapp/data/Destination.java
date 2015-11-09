package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Destination extends BaseDTO {
    private int destId;
    private String destName;
    private double latitude;
    private double longitude;

    public int getDestId() {
        return destId;
    }

    public void setDestId(int mDestId) {
        this.destId = destId;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String DestName) {
        this.destName = DestName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double aLong) {
        longitude = aLong;
    }

    public static List<Destination> deserializeDestinations(List<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Destination> destinationList = new ArrayList<>();

        for (String serializedString : serializedStringList) {
            Destination deserizedDestionation = gson.fromJson(serializedString, Destination.class);
            destinationList.add(deserizedDestionation);
        }
        return destinationList;
    }

}
