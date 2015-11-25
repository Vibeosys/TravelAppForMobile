package com.vibeosys.travelapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.vibeosys.travelapp.Adaptors.OthersQuestionsAdaptor;
import com.vibeosys.travelapp.Adaptors.UserListAdaptor;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.Like;
import com.vibeosys.travelapp.data.Options;
import com.vibeosys.travelapp.data.SendQuestionAnswers;
import com.vibeosys.travelapp.data.TableDataDTO;
import com.vibeosys.travelapp.data.UserLikeDTO;
import com.vibeosys.travelapp.tasks.BaseFragment;
import com.vibeosys.travelapp.util.DbTableNameConstants;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/7/2015.
 */

public class QuestionsFromOthers extends BaseFragment {

    ExpandableListView questionslistView;
    List<SendQuestionAnswers> mListQuestions = null;
    List<SendQuestionAnswers> mListOptions = null;
    HashMap<String, Options> mListQuestionsAnswers = null;
    String destId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        destId = String.valueOf(getActivity().getIntent().getExtras().getInt("DestId"));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.otherquestionlist_layout, null);
        questionslistView = (ExpandableListView) view.findViewById(R.id.reviewList);
        mListQuestions = mNewDataBase.getQuestionOptions(destId);
        if (mListQuestions != null && mListQuestions.size() > 0) {
            Log.d("getQuestions", "" + mListQuestions.size());
            Options options = null;
            mListQuestionsAnswers = new HashMap<>();
            for (SendQuestionAnswers questionAnswer : mListQuestions) {
                mListOptions = new ArrayList<>();
                String m = questionAnswer.getmQuestionText();
                mListOptions = mNewDataBase.getAnsweredAnswers(questionAnswer.getmQuestionId(), Integer.parseInt(destId));
                options = new Options();
                String[] option = new String[mListOptions.size()];
                int[] optionids = new int[mListOptions.size()];
                long[] userCountArray = new long[mListOptions.size()];
                for (int j = 0; j < mListOptions.size(); j++) {
                    option[j] = mListOptions.get(j).getmOptionText();
                    optionids[j] = mListOptions.get(j).getmOptionId();
                    userCountArray[j] = mNewDataBase.getReviewUserCount(mListOptions.get(j).getmOptionId(), destId);
                }
                options.setmOptionText(option);
                options.setmOptionIds(optionids);
                options.setmUserCounts(userCountArray);
                mListQuestionsAnswers.put(m, options);
            }

        }
        if (mListOptions != null) {
            questionslistView.setAdapter(new OthersQuestionsAdaptor(this, getActivity(), mListQuestionsAnswers));
        }
        questionslistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
        return view;
    }

    public void showUserListDialog(int questionId) {
        Log.d("DestId QS", "" + destId);
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.userlistview);
        dialog.setTitle("Reviews from users");
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        List<UserLikeDTO> listUsersDetails = mNewDataBase.getReviewUserList(questionId, destId);
        dialog.show();
        ListView usersListView = (ListView) dialog.findViewById(R.id.userlistview);
        usersListView.setAdapter(new UserListAdaptor(this, listUsersDetails));
    }

    public boolean updateLike(String imageUserId, int userLikeCount) {

        Like like = new Like();
        like.setUserId(imageUserId);
        like.setDestId(Integer.parseInt(destId));
        String likeToSync = like.serializeString();
        mNewDataBase.insertOrUpdateLikeCount(imageUserId, Integer.parseInt(destId), userLikeCount);

        if (NetworkUtils.isActiveNetworkAvailable(getActivity())) {

            TableDataDTO tableData = new TableDataDTO(DbTableNameConstants.LIKE, like.serializeString(), null);
            mServerSyncManager.uploadDataToServer(tableData);
            //super.uploadToServer(uploadData, getActivity());
            return true;
        } else {
            mNewDataBase.addDataToSync(DbTableNameConstants.LIKE, SessionManager.Instance().getUserId(), likeToSync);
            LayoutInflater
                    layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.cust_toast, null);
            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setView(view);//setting the view of custom toast layout
            toast.show();
            return false;
        }

    }

}