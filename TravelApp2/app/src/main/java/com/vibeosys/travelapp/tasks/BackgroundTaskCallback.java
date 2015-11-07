package com.vibeosys.travelapp.tasks;

import com.vibeosys.travelapp.data.*;

/**
 * Callback for handling async task states
 */
public interface BackgroundTaskCallback {
    void onSuccess(String downloadedJson);

    void onFailure(TravelAppError appError);
}
