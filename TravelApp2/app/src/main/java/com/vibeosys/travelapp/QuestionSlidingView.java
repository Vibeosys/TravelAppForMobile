package com.vibeosys.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vibeosys.travelapp.data.Answer;
import com.vibeosys.travelapp.data.TableDataDTO;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.SessionManager;
import com.vibeosys.travelapp.util.UserAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/14/2015.
 */
public class QuestionSlidingView extends BaseActivity implements ScreenSlidePage.OnDataPass {
    private static int NUM_PAGES;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    Button mPrevButton, mNextButton;
    //NewDataBase newDataBase = null;
    //String UserId;
    int DestId;
    //String EmailId;
    List<String> mListOptions;
    //SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionslidingview);
        String destName = getIntent().getExtras().getString("DestName");

        this.setTitle("Feedback about " + destName);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        // Make us non-modal, so that others can receive touch events.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        //  this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DestId = getIntent().getExtras().getInt("DestId");
        mListOptions = new ArrayList<>();
        //UserId = mSessionManager.Instance().getUserId();
        //EmailId = mSessionManager.Instance().getUserEmailId();
        //newDataBase = new NewDataBase(this);
        int pages = mNewDataBase.Questions(DestId);
        NUM_PAGES = pages;

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mPrevButton = (Button) findViewById(R.id.prevButton);
        mNextButton = (Button) findViewById(R.id.nextButton);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });

        Log.d("Size ", "" + mListOptions.size());
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserAuth.isUserLoggedIn(getApplicationContext()))
                    return;

                String theUserId = SessionManager.Instance().getUserId();
                String theUserEmailId = SessionManager.Instance().getUserEmailId();

                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);

                if ((mViewPager.getCurrentItem() + 1 == mPagerAdapter.getCount())) {
                    Log.d("Calling Finish", "");
                }
                String uploadData;
                if ((mViewPager.getCurrentItem() == mPagerAdapter.getCount() - 1)) {
                    if (mListOptions.size() > 0) {
                        Gson gson = new Gson();
                        ArrayList<TableDataDTO> tableDataList = new ArrayList<TableDataDTO>();
                        for (int i = 0; i < mListOptions.size(); i++) {
                            Answer answer = new Answer(mListOptions.get(i), String.valueOf(DestId), theUserId);
                            String SerializedJsonString = gson.toJson(answer);
                            Log.d("Option Data", SerializedJsonString);
                            tableDataList.add(new TableDataDTO("answer", SerializedJsonString, null));
                        }

                        //uploadData = gson.toJson(new Upload(new UploadUser(theUserId, theUserEmailId), tableDataList));
                        //Log.d("Like Data to Uplaod", uploadData);

                        if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext())) {
                            mServerSyncManager.uploadDataToServer(tableDataList);
                        } else {
                            for (TableDataDTO tableData : tableDataList)
                                mNewDataBase.addDataToSync("answer", theUserEmailId, tableData.getTableData());

                            LayoutInflater
                                    layoutInflater = getLayoutInflater();
                            View view = layoutInflater.inflate(R.layout.cust_toast, null);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setView(view);//setting the view of custom toast layout
                            toast.show();

                        }
                        finish();
                    }
                }
            }
        });

        mNextButton.setText("Next");
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
//            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_screen_slide, menu);

        menu.findItem(R.id.action_previous).setEnabled(mViewPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mViewPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataPass(String data) {
        mListOptions.add(data);
        Log.d("Data From Fragment", data);
        Log.d("Size of List", "" + mListOptions.size());
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return ScreenSlidePage.create(position);
        }
    }
}