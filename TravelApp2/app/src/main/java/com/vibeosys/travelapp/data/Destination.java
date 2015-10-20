package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Destination {
int mDestId[];
String mDestName[];
double Lat[];
double Long[];

    public int[] getmDestId() {
        return mDestId;
    }

    public void setmDestId(int[] mDestId) {
        this.mDestId = mDestId;
    }

    public String[] getmDestName() {
        return mDestName;
    }

    public void setmDestName(String[] mDestName) {
        this.mDestName = mDestName;
    }

    public double[] getLat() {
        return Lat;
    }

    public void setLat(double[] lat) {
        Lat = lat;
    }

    public double[] getLong() {
        return Long;
    }

    public void setLong(double[] aLong) {
        Long = aLong;
    }
}
