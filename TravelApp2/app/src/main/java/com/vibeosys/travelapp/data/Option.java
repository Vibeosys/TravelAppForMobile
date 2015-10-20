package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Option {
private int mOptionsId[];
private String mOptionText[];
private int mQuestionId[];

    public int[] getmOptionsId() {
        return mOptionsId;
    }

    public void setmOptionsId(int[] mOptionsId) {
        this.mOptionsId = mOptionsId;
    }

    public String[] getmOptionText() {
        return mOptionText;
    }

    public void setmOptionText(String[] mOptionText) {
        this.mOptionText = mOptionText;
    }

    public int[] getmQuestionId() {
        return mQuestionId;
    }

    public void setmQuestionId(int[] mQuestionId) {
        this.mQuestionId = mQuestionId;
    }
}
