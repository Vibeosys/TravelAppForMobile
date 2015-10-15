package com.vibeosys.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class UserLikeAdaptor extends BaseAdapter {
    Context mContext;
    List<CommentsAndLikes> mUserList;

    public UserLikeAdaptor(Context applicationContext, List<CommentsAndLikes> mUserLikesList) {
   this.mContext=applicationContext;
   this.mUserList=mUserLikesList;
    }

    @Override
    public int getCount() {
     if(mUserList!=null)   return mUserList.size();
      else return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return mUserList.get(position).getUserId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder=null;
        TextView muser=null;
        if (view == null) {
            LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.userlikelist, null);
            viewHolder = new ViewHolder();
            viewHolder.textswitcher = (TextSwitcher) view.findViewById(R.id.countlikestext);
            viewHolder.textview = (TextView) view.findViewById(R.id.userlikesname);
            viewHolder.imageview = (ImageView) view.findViewById(R.id.likebutton);
            muser = new TextView(mContext);
            view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();
        viewHolder.textview.setText(mUserList.get(position).getUsername());
        muser.setText(String.valueOf(mUserList.get(position).getmLikeCount()));
        viewHolder.textswitcher.addView(muser);
        return view;
    }
private static class ViewHolder{
        TextSwitcher textswitcher;
        ImageView imageview;
        TextView textview;
    }

}
