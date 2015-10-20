package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Download {
private String mKey[];
private String mValue[];
    enum data{Comment,Like,Answer,Destination,Options,Question,Images};
Download(){

}

    public String[] getmKey() {
        return mKey;
    }

    public void setmKey(String[] mKey) {
        this.mKey = mKey;
    }

    public String[] getmValue() {
        return mValue;
    }

    public void setmValue(String[] mValue) {
        this.mValue = mValue;
    }
}
