package com.vibeosys.travelapp.Adaptors;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vibeosys.travelapp.data.Options;
import com.vibeosys.travelapp.fragments.QuestionsFromOthers;
import com.vibeosys.travelapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anand on 25-11-2015.
 */
public class OthersQuestionsAdaptor
        implements ExpandableListAdapter, View.OnClickListener {

    private QuestionsFromOthers mQuestionsFromOthersForm;
    private Context mContext;
    private HashMap<String, Options> mList;
    private List<String> keyList = null;
    private ArrayList<Options> valueList = null;


    public OthersQuestionsAdaptor(QuestionsFromOthers questionsFromOthersForm,
                                  Context aContext, HashMap<String, Options> aList) {
        this.mQuestionsFromOthersForm = questionsFromOthersForm;
        mContext = aContext;
        mList = aList;
        keyList = Collections.list(Collections.enumeration(aList.keySet()));
        valueList = Collections.list(Collections.enumeration(aList.values()));
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
        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);
        theView.setTextSize(16);
        theView.setTypeface(Typeface.DEFAULT_BOLD);
        theView.setText(keyList.get(groupPosition));
        return theView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String[] optionTextArray = valueList.get(groupPosition).getmOptionText();
        long[] userCountArray = valueList.get(groupPosition).getmUserCounts();
        int[] optionIdArray = valueList.get(groupPosition).getmOptionIds();

        int firstElementPosition = 0;
        int secondElementPosition = 1;
        if (childPosition > 0) {
            firstElementPosition = childPosition + 1;
            secondElementPosition = childPosition + 2;
        }

        //childPosition = childPosition * 2;
        LayoutInflater layoutInflater = (LayoutInflater) mQuestionsFromOthersForm.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View aView = layoutInflater.inflate(R.layout.answer, null);
        TextView theText = (TextView) aView.findViewById(R.id.text);
        LinearLayout firstQuestion = (LinearLayout) aView.findViewById(R.id.firstquestion);
        LinearLayout secondQuestion = (LinearLayout) aView.findViewById(R.id.secondquestion);
        TextView showTextto = (TextView) aView.findViewById(R.id.textView);
        String optionText = optionTextArray[firstElementPosition];
        String count = String.valueOf(userCountArray[firstElementPosition]);
        firstQuestion.setId(optionIdArray[firstElementPosition]);
        theText.setText(Html.fromHtml("<font color='#27ACD4'> " + count + "  Says  " + "</font>  <font color='#27ACD4'>" + optionText + "</font>"));
        //theText.setTextColor(Color.RED);
        if (optionTextArray.length >= secondElementPosition + 1) {
            //if (getChildrenCount(groupPosition) > childPosition + 1) {
            String optionTextq = optionTextArray[secondElementPosition];
            String countq = String.valueOf(userCountArray[secondElementPosition]);
            showTextto.setText(Html.fromHtml("<font color='#27ACD4'> " + countq + "  Says  " + "</font> <font color='#27ACD4'>" + optionTextq + "</font>"));
            secondQuestion.setId(optionIdArray[secondElementPosition]);

            secondQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mQuestionsFromOthersForm.showUserListDialog(v.getId());
                    //Toast.makeText(getActivity(), "Clicked on second" + v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
            //}
        }
        firstQuestion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mQuestionsFromOthersForm.showUserListDialog(v.getId());
                //Toast.makeText(getActivity(), "Clicked on first" + v.getId(), Toast.LENGTH_SHORT).show();
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

    }
}
