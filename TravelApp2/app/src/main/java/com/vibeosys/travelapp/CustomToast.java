package com.vibeosys.travelapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by mahesh on 10/20/2015.
 */
public class CustomToast extends Toast {
   Context mContext;
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public CustomToast(Context context) {
        super(context);
    }

    @Override
    public void setView(View view) {
        LayoutInflater layoutInflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.cust_toast, null);
        super.setView(view);
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        super.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    }

    @Override
    public void setText(CharSequence s) {
        super.setText("Please Connect to Internet");
    }

    @Override
    public void setDuration(int duration) {
        super.setDuration(LENGTH_LONG);
    }
}
