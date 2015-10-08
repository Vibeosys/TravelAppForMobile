package com.vibeosys.travelapp.data;

/**
 * Created by mahesh on 10/7/2015.
 */
public class Question {

    private String[] mAnswers;
    private String mQuestion;
    private String mType[];


    public String[] getmType() {
        return mType;
    }

    public void setmType(String[] mType) {
        this.mType = mType;
    }



    public Question() {

    }

    public String[] getmAnswers() {
        return mAnswers;
    }

    public void setmAnswers(String[] mAnswers) {
        this.mAnswers = mAnswers;
    }

    public String getmQuestion() {
        return mQuestion;
    }

    public void setmQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }


}
