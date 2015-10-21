package com.vibeosys.travelapp.Adaptors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeosys.travelapp.MyImageDB;
import com.vibeosys.travelapp.R;

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
            viewHolder.imageView = (ImageView) row.findViewById(R.id.viewImage);
            viewHolder.textView = (TextView) row.findViewById(R.id.dateText);
            row.setTag(viewHolder);

        } else viewHolder = (ImageAdapter.ViewHolder) row.getTag();

        bmp = decodeURI(myImageDBs.get(position).getmImagePath());
        //BitmapFactory.decodeFile(mUrls[position].getPath());
        String theDate=myImageDBs.get(position).getmCreatedDate();
        String[] splited=theDate.split(",");
        String[] dateYear=splited[1].split("\\s+");
        Log.d("MyPhotos", dateYear[0]);
        Log.d("MyPhotos", myImageDBs.get(position).getmCreatedDate());
        viewHolder.textView.setText(splited[0]);
        viewHolder.imageView.setImageBitmap(bmp);
        return row;


    }


    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    public Bitmap decodeURI(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if (options.outHeight * options.outWidth * 2 >= 16384) {
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int) Math.pow(2d, Math.floor(
                            Math.log(sampleSize) / Math.log(2d)));
        }

        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);

        return output;
    }

}
