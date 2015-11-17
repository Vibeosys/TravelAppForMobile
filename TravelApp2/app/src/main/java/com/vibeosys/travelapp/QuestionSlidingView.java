package com.vibeosys.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.vibeosys.travelapp.data.Answer;
import com.vibeosys.travelapp.data.TableDataDTO;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.DbTableNameConstants;
import com.vibeosys.travelapp.util.NetworkUtils;
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
    int DestId;
    List<String> mListOptions;

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

        int pages = mNewDataBase.getQuestions();
        NUM_PAGES = pages;

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

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

    private void saveAnswer(String optionId) {
        Answer answer = new Answer(optionId, String.valueOf(DestId), mSessionManager.getUserId());
        String serializedJsonString = answer.serializeString();
        Log.d("Option Data", serializedJsonString);
        TableDataDTO tableDataDTO = new TableDataDTO("answer", serializedJsonString, null);

        if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext())) {
            mServerSyncManager.uploadDataToServer(tableDataDTO);
        } else {
            mNewDataBase.addDataToSync(DbTableNameConstants.ANSWER, mSessionManager.getUserEmailId(), serializedJsonString);

        }
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
        //Log.d("Data From Fragment", data);
        //Log.d("Size of List", "" + getAnsweredAnswers.size());
        if (mViewPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
            Toast.makeText(getApplicationContext(), "Thanks for your feedback", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!UserAuth.isUserLoggedIn(getApplicationContext()))
                return;

            saveAnswer(data);
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        }
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