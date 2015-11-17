package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/17/2015.
 */
public class Options {
    int mOptionId;
    String mOptionText[];
    int mOptionIds[];
    int mUserCounts[];
    int mUserCount;


    public int getmUserCount() {
        return mUserCount;
    }

    public void setmUserCount(int mUserCount) {
        this.mUserCount = mUserCount;
    }

    public int[] getmUserCounts() {
        return mUserCounts;
    }

    public void setmUserCounts(int[] mUserCounts) {
        this.mUserCounts = mUserCounts;
    }

    public int[] getmOptionIds() {
        return mOptionIds;
    }

    public void setmOptionIds(int[] mOptionIds) {
        this.mOptionIds = mOptionIds;
    }

    public int getmOptionId() {
        return mOptionId;
    }

    public void setmOptionId(int mOptionId) {
        this.mOptionId = mOptionId;
    }

    public String[] getmOptionText() {
        return mOptionText;
    }

    public void setmOptionText(String mOptionText[]) {
        this.mOptionText = mOptionText;
    }
}
