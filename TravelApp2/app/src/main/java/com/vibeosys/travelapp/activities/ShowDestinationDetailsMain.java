package com.vibeosys.travelapp.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.vibeosys.travelapp.Adaptors.DestinationDetailsPagerAdapter;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.tasks.BaseActivity;

/**
 * Created by mahesh on 10/28/2015.
 */
public class ShowDestinationDetailsMain extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mServerSyncManager != null && !mServerSyncManager.isDownloadInProgress())
            mServerSyncManager.syncDataWithServer(false);
        //fetchData(SessionManager.Instance().getUserId(), false);
        setContentView(R.layout.show_destination_details);
        // Get the ViewPager and set it's PagerAdapter so that it can display itemsDN

        String destName = getIntent().getExtras().getString("DestName");
        int id = getIntent().getExtras().getInt("Id");
        setTitle(destName);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new DestinationDetailsPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(id);
        // Give the PagerSlidingTabStrip the ViewPager
        final PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }
}
