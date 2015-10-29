package com.vibeosys.travelapp.Adaptors;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;
import com.vibeosys.travelapp.QuestionsFromOthers;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.activities.DestinationComments;
import com.vibeosys.travelapp.fragments.ImageGridFragment;

/**
 * Created by mahesh on 10/28/2015.
 */

public class DestinationDetailsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    private int tabIcons[] = {R.drawable.camera_white, R.drawable.comment_outline, R.drawable.star_outline};

    public DestinationDetailsPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ImageGridFragment();
            case 1:

              return  new DestinationComments();
            case 2:
                return  new QuestionsFromOthers();

        }
        return null;
    }

    @Override
    public int getPageIconResId(int position) {
        return tabIcons[position];
    }
}
