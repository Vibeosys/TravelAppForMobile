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
    NewDataBase newDataBase=null;
    List<SendQuestionAnswers> mListQuestionOptions=null;
    List<QuestionAnswers> mList = new ArrayList<QuestionAnswers>();
    List<SendQuestionAnswers> mListAsksQuestions=null;
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
        newDataBase=new NewDataBase(getActivity());
        mListQuestionOptions=new ArrayList<>();
        mListQuestionOptions=newDataBase.mListAskQuestion();
        mListAsksQuestions=new ArrayList<>();
        for(int i=0;i<mListQuestionOptions.size();i++){
            for (int j=i+1;j<mListQuestionOptions.size();j++){
                if(mListQuestionOptions.get(i).getmQuestionId()==mListQuestionOptions.get(j).getmQuestionId()){
                    SendQuestionAnswers questionAnswers=new SendQuestionAnswers();
                    questionAnswers.setmOptionId(mListQuestionOptions.get(i).getmOptionId());
                    questionAnswers.setmOptionId(mListQuestionOptions.get(j).getmOptionId());
                    questionAnswers.setmOptionTextArr(new String[]{mListQuestionOptions.get(i).getmOptionText(), mListQuestionOptions.get(j).getmOptionText()});
                    questionAnswers.setmQuestionText(mListQuestionOptions.get(i).getmQuestionText());
                    questionAnswers.setmId(i);
                    mListAsksQuestions.add(questionAnswers);
                }
            }
        }

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
        textView.setText(mListQuestionOptions.get(mPageNumber).getmQuestionText());
        RadioGroup radioGroup=(RadioGroup)rootView.findViewById(R.id.questionradiogroup);
        RadioButton theRadioButton;
        int theRadioButtonlen=mListQuestionOptions.get(mPageNumber).getmOptionTextArr().length;
        for(int i=0;i<theRadioButtonlen;i++) {
            theRadioButton=new RadioButton(getActivity());
            theRadioButton.setText(mListQuestionOptions.get(i).getmOptionTextArr()[i]);
            radioGroup.addView(theRadioButton);
            radioGroup.setTag(mListQuestionOptions.get(i).getmOptionTextArr()[i]);
        }
     radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id= (int) group.getTag();
        Toast.makeText(getActivity(), "Clicked On"+id, Toast.LENGTH_SHORT).show();
    }
    });
        return rootView;
    }
    public int getPageNumber() {
        return mPageNumber;
    }
}
