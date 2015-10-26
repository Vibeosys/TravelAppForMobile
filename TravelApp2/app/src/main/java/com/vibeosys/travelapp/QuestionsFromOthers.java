package com.vibeosys.travelapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.tasks.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/7/2015.
 */

public class QuestionsFromOthers extends BaseActivity {

    ExpandableListView questionslistView;
    NewDataBase newDataBase = null;
    List<SendQuestionAnswers> mListQuestions = null;
    List<SendQuestionAnswers> mListOptions = null;
    HashMap<String, Options> mListQuestionsAnswers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String destName = getIntent().getExtras().getString("DestName");
        this.setTitle("Traveller reviews for " + destName);

        setContentView(R.layout.otherquestionlist_layout);
        questionslistView = (ExpandableListView) findViewById(R.id.listView2);
        newDataBase = new NewDataBase(this);
        mListQuestions = newDataBase.mListQuestions();

        if (mListQuestions != null && mListQuestions.size() > 0) {
            Log.d("Questions", "" + mListQuestions.size());
            Options options = null;
            mListQuestionsAnswers = new HashMap<>();
            for (int i = 0; i < mListQuestions.size(); i++) {
                mListOptions = new ArrayList<>();
                String m = mListQuestions.get(i).getmQuestionText();
                mListOptions = newDataBase.mListOptions(mListQuestions.get(i).getmQuestionId());
                options = new Options();
                String[] option = new String[mListOptions.size()];
                int[] optionids = new int[mListOptions.size()];
                int[] UsersCount = new int[mListOptions.size()];
                for (int j = 0; j < mListOptions.size(); j++) {
                    option[j] = mListOptions.get(j).getmOptionText();
                    optionids[j] = mListOptions.get(j).getmOptionId();
                    UsersCount[j] = newDataBase.CountOfUsers(mListOptions.get(j).getmOptionId());
                }
                options.setmOptionText(option);
                options.setmOptionIds(optionids);
                options.setmUserCounts(UsersCount);
                mListQuestionsAnswers.put(m, options);
            }

        }
        questionslistView.setAdapter(new OthersQuestionsAdaptor(this, mListQuestionsAnswers));
        questionslistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(getApplicationContext(), "Clicked On.." + mListOptions.get(groupPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private class OthersQuestionsAdaptor implements ExpandableListAdapter {
        private Context mContext;
        private HashMap<String, Options> mList;
        List<String> keyList = Collections.list(Collections.enumeration(mListQuestionsAnswers.keySet()));
        ArrayList<Options> valueList = Collections.list(Collections.enumeration(mListQuestionsAnswers.values()));

        OthersQuestionsAdaptor(Context aContext, HashMap<String, Options> aList) {
            mContext = aContext;
            mList = aList;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            if (keyList != null) {
                return keyList.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            if (valueList != null) {
                int childCount = valueList.get(groupPosition).getmOptionText().length;
                if (childCount % 2 == 0) {
                    return childCount / 2;
                } else {
                    return (childCount / 2) + 1;
                }
            }
            return 0;
        }

        @Override
        public Options getGroup(int groupPosition) {
            return mList.get(groupPosition);
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            return valueList.get(groupPosition).getmOptionText()[childPosition];
            // return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView theView = new TextView(mContext);
            questionslistView.expandGroup(groupPosition);
            theView.setTextSize(15);
            theView.setTypeface(Typeface.DEFAULT_BOLD);
            theView.setText(keyList.get(groupPosition));
            return theView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            childPosition = childPosition * 2;
            View aView = getLayoutInflater().inflate(R.layout.answer, null);
            TextView theText = (TextView) aView.findViewById(R.id.text);
            TextView theCount = (TextView) aView.findViewById(R.id.count);
            TextView theText2 = (TextView) aView.findViewById(R.id.textView);
            TextView theCount2 = (TextView) aView.findViewById(R.id.countview);
            theText.setText(valueList.get(groupPosition).getmOptionText()[childPosition]);
            theCount.setText(String.valueOf(valueList.get(groupPosition).getmUserCounts()[childPosition]));
            if (getChildrenCount(groupPosition) > childPosition) {
                theText2.setText(valueList.get(groupPosition).getmOptionText()[childPosition + 1]);
                theCount2.setText(String.valueOf(valueList.get(groupPosition).getmUserCounts()[childPosition] + 1));
            }
            return aView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {
        }

        @Override
        public void onGroupCollapsed(int groupPosition) {
        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    }


}
