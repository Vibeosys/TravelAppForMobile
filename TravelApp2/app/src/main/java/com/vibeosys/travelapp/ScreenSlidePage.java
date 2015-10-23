package com.vibeosys.travelapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vibeosys.travelapp.data.Option;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/14/2015.
 */
public class ScreenSlidePage extends Fragment {
    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    ArrayList<Option> answers;
    NewDataBase newDataBase=null;
    List<SendQuestionAnswers> mListQuestions=null;
    List<SendQuestionAnswers> mListOptions=null;
    HashMap<String,Options> mListQuestionsAnswers=null;
    OnDataPass onDataPass;
    public static ScreenSlidePage create(int pageNumber) {
        ScreenSlidePage fragment = new ScreenSlidePage();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public ScreenSlidePage() {
    }
    public interface OnDataPass {
        public void onDataPass(String data);
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        onDataPass = (OnDataPass) a;
    }
    public void passData(String data) {
        onDataPass.onDataPass(data);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newDataBase=new NewDataBase(getActivity());
        mListQuestions=newDataBase.mListQuestions();
        Log.d("Questions", "" + mListQuestions.size());

      if(mListQuestions!=null&&mListQuestions.size()>0) {
          Options options=null;
          mListQuestionsAnswers=new HashMap<>();
          for(int i = 0;i<mListQuestions.size();i++){
              mListOptions=new ArrayList<>();
              String m=mListQuestions.get(i).getmQuestionText();
              mListOptions = newDataBase.mListOptions(mListQuestions.get(i).getmQuestionId());
              options = new Options();
              String[] option=new String[mListOptions.size()];
              int [] optionids=new int[mListOptions.size()];
              for(int j=0;j<mListOptions.size();j++) {
                option[j]=mListOptions.get(j).getmOptionText();
                optionids[j]=mListOptions.get(j).getmOptionId();
              }
              options.setmOptionText(option);
              options.setmOptionIds(optionids);

              mListQuestionsAnswers.put(m,options);
          }
      }

        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.userquestionanswers, container, false);
        answers=new ArrayList<>();
        // Set the title view to show the page number.
        TextView textView=(TextView)rootView.findViewById(R.id.questionText);
        List<String> keyList = Collections.list(Collections.enumeration(mListQuestionsAnswers.keySet()));//Questions List
        textView.setText(keyList.get(mPageNumber));
        RadioGroup radioGroup=(RadioGroup)rootView.findViewById(R.id.questionradiogroup);
        final ArrayList<Options> valueList = Collections.list(Collections.enumeration(mListQuestionsAnswers.values()));
        Log.d("ListOptions",""+valueList.size());
        int theRadioButtonlen=valueList.get(mPageNumber).getmOptionText().length;

        for(int i=0;i<theRadioButtonlen;i++) {
            RadioButton  theRadioButton=new RadioButton(getActivity());
            theRadioButton.setText(valueList.get(mPageNumber).mOptionText[i]);
            theRadioButton.setId(valueList.get(mPageNumber).mOptionIds[i]);
            radioGroup.addView(theRadioButton);
        }

        radioGroup.setTag(valueList.get(mPageNumber).getmOptionId());
        radioGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

     radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
             int id = (int) group.getTag();

             int lo = group.getCheckedRadioButtonId();
             onDataPass.onDataPass(String.valueOf(lo));
             Toast.makeText(getActivity(), "Clicked On" + lo, Toast.LENGTH_SHORT).show();
         }
     });

        Log.d("Answers", "" + answers.size());

        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }
}
