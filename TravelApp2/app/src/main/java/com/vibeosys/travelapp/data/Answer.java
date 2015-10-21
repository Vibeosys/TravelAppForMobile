package com.vibeosys.travelapp.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/20/2015.
 */
public class Answer {
    String AnswerId;
    String UserId;
    String DestId;
    String OptionId;
    String CreatedDate;

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getAnswerId() {
        return AnswerId;
    }

    public void setAnswerId(String AnswerId) {
        this.AnswerId = AnswerId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getDestId() {
        return DestId;
    }

    public void setDestId(String DestId) {
        this.DestId = DestId;
    }

    public String getOptionId() {
        return OptionId;
    }

    public void setOptionId(String OptionId) {
        this.OptionId = OptionId;
    }

   public static List<Answer> deserializeSting(ArrayList<String> serializedStringList) {
        Gson gson = new Gson();
        ArrayList<Answer> answersList=new ArrayList<>();

        for(String serializedString: serializedStringList){
         Answer deserizeedAnswer= gson.fromJson(serializedString, Answer.class);
            answersList.add(deserizeedAnswer);
        }
        return answersList;
    }

}
