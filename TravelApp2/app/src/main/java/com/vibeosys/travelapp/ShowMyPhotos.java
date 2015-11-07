package com.vibeosys.travelapp;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ListView;

import com.vibeosys.travelapp.Adaptors.ImageAdapter;
import com.vibeosys.travelapp.tasks.BaseActivity;

import java.util.List;

/**
 * Created by mahesh on 10/3/2015.
 */
public class ShowMyPhotos extends BaseActivity {
    ListView showphoto_view;
    List<MyImageDB> mUserImagesList = null;
    //NewDataBase newDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.showmyphoto_layout);
        getSupportActionBar().setTitle("My Photos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        showphoto_view = (ListView) findViewById(R.id.grid_images);

        try {
            mUserImagesList = mNewDataBase.mUserImagesList();
            showphoto_view.setAdapter(new ImageAdapter(this, mUserImagesList));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

