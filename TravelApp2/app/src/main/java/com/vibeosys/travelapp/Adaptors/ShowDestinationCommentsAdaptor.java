package com.vibeosys.travelapp.Adaptors;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.view.RoundedImageView;
import com.vibeosys.travelapp.data.UserCommentDTO;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class ShowDestinationCommentsAdaptor extends BaseAdapter {
    private Context mContext;
    private List<UserCommentDTO> mListDestinationComments = new ArrayList<>();

    public ShowDestinationCommentsAdaptor(Context destinationComments, List<UserCommentDTO> mListDestination) {
        mContext = destinationComments;
        updateResults(mListDestination);
    }

    public void updateResults(List<UserCommentDTO> results) {
        mListDestinationComments = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        if (mListDestinationComments != null) return mListDestinationComments.size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        return mListDestinationComments.get(position).getUserId();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View viewrow = convertView;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.destination_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.userNametext1);
            viewHolder.textView1 = (TextView) convertView.findViewById(R.id.userNameComment2);
            viewHolder.imageView = (RoundedImageView) convertView.findViewById(R.id.userimagedest);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView1.setText(mListDestinationComments.get(position).getCommentText());
        viewHolder.textView.setText(mListDestinationComments.get(position).getUserName());
        RoundedImageView roundedImageView = (RoundedImageView) viewHolder.imageView;
        String photoUrl = mListDestinationComments.get(position).getUserPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            downloadImageAsync(photoUrl, roundedImageView);
        }

        return convertView;
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    protected static synchronized void downloadImageAsync(final String url, final ImageView imageView) {
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {

            @Override
            public Bitmap doInBackground(Void... params) {
                URL imageUrl;
                Bitmap imageBitmap = null;
                URLConnection imageUrlConnection;
                InputStream imageInputStream = null;
                try {

                    imageUrl = new URL(url);
                    imageUrlConnection = imageUrl.openConnection();
                    imageInputStream = imageUrlConnection.getInputStream();

                    imageBitmap = BitmapFactory.decodeStream(imageInputStream);
                } catch (MalformedURLException e) {
                    Log.e("DownloadImgBkgErr", "TravelAppError occurred while downloading profile image in background " + e.toString());
                } catch (Exception e) {
                    Log.e("DownloadImgBkgErr", "TravelAppError occurred while downloading profile image in background " + e.toString());
                } finally {
                    try {
                        if (imageInputStream != null)
                            imageInputStream.close();
                    } catch (IOException e) {
                        Log.e("StreamClose", "Error occurred while closing input stream for profile image");
                    }
                }
                return imageBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null)
                    imageView.setImageBitmap(result);
            }

        };
        task.execute();
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView textView1;
    }

}
