package com.vibeosys.travelapp.util;

import com.vibeosys.travelapp.data.Answer;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.DbImageDTO;
import com.vibeosys.travelapp.data.DbUserDTO;
import com.vibeosys.travelapp.data.Destination;
import com.vibeosys.travelapp.data.Like;
import com.vibeosys.travelapp.data.Option;
import com.vibeosys.travelapp.data.Question;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;

import java.util.List;

/**
 * Created by anand on 09-11-2015.
 */
public class DbOperations {

    private NewDataBase mNewDataBase;

    public DbOperations(NewDataBase dataBase) {
        mNewDataBase = dataBase;
    }

    public boolean addOrUpdateDestinations(List<String> inserts, List<String> updates) {
        List<Destination> destInserts = Destination.deserializeDestinations(inserts);
        List<Destination> destUpdates = Destination.deserializeDestinations(updates);

        boolean isInserted = mNewDataBase.insertDestinations(destInserts);
        boolean isUpdated = mNewDataBase.updateDestinations(destUpdates);
        return isInserted & isUpdated;
    }

    public boolean addOrUpdateUsers(List<String> inserts, List<String> updates) {
        List<DbUserDTO> userInserts = DbUserDTO.deserializeUsers(inserts);
        List<DbUserDTO> userUpdates = DbUserDTO.deserializeUsers(updates);

        boolean isInserted = mNewDataBase.insertUsers(userInserts);
        boolean isUpdated = mNewDataBase.updateUsers(userUpdates);
        return isInserted & isUpdated;
    }

    public boolean addOrUpdateQuestions(List<String> inserts, List<String> updates) {
        List<Question> questionInserts = Question.deserializeQuestions(inserts);
        List<Question> questionUpdates = Question.deserializeQuestions(updates);

        boolean isInserted = mNewDataBase.insertQuestions(questionInserts);
        boolean isUpdated = mNewDataBase.updateQuestions(questionUpdates);
        return isInserted & isUpdated;
    }

    public boolean addOrUpdateOptions(List<String> inserts, List<String> updates) {
        List<Option> optionInserts = Option.deserializeOptions(inserts);
        List<Option> optionUpdates = Option.deserializeOptions(updates);

        boolean isInserted = mNewDataBase.insertOptions(optionInserts);
        boolean isUpdated = mNewDataBase.updateOptions(optionUpdates);
        return isInserted & isUpdated;
    }

    public boolean addOrUpdateImages(List<String> inserts)
    {
        List<DbImageDTO> imageInserts = DbImageDTO.deserializeImages(inserts);

        boolean isInserted = mNewDataBase.insertImages(imageInserts);
        return isInserted;
    }

    public boolean addOrUpdateComments(List<String> inserts, List<String> updates) {
        List<Comment> commentInserts = Comment.deserializeComments(inserts);
        List<Comment> commentUpdates = Comment.deserializeComments(updates);

        boolean isInserted = mNewDataBase.insertComments(commentInserts);
        boolean isUpdated = mNewDataBase.updateComments(commentUpdates);
        return isInserted & isUpdated;
    }

    public boolean addOrUpdateLikes(List<String> inserts, List<String> updates) {
        List<Like> likeInserts = Like.deserializeLikes(inserts);
        List<Like> likeUpdates = Like.deserializeLikes(updates);

        boolean isInserted = mNewDataBase.insertLikes(likeInserts);
        boolean isUpdated = mNewDataBase.updatetLikes(likeUpdates);
        return isInserted & isUpdated;
    }

    public boolean addOrUpdateAnswers(List<String> inserts) {
        List<Answer> likeInserts = Answer.deserializeAnswers(inserts);
        //List<Answer> likeUpdates = Answer.deserializeAnswers(updates);

        boolean isInserted = mNewDataBase.insertAnswers(likeInserts);
        //boolean isUpdated = mNewDataBase.updatetLikes(likeUpdates);
        return isInserted;
    }
}
