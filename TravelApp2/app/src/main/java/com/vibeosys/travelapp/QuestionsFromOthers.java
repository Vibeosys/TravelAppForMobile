package com.vibeosys.travelapp;

import android.app.Dialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vibeosys.travelapp.databaseHelper.NewDataBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/7/2015.
 */

public class QuestionsFromOthers extends Fragment {

    ExpandableListView questionslistView;
    NewDataBase newDataBase = null;
    List<SendQuestionAnswers> mListQuestions = null;
    List<SendQuestionAnswers> mListOptions = null;
    HashMap<String, Options> mListQuestionsAnswers = null;
    private List<UserDetails> listUsersDetails = new ArrayList<>();
    String destId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        destId = String.valueOf(getActivity().getIntent().getExtras().getInt("DestId"));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutInflater layoutInflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.otherquestionlist_layout,null);
        questionslistView = (ExpandableListView) view.findViewById(R.id.listView2);
        newDataBase = new NewDataBase(getActivity());
        mListQuestions = newDataBase.mListQuestions(destId);
        if (mListQuestions != null && mListQuestions.size() > 0) {
            Log.d("Questions", "" + mListQuestions.size());
            Options options = null;
            mListQuestionsAnswers = new HashMap<>();
            for (int i = 0; i < mListQuestions.size(); i++) {
                mListOptions = new ArrayList<>();
                String m = mListQuestions.get(i).getmQuestionText();
                mListOptions = newDataBase.mListOptions(mListQuestions.get(i).getmQuestionId(), Integer.parseInt(destId));
                options = new Options();
                String[] option = new String[mListOptions.size()];
                int[] optionids = new int[mListOptions.size()];
                int[] UsersCount = new int[mListOptions.size()];
                for (int j = 0; j < mListOptions.size(); j++) {
                    option[j] = mListOptions.get(j).getmOptionText();
                    optionids[j] = mListOptions.get(j).getmOptionId();
                    UsersCount[j] = newDataBase.CountOfUsers(mListOptions.get(j).getmOptionId(), destId);
                }
                options.setmOptionText(option);
                options.setmOptionIds(optionids);
                options.setmUserCounts(UsersCount);
                mListQuestionsAnswers.put(m, options);
            }

        }
        questionslistView.setAdapter(new OthersQuestionsAdaptor(getActivity(), mListQuestionsAnswers));
        questionslistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //    Toast.makeText(getApplicationContext(), "Clicked On.." + id, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
return view;
    }

    class OthersQuestionsAdaptor implements ExpandableListAdapter, View.OnClickListener {
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
            return valueList.get(groupPosition).getmOptionIds()[childPosition];
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
            LayoutInflater layoutInflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View aView = layoutInflater.inflate(R.layout.answer, null);
            TextView theText = (TextView) aView.findViewById(R.id.text);
            LinearLayout firstQuestion = (LinearLayout) aView.findViewById(R.id.firstquestion);
            LinearLayout secondQuestion = (LinearLayout) aView.findViewById(R.id.secondquestion);
            TextView showTextto = (TextView) aView.findViewById(R.id.textView);
            String optionText=valueList.get(groupPosition).getmOptionText()[childPosition];
            String count = String.valueOf(valueList.get(groupPosition).getmUserCounts()[childPosition]);
            // theCount.setText(String.valueOf(valueList.get(groupPosition).getmUserCounts()[childPosition]));
            //showText.setText("Says");
            firstQuestion.setId(valueList.get(groupPosition).getmOptionIds()[childPosition]);
            theText.setText(Html.fromHtml("<font color='#27ACD4'> "+ count +"  Says  " +"</font>  <font color='#27ACD4'>" + optionText + "</font>"));
            //theText.setTextColor(Color.RED);
            if (getChildrenCount(groupPosition) > childPosition + 1) {
                String optionTextq=valueList.get(groupPosition).getmOptionText()[childPosition+1];
                String countq = String.valueOf(valueList.get(groupPosition).getmUserCounts()[childPosition+1]);
                showTextto.setText(Html.fromHtml("<font color='#27ACD4'> "+ countq +"  Says  "+ "</font> <font color='#27ACD4'>" + optionTextq + "</font>"));
                secondQuestion.setId(valueList.get(groupPosition).getmOptionIds()[childPosition + 1]);
            }

            firstQuestion.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showUsersList(v.getId());
                    Toast.makeText(getActivity(), "Clicked on first" + v.getId(), Toast.LENGTH_SHORT).show();
                }
            });

            secondQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUsersList(v.getId());
                    Toast.makeText(getActivity(), "Clicked on second" + v.getId(), Toast.LENGTH_SHORT).show();
                }
            });

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

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.firstquestion:
                    break;
                case R.id.secondquestion:
                    Toast.makeText(getActivity(), "Clicked on" + v.getId(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    private void showUsersList(int id) {
        userListDialog(id);
    }

    private void userListDialog(int questionId) {
        Log.d("DestId QS", "" + destId);
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.userlistview);
        dialog.setTitle("Users List");
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listUsersDetails = newDataBase.answerlikesUsers(questionId, destId);
        //likeCount=
        dialog.show();
        ListView usersListView = (ListView) dialog.findViewById(R.id.userlistview);
        usersListView.setAdapter(new UserListAdaptor(QuestionsFromOthers.this, listUsersDetails));


    }


    private class UserListAdaptor extends BaseAdapter {
        List<UserDetails> userDetailsList;
        Context mContext;

        public UserListAdaptor(QuestionsFromOthers questionsFromOthers, List<UserDetails> listUsersDetails) {
            userDetailsList = listUsersDetails;
            mContext = getActivity();
        }


        @Override
        public int getCount() {
            return userDetailsList.size();
        }

        @Override
        public Object getItem(int position) {
            return userDetailsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.userlikelist, null);
            TextView usernameText = (TextView) view.findViewById(R.id.userlikesname);
            usernameText.setText(userDetailsList.get(position).getUsername());
            return view;
        }
    }
}