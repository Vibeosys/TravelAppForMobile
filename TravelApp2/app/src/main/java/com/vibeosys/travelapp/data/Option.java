package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Option {
private int OptionsId;
private String OptionText;
private int QuestionId;

    public int getOptionId() {
        return OptionsId;
    }

    public void setOptionsId(int optionsId) {
        this.OptionsId = optionsId;
    }

    public String getOptionText() {
        return OptionText;
    }

    public void setOptionText(String optionText) {
        this.OptionText = optionText;
    }

    public int getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int questionId) {
        this.QuestionId = questionId;
    }

    public static List<Option> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Option> optionsList=new ArrayList<>();

        for(String serializedString: serializedStringList){
            Option deserizeedImages= gson.fromJson(serializedString, Option.class);
            optionsList.add(deserizeedImages);
        }
        return optionsList;
    }

}
