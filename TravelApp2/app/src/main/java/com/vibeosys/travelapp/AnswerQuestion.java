package com.vibeosys.travelapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/8/2015.
 */
public class AnswerQuestion extends AppCompatActivity {
ExpandableListView anweerslist;
Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.answerquestiolist);
        anweerslist=(ExpandableListView)findViewById(R.id.answerquestionlist);
        List<QuestionAnswers> mList=new ArrayList<QuestionAnswers>();
        QuestionAnswers q1=new QuestionAnswers();
        q1.setQuestion("How's Your experience?");
        q1.setAnswers(new String[]{"good", "bad", "very bad"});
        QuestionAnswers q2=new QuestionAnswers();
        q2.setQuestion("How's the Place?");
        q2.setAnswers(new String[]{"good", "bad"});
        QuestionAnswers q3=new QuestionAnswers();
        q3.setQuestion("What do you Expect from this Place?");
        q3.setAnswers(new String[]{"na", "river", "coolwind"});
        mList.add(q1);
        mList.add(q2);
        mList.add(q3);
        anweerslist.setAdapter( new AnswerQuestionAdaptor(this,mList));
        anweerslist.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            CheckBox radioButton;
                radioButton=(CheckBox)v.findViewById(R.id.answerqueestionoptiontext);
                radioButton.setChecked(true);
                //if(radioButton.isChecked()==true)radioButton.setChecked(false);

                return false;
            }
        });
    }
    public class AnswerQuestionAdaptor implements ExpandableListAdapter {
        Context mContext;
        List<QuestionAnswers> mLists;
        HashMap<String,String> mAnswers;
        public AnswerQuestionAdaptor(AnswerQuestion answerQuestion, List<QuestionAnswers> mList) {
            mContext=answerQuestion;
            mLists=mList;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }


        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            if(mLists!=null) return mLists.size();
        return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if(mLists!=null) return mLists.get(groupPosition).getAnswers().length;
        return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mLists.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mLists.get(groupPosition).getAnswers()[childPosition];
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
            anweerslist.expandGroup(groupPosition);
            theView.setTextSize(20);
            theView.setTypeface(Typeface.DEFAULT_BOLD);
            theView.setText(mLists.get(groupPosition).getQuestion());
            return theView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View theChild=getLayoutInflater().inflate(R.layout.answerquestionchild, null);
            CheckBox theClicked=(CheckBox)theChild.findViewById(R.id.answerqueestionoptiontext);
            TextView theAnsewers=(TextView)theChild.findViewById(R.id.answertext);
            theAnsewers.setText(mLists.get(groupPosition).getAnswers()[childPosition]);
            //theClicked.setOnCheckedChangeListener(new OnChangeClickLitener());
            return theChild;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
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

        private class OnChangeClickLitener implements CompoundButton.OnCheckedChangeListener {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(mContext, "Cliced" + isChecked, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
