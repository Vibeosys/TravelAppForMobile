package com.vibeosys.travelapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mahesh on 10/6/2015.
 */
public class PhotosFromOthers extends AppCompatActivity {
    ListView other_photo_list;
 List<usersImages> mPhotoList=null;
    int mDestId;
    NewDataBase newDataBase=null;
    public interface BtnClickListener {
        public abstract void onBtnClick(int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherphotolist);
        newDataBase=new NewDataBase(PhotosFromOthers.this);
        other_photo_list = (ListView) findViewById(R.id.otherlistView);
        Bundle extras = getIntent().getExtras();
        mDestId= extras.getInt("DestId");
        Log.d("PhotosFromOthers",""+mDestId);
        mPhotoList=newDataBase.Images(mDestId);
        other_photo_list.setAdapter(new ShowListAdaptor(getApplicationContext(), new BtnClickListener() {
            @Override
            public void onBtnClick(int position) {
             Intent intent=new Intent(getApplicationContext(),UserLikes.class);
                startActivity(intent);
            }
        },mPhotoList));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(PhotosFromOthers.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

class ShowListAdaptor extends BaseAdapter {
    Context mcontext;
    PhotosFromOthers.BtnClickListener mbtnClickListener = null;
    List<usersImages> thePhotoList=null;
    ShowListAdaptor(Context context, PhotosFromOthers.BtnClickListener btnClickListener, List<usersImages> mPhotoList) {
        this.mcontext = context;
        this.mbtnClickListener = btnClickListener;
        this.thePhotoList=mPhotoList;
    }

    @Override
    public int getCount() {
        if(thePhotoList!=null) return thePhotoList.size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        Log.d("GetItem Returned..", String.valueOf(position));
        return position;
    }

    @Override
    public long getItemId(int position) {
        Log.d("GetItemId Returned..", String.valueOf(position));
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.otherphotos, parent, false);
        ImageView photo = null;
        TextView user = null;
        photo = (ImageView) view.findViewById(R.id.otherphotoview);
        user = (TextView) view.findViewById(R.id.user_text);
        user.setText("User");
        user.setTextColor(103048);
        Picasso.with(mcontext).load(thePhotoList.get(position).getmImagePaths()).into(photo);
        photo.setTag(position);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbtnClickListener != null) {
                    mbtnClickListener.onBtnClick((int) v.getTag());
                }

            }
        });
        return view;
    }
}