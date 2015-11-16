package com.vibeosys.travelapp.Adaptors;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vibeosys.travelapp.MyImageDB;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.view.LoaderImageView;

import java.util.List;

/**
 * Created by mahesh on 10/21/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyImageDB> myImageDBs;

    public ImageAdapter(Context context, List<MyImageDB> myimages) {
        mContext = context;
        myImageDBs = myimages;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    public int getCount() {
        if (myImageDBs != null) return myImageDBs.size();
        else return 0;
    }

    public Object getItem(int position) {
        return myImageDBs.get(position).getmImageId();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageAdapter.ViewHolder viewHolder = null;
        Bitmap bmp = null;
        if (row == null) {
            LayoutInflater theLayoutInflator = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = theLayoutInflator.inflate(R.layout.gridviewsource, null);
            viewHolder = new ImageAdapter.ViewHolder();
            viewHolder.imageView = (LoaderImageView) row.findViewById(R.id.viewImage);
            viewHolder.textView = (TextView) row.findViewById(R.id.dateText);
            row.setTag(viewHolder);

        } else viewHolder = (ImageAdapter.ViewHolder) row.getTag();

        //bmp = decodeURI(myImageDBs.get(position).getmImagePath());
        //BitmapFactory.decodeFile(mUrls[position].getPath());
        String theDate = myImageDBs.get(position).getmCreatedDate();
        //String[] splited = theDate.split(",");
        //String[] dateYear = splited[1].split("\\s+");
        //Log.d("MyPhotos", dateYear[0]);
        Log.d("MyPhotos", myImageDBs.get(position).getmCreatedDate());
        viewHolder.textView.setText(theDate);
        viewHolder.imageView.loadImageFromFile("file:" + myImageDBs.get(position).getmImagePath());
        return row;
    }

    private static class ViewHolder {
        LoaderImageView imageView;
        TextView textView;
    }
}
