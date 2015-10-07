package com.example.mahesh.travelapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.mahesh.travelapp.data.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/7/2015.
 */
public class QuestionsFromOthers extends AppCompatActivity {

    ExpandableListView questionslistView;
    String[] questions_text = {"Hows your experience?", "Do you like the Place?", "Hows your trip?"};
    String[] answers_text = {"fine", "good", "wrost", "nice", "glorius", "bad"};
    String[] counts_text = {"3", "2", "4", "3", "10", "10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherquestionlist_layout);
        questionslistView = (ExpandableListView) findViewById(R.id.listView2);

        //questionslistView.setAdapter(new OthersQuestionsAdaptor(getApplicationContext()));

        List<Question> questionsList = new ArrayList<Question>();

        Question q1 = new Question();
        q1.setmQuestion("Hows your experience?");
        q1.setmAnswers(new String[] {"fine", "good", "wrost", "nice", "glorius", "bad"});

        Question q2 = new Question();
        q2.setmQuestion("Do you like the Place?");
        q2.setmAnswers(new String[]{"3", "2", "4"});

        Question q3 = new Question();
        q3.setmQuestion("Do you like the Place?");
        q3.setmAnswers(new String[]{"3"});

        questionsList.add(q1);
        questionsList.add(q2);
        questionsList.add(q3);
        questionslistView.setAdapter(new OthersQuestionsAdaptor(this, questionsList));
        questionslistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                return false;
            }
        });

    }

    private class OthersQuestionsAdaptor implements ExpandableListAdapter {

        private Context mContext;
        private List<Question> mList;

        OthersQuestionsAdaptor(Context aContext, List<Question> aList) {
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
            if(mList != null) {
                return  mList.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            if(mList != null) {
                return  mList.get(groupPosition).getmAnswers().length;
            }
            return 0;
        }

        @Override
        public Question getGroup(int groupPosition) {
            return mList.get(groupPosition);
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            return mList.get(groupPosition).getmAnswers()[childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView theView = new TextView(mContext);
            theView.setText(mList.get(groupPosition).getmQuestion());
            return theView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View aView = getLayoutInflater().inflate(R.layout.answer, null);
            CheckBox theText = (CheckBox) aView.findViewById(R.id.text);
            theText.setText(getChild(groupPosition, childPosition));
            return aView;
            /*ViewHolder viewHolder = new ViewHolder();
            LinearLayout parentlinearLayout = new LinearLayout(mContext);
            parentlinearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout child = null;
            viewHolder.questions = new TextView(mContext);
            viewHolder.counts = new TextView(mContext);
            viewHolder.answers = new TextView(mContext);
            viewHolder.questions.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewHolder.questions.setText(questions_text[childPosition]);
            for (int i = 0; i < answers_text.length; i++) {
                child = new LinearLayout(mContext);
                child.setOrientation(LinearLayout.HORIZONTAL);
                viewHolder.answers.append(answers_text[i] + "\n ");
                viewHolder.counts.append(counts_text[i] + "\n ");
            }

            parentlinearLayout.addView(viewHolder.questions);
            // linearLayout.addView(linearLayout);
            child.addView(viewHolder.answers);
            child.addView(viewHolder.counts);
            parentlinearLayout.addView(child);
            return parentlinearLayout;
            */
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
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

    static class ViewHolder {
        TextView answers;
        TextView questions;
        TextView counts;
    }
}
