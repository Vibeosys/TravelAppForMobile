package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Destination {
int DestId;
String DestName;
double Lat;
double Long;

    public int getDestId() {
        return DestId;
    }

    public void setDestId(int mDestId) {
        this.DestId = DestId;
    }

    public String getDestName() {
        return DestName;
    }

    public void setDestName(String DestName) {
        this.DestName = DestName;
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

    public static List<Destination> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Destination> destinationList=new ArrayList<>();

        for(String serializedString: serializedStringList){
            Destination deserizedDestionation= gson.fromJson(serializedString, Destination.class);
            destinationList.add(deserizedDestionation);
        }
        return destinationList;
    }

}
