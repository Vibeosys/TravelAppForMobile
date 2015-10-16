package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/9/2015.
 */
public class Destination {

    protected int mDestId;
    protected String mDestName;
    protected double mLat;
    protected double mLong;
    Destination(int cDestId,String cDestName,double cLat,double cLong){
        mDestId=cDestId;
        mDestName=cDestName;
        mLat=cLat;
        mLong=cLong;
    }
Destination(){

}
    Destination(int cDestId,String cDestName){

    }
    public String getmDestName() {
        return mDestName;
    }

    public void setmDestName(String mDestName) {
        this.mDestName = mDestName;
    }

    public int getmDestId() {
        return mDestId;
    }

    public void setmDestId(int mDestId) {
        this.mDestId = mDestId;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLong() {
        return mLong;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }

}
