package com.vibeosys.travelapp.util;

import android.content.Context;
import android.content.Intent;

import com.vibeosys.travelapp.LoginActivity;

/**
 * Created by anand on 02-11-2015.
 */
public class UserAuth {

    public static boolean isUserLoggedIn(Context context, String userName, String userEmailId) {
        if (userEmailId == null || userEmailId == "" || userName == null || userName == "") {
            Intent theLoginIntent = new Intent(context, LoginActivity.class);
            theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(theLoginIntent);
            return false;
        }
        return true;
    }

    public static boolean isUserLoggedIn(Context context) {
        String theUserEmailId = SessionManager.Instance().getUserEmailId();
        String theUserName = SessionManager.Instance().getUserName();
        return isUserLoggedIn(context, theUserName, theUserEmailId);
    }

}
