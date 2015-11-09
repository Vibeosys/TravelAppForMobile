package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/21/2015.
 */
public class Question extends BaseDTO {
    int questionId;
    String questionText;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public static List<Question> deserializeQuestions(List<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Question> questionsList = new ArrayList<>();

        for (String serializedString : serializedStringList) {
            Question deserizeedQuestions = gson.fromJson(serializedString, Question.class);
            questionsList.add(deserizeedQuestions);
        }
        return questionsList;
    }
}
