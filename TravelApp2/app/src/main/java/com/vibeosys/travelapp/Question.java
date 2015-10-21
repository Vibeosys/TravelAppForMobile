package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/7/2015.
 */
public class Question {

    private String[] mAnswers;
    private String mQuestion;
    private String mType[];
    private String mQuestionText[];
    private int QuestionId[];


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

    public String[] getmQuestionText() {
        return mQuestionText;
    }

    public void setmQuestionText(String[] mQuestionText) {
        this.mQuestionText = mQuestionText;
    }

    public int[] getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int[] questionId) {
        QuestionId = questionId;
    }


}
