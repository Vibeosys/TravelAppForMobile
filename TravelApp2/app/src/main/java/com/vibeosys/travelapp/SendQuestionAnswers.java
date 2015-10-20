package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/16/2015.
 */
public class SendQuestionAnswers extends UserDetails{
int mQuestionId;
String mQuestionText;
int mOptionId;
String mOptionText;
    int mOptionIdArr[];
String mOptionTextArr[];
    int mId;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    SendQuestionAnswers(){

    }
    SendQuestionAnswers(int cQuestionId,String cQuestionText){
this.mQuestionId=cQuestionId;
this.mQuestionText=cQuestionText;
    }
    SendQuestionAnswers(int QuestionId,int cOptionId,String cOptionText){
        this.mOptionId=cOptionId;
        this.mQuestionId=QuestionId;
        this.mOptionText=cOptionText;
    }
    SendQuestionAnswers(String cUserId,int DestId,int cQuestionId,int OptionId,String OptionText,String QuestionText){
this.UserId=cUserId;
this.DestId=DestId;
this.mOptionId=OptionId;
this.mOptionText=OptionText;
this.mQuestionId=cQuestionId;
this.mQuestionText=QuestionText;
    }

    public int[] getmOptionIdArr() {
        return mOptionIdArr;
    }

    public void setmOptionIdArr(int[] mOptionIdArr) {
        this.mOptionIdArr = mOptionIdArr;
    }

    SendQuestionAnswers(int id,String cQuestionText,int OptionId[],String cOptionText[]){
  this.mOptionIdArr=OptionId;
  this.mOptionTextArr=cOptionText;
  this.mId=id;
  this.mQuestionText=cQuestionText;
}

    public int getmQuestionId() {
        return mQuestionId;
    }

    public void setmQuestionId(int mQuestionId) {
        this.mQuestionId = mQuestionId;
    }

    public String getmQuestionText() {
        return mQuestionText;
    }

    public void setmQuestionText(String mQuestionText) {
        this.mQuestionText = mQuestionText;
    }

    public int getmOptionId() {
        return mOptionId;
    }

    public void setmOptionId(int mOptionId) {
        this.mOptionId = mOptionId;
    }

    public String getmOptionText() {
        return mOptionText;
    }

    public void setmOptionText(String mOptionText) {
        this.mOptionText = mOptionText;
    }

    public String[] getmOptionTextArr() {
        return mOptionTextArr;
    }

    public void setmOptionTextArr(String[] mOptionTextArr) {
        this.mOptionTextArr = mOptionTextArr;
    }
}
