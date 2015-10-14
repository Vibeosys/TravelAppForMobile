package com.vibeosys.travelapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/14/2015.
 */
public class ScreenSlidePage extends Fragment {
    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    List<QuestionAnswers> mList = new ArrayList<QuestionAnswers>();
    public static ScreenSlidePage create(int pageNumber) {
        ScreenSlidePage fragment = new ScreenSlidePage();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public ScreenSlidePage() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuestionAnswers q1 = new QuestionAnswers();
        q1.setQuestion("How's Your experience?");
        q1.setAnswers(new String[]{"good", "bad", "very bad"});
        QuestionAnswers q2 = new QuestionAnswers();
        q2.setQuestion("How's the Place?");
        q2.setAnswers(new String[]{"good", "bad"});
        QuestionAnswers q3 = new QuestionAnswers();
        q3.setQuestion("What do you Expect from this Place?");
        q3.setAnswers(new String[]{"na", "river", "coolwind"});
        mList.add(q1);
        mList.add(q2);
        mList.add(q3);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.userquestionanswers, container, false);

        // Set the title view to show the page number.
        TextView textView=(TextView)rootView.findViewById(R.id.questionText);
        textView.setText(mList.get(mPageNumber).getQuestion());
        RadioGroup radioGroup=(RadioGroup)rootView.findViewById(R.id.questionradiogroup);
        RadioButton theRadioButton;
        int theRadioButtonlen=mList.get(mPageNumber).getAnswers().length;
        for(int i=0;i<theRadioButtonlen;i++) {
            theRadioButton=new RadioButton(getActivity());
            theRadioButton.setText(mList.get(i).getAnswers()[i]);
            radioGroup.addView(theRadioButton);
        }
     radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Toast.makeText(getActivity(), "Clicked On", Toast.LENGTH_SHORT).show();
    }
    });
        return rootView;
    }
    public int getPageNumber() {
        return mPageNumber;
    }
}
