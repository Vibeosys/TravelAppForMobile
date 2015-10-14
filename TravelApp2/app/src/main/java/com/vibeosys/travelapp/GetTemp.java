package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/10/2015.
 */
public class GetTemp {
int Id;
String DestName;
double Lat;
double Long;
int DestId;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDestName() {
        return DestName;
    }

    public void setDestName(String destName) {
        DestName = destName;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public int getDestId() {
        return DestId;
    }

    public void setDestId(int destId) {
        DestId = destId;
    }
}
