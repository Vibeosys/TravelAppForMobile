package com.vibeosys.travelapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.List;

/**
 * Created by mahesh on 10/3/2015.
 */
public class ShowMyPhotos extends AppCompatActivity {
    ListView showphoto_view;
    List<MyImageDB> mUserImagesList = null;
    NewDataBase newDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showmyphoto_layout);
        getSupportActionBar().setTitle("My Photos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        showphoto_view = (ListView) findViewById(R.id.grid_images);
        newDataBase = new NewDataBase(this);
        try {
            mUserImagesList = newDataBase.mUserImagesList();
            showphoto_view.setAdapter(new ImageAdapter(this, mUserImagesList));
        }catch (NullPointerException e){
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

class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyImageDB> myImageDBs;

    ImageAdapter(Context context, List<MyImageDB> myimages) {
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
        ViewHolder viewHolder = null;
        Bitmap bmp = null;
        if (row == null) {
            LayoutInflater theLayoutInflator = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = theLayoutInflator.inflate(R.layout.gridviewsource, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) row.findViewById(R.id.viewImage);
            viewHolder.textView = (TextView) row.findViewById(R.id.dateText);
            row.setTag(viewHolder);

        } else viewHolder = (ViewHolder) row.getTag();

        bmp = decodeURI(myImageDBs.get(position).getmImagePath());
        //BitmapFactory.decodeFile(mUrls[position].getPath());
        String theDate=myImageDBs.get(position).getmCreatedDate();
        String[] splited=theDate.split(",");
        String[] dateYear=splited[1].split("\\s+");
        Log.d("MyPhotos",dateYear[0]);
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
