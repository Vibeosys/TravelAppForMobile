package com.vibeosys.travelapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mahesh on 10/8/2015.
 */
public class NetworkUtils {


    /* Method to check network availability
    * */
    public boolean isActiveNetworkAvailable(Context aContext){

        boolean theStatus = false;
        ConnectivityManager theConManager = (ConnectivityManager)aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo theNetInfo = theConManager.getActiveNetworkInfo();
        if(theNetInfo != null) {
            theStatus = theNetInfo.isConnected();
        }
        return theStatus;

    }
}
