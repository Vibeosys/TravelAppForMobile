package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Option extends BaseDTO {
    private String optionId;
    private String optionText;
    private int questionId;

    public Option() {

    }

    public Option(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public static List<Option> deserializeOptions(List<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Option> optionList = new ArrayList<>();

        for (String serializedString : serializedStringList) {
            Option deserializedOption = gson.fromJson(serializedString, Option.class);
            optionList.add(deserializedOption);
        }
        return optionList;
    }
}
