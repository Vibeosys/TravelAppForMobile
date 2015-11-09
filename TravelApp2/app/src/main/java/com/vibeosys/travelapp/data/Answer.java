package com.vibeosys.travelapp.data;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mahesh on 10/20/2015.
 */
public class Answer {
    private String answerId;
    private String userId;
    private String destId;
    private String optionId;
    private String createdDate;

    public Answer() {

    }

    public Answer(String optionId) {
        this.optionId = optionId;
    }

    public Answer(String optionId, String DestId, String userId) {
        this.optionId = optionId;
        this.destId = DestId;
        this.userId = userId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String AnswerId) {
        this.answerId = AnswerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String UserId) {
        this.userId = UserId;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String DestId) {
        this.destId = DestId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String OptionId) {
        this.optionId = OptionId;
    }

    public static String serializeString(Answer answer) {
        Gson gson = new Gson();
        Answer answer1 = answer;
        String serializedString = gson.toJson(answer1);
        Log.d("Answer Serialized ", serializedString);
        return serializedString;
    }


    public static List<Answer> deserializeAnswers(List<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Answer> answersList = new ArrayList<>();

        for (String serializedString : serializedStringList) {
            Answer deserizeedAnswer = gson.fromJson(serializedString, Answer.class);
            answersList.add(deserizeedAnswer);
        }
        return answersList;
    }

}
