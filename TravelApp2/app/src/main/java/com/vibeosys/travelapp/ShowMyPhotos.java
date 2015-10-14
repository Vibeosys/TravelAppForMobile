package com.vibeosys.travelapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by mahesh on 10/3/2015.
 */
public class ShowMyPhotos extends AppCompatActivity{
ListView showphoto_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showmyphoto_layout);
        getSupportActionBar().setTitle("My Photos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

showphoto_view=(ListView)findViewById(R.id.grid_images);

showphoto_view.setAdapter(new ImageAdapter(this));
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
class ImageAdapter extends BaseAdapter{
    private Context mContext;
int [] mThumbIds= new int[]{
        R.drawable.eiffeltower, R.drawable.dubaiimage, R.drawable.bridgeimage, R.drawable.beachimg, R.drawable.europeimg, R.drawable.ukimg
};

    public int getCount() {
        return mThumbIds.length;
    }
    public Object getItem(int position) {
        return mThumbIds[position];
    }
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder = null;
        Bitmap bmp = null;
        if(row==null) {
            LayoutInflater theLayoutInflator = (LayoutInflater)mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);;
            row = theLayoutInflator.inflate(R.layout.gridviewsource, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) row.findViewById(R.id.viewImage);
            row.setTag(viewHolder);

        }
        else viewHolder = (ViewHolder) row.getTag();

         //   bmp = decodeURI();

            //BitmapFactory.decodeFile(mUrls[position].getPath());
            viewHolder.imageView.setImageBitmap(bmp);

        return row;


    }

    public ImageAdapter(Context c) {
        mContext = c;
    }


    private static class ViewHolder {
    ImageView imageView;
    TextView textView;
    }
    public Bitmap decodeURI(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if(options.outHeight * options.outWidth * 2 >= 16384){
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int)Math.pow(2d, Math.floor(
                            Math.log(sampleSize)/Math.log(2d)));
        }

        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);

        return output;
    }

}
