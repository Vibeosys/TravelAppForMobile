package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/21/2015.
 */
public class Question {
int QuestionId;
String QuestionText;

    public int getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int questionId) {
        QuestionId = questionId;
    }

    public String getQuestionText() {
        return QuestionText;
    }

    public void setQuestionText(String questionText) {
        QuestionText = questionText;
    }

    public static List<Question> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Question> questionsList=new ArrayList<>();

        for(String serializedString: serializedStringList){
            Question deserizeedQuestions= gson.fromJson(serializedString, Question.class);
            questionsList.add(deserizeedQuestions);
        }
        return questionsList;
    }
}
