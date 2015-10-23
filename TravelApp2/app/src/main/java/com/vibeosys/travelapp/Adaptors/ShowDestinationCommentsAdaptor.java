package com.vibeosys.travelapp.Adaptors;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeosys.travelapp.CommentsAndLikes;
import com.vibeosys.travelapp.MyImageDB;
import com.vibeosys.travelapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class ShowDestinationCommentsAdaptor extends BaseAdapter {
 Context mContext;
    List<MyImageDB> mUserImagesList = null;
    int DestId;
    List<CommentsAndLikes> mListDestinationComments=new ArrayList<>();
    public ShowDestinationCommentsAdaptor(Context destinationComments, List<CommentsAndLikes> mListDestination, int destId) {
    this.mContext=destinationComments;
    this.mListDestinationComments=mListDestination;
        this.DestId=destId;
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
  if(mListDestinationComments!=null)  return mListDestinationComments.size();
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
        View viewrow=convertView;
        ViewHolder viewHolder = null;
        if(viewrow==null) {
            LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewrow=layoutInflater.inflate(R.layout.destination_comment,null);
            viewHolder=new ViewHolder();
            viewHolder.textView=(TextView)viewrow.findViewById(R.id.userNametext1);
            viewHolder.textView1=(TextView)viewrow.findViewById(R.id.userNameComment2);
            viewHolder.imageView=(ImageView)viewrow.findViewById(R.id.userimagedest);
            viewrow.setTag(viewHolder);
        }
        else viewHolder=(ViewHolder)viewrow.getTag();
            viewHolder.textView1.setText(mListDestinationComments.get(position).getmCommentText());
            viewHolder.textView.setText(mListDestinationComments.get(position).getUserName());

            return viewrow;
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
    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView textView1;
    }
    public Bitmap roundCornerImage(Bitmap raw, float round) {
        int width = raw.getWidth();
        int height = raw.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#000000"));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(raw, rect, rect, paint);

        return result;
    }

}
