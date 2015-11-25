package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/17/2015.
 */
public class Options {
    int mOptionId;
    String mOptionText[];
    int mOptionIds[];
    long mUserCounts[];
    int mUserCount;


    public int getmUserCount() {
        return mUserCount;
    }

    public void setmUserCount(int mUserCount) {
        this.mUserCount = mUserCount;
    }

    public long[] getmUserCounts() {
        return mUserCounts;
    }

    public void setmUserCounts(long[] mUserCounts) {
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

    public String getOptionText(int index) {
        return mOptionText[index];
    }

    public int getOptionId(int index) {
        return mOptionIds[index];
    }

    public void setmOptionText(String mOptionText[]) {
        this.mOptionText = mOptionText;
    }
}
