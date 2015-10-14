package com.vibeosys.travelapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vibeosys.travelapp.data.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/7/2015.
 */
public class QuestionsFromOthers extends AppCompatActivity {

    ExpandableListView questionslistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherquestionlist_layout);
        questionslistView = (ExpandableListView) findViewById(R.id.listView2);

        //questionslistView.setAdapter(new OthersQuestionsAdaptor(getApplicationContext()));

        final List<Question> questionsList = new ArrayList<Question>();

        Question q1 = new Question();
        q1.setmQuestion("Hows your experience?");
        q1.setmAnswers(new String[]{"fine", "good", "wrost", "nice", "glorius"});
        q1.setmType(new String[]{"2", "3", "6", "2", "6"});

        Question q2 = new Question();
        q2.setmQuestion("Do you like the Place?");
        q2.setmAnswers(new String[]{"good", "bad", "na"});
        q2.setmType(new String[]{"3", "4", "2"});
        Question q3 = new Question();
        q3.setmQuestion("Do you like the Place?");
        q3.setmAnswers(new String[]{"Fine", "Great"});
        q3.setmType(new String[]{"2", "3"});
        questionsList.add(q1);
        questionsList.add(q2);
        questionsList.add(q3);
        questionslistView.setAdapter(new OthersQuestionsAdaptor(this, questionsList));

        questionslistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), "Clicked On.." + questionsList.get(groupPosition).getmAnswers()[childPosition], Toast.LENGTH_SHORT).show();
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
            if (mList != null) {
                return mList.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            if (mList != null) {
                int childCount = mList.get(groupPosition).getmAnswers().length;
                if (childCount % 2 == 0) {
                    return childCount / 2;
                } else {
                    return (childCount / 2) + 1;
                }
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
            theView.setTextSize(20);
            theView.setTypeface(Typeface.DEFAULT_BOLD);
            theView.setText(mList.get(groupPosition).getmQuestion());

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

            theText.setText(mList.get(groupPosition).getmAnswers()[childPosition]);
            theCount.setText(mList.get(groupPosition).getmType()[childPosition]);

            if(getChildrenCount(groupPosition) > childPosition) {
                theText2.setText(mList.get(groupPosition).getmAnswers()[childPosition + 1]);
                theCount2.setText(mList.get(groupPosition).getmType()[childPosition + 1]);
            }
            return aView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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
