package com.vibeosys.travelapp;

import com.vibeosys.travelapp.data.MyImages;

/**
 * Created by mahesh on 10/14/2015.
 */
public class MyImageDB extends MyImages {
    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    int mId;

    public MyImageDB() {
    }

    public MyImageDB(int cImageId, String cImagePath, String cCreatedDate) {
        this.mImageId = cImageId;
        this.mImagePath = cImagePath;
        this.mCreatedDate = cCreatedDate;

    }
}
