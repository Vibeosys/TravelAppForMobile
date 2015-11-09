package com.vibeosys.travelapp.tasks;

/**
 * Callback for handling async task states
 */
public interface BackgroundTaskCallback {
    void onResultReceived(String downloadedJson);
}
