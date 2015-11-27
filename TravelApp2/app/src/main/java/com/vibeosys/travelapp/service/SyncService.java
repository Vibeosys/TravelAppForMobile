package com.vibeosys.travelapp.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.vibeosys.travelapp.MainActivity;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.util.DbTableNameConstants;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.ServerSyncManager;
import com.vibeosys.travelapp.util.SessionManager;

import java.util.Map;

public class SyncService extends IntentService
        implements ServerSyncManager.OnDownloadReceived {

    private static int notifyId = 1;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SyncService() {
        super(SyncService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        SessionManager mSessionManager = SessionManager.getInstance(getApplicationContext());
        ServerSyncManager mServerSyncManager = new ServerSyncManager(getApplicationContext(), mSessionManager);
        mServerSyncManager.setOnDownloadReceived(this);

        while (true) {
            synchronized (this) {
                try {
                    //TODO: Hardcoded time for now, need to read from properties
                    wait(10 * 1000);

                    if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext()))
                        mServerSyncManager.syncDataWithServer(false);

                } catch (Exception e) {
                    Log.e("SyncService", "Error occurred in background service " + e.toString());
                }
            }
        }
    }

    @Override
    public void onDownloadResultReceived(@NonNull Map<String, Integer> results) {
        String showMessage = "";
        for (java.util.Map.Entry<String, Integer> entry : results.entrySet()) {
            String key = entry.getKey();
            String msgKey = null;
            if (key.equals(DbTableNameConstants.DESTINATION))
                msgKey = "destinations";
            else if (key.equals(DbTableNameConstants.USER))
                msgKey = "users";
            showMessage += entry.getValue() + " new " + msgKey + " are added\n";
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cast_ic_notification_on)
                        .setContentTitle("Safar Ka Sathi Updates")
                        .setContentText(showMessage);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifyId++;
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notifyId, mBuilder.build());
    }
}
