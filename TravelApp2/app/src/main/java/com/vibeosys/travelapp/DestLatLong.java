package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/3/2015.
 */
public class DestLatLong {
    private String mDestName;

    public String getmDestName() {
        return mDestName;
    }

    public void setmDestName(String mDestName) {
        this.mDestName = mDestName;
    }

    private double latitude;
    private double longitude;

    DestLatLong() {

    }

    DestLatLong(double lat, double longi) {
        latitude = lat;
        longitude = longi;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}
