package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/14/2015.
 */
public class DestinationTempData extends Destination {
    int mTempId;

    public DestinationTempData() {

    }

    public DestinationTempData(int cTempId, int cDestId, double cLat, double cLong, String cDestName) {
        this.latitude = cLat;
        this.longitude = cLong;
        this.destName = cDestName;
        this.destId = cDestId;
        this.mTempId = cTempId;
    }

    public int getmTempId() {
        return mTempId;
    }

    public void setmTempId(int mTempId) {
        this.mTempId = mTempId;
    }
}
