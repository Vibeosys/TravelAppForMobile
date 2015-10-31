package com.vibeosys.travelapp.tasks;

/**
 * Callback for handling async task states
 */
public interface BackgroundTaskCallback {
    void onSuccess(String aData);

    void onFailure(String aData);
}
