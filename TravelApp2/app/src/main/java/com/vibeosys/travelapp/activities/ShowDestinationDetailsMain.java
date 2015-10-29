package com.vibeosys.travelapp.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.vibeosys.travelapp.Adaptors.DestinationDetailsPagerAdapter;
import com.vibeosys.travelapp.R;

/**
 * Created by mahesh on 10/28/2015.
 */
public class ShowDestinationDetailsMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_destination_details);
        // Get the ViewPager and set it's PagerAdapter so that it can display items

        String destName = getIntent().getExtras().getString("DestName");
        setTitle(destName);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new DestinationDetailsPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        final PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

    }
}