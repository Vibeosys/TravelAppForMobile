package com.vibeosys.travelapp.util;

import android.content.Context;
import android.content.Intent;

import com.vibeosys.travelapp.LoginActivity;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;

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

    public static boolean saveAuthenticationInfo(User userInfo, Context context) {
        if (userInfo == null)
            return false;

        if (userInfo.getEmailId() == null || userInfo.getEmailId() == "" ||
                userInfo.getUserName() == null || userInfo.getUserName() == "")
            return false;

        SessionManager theSessionManager = SessionManager.getInstance(context);
        theSessionManager.setUserName(userInfo.getUserName());
        theSessionManager.setUserEmailId(userInfo.getEmailId());
        theSessionManager.setUserPhotoUrl(userInfo.getPhotoURL());
        theSessionManager.setUserLoginRegdSoure(userInfo.getLoginSource().toString());
        theSessionManager.setUserRegdApiKey(userInfo.getApiKey());

        NewDataBase newDataBase = new NewDataBase(context);
        boolean isRecordUpdated = newDataBase.updateUserAuthenticationInfo(userInfo);
        return isRecordUpdated;
    }

}
