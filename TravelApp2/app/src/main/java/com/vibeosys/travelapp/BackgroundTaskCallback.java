package com.vibeosys.travelapp;

/**
 * Callback for handling async task states
 */
public interface BackgroundTaskCallback {

    public void onSuccess(String aData,int id);
    public void onFailure(String aData,int id);


}
