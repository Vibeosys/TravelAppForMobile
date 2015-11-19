package com.vibeosys.travelapp.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vibeosys.travelapp.LoginActivity;
import com.vibeosys.travelapp.data.UploadUser;
import com.vibeosys.travelapp.data.UploadUserOtp;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anand on 02-11-2015.
 */
public class UserAuth {

    private OnUpdateUserResultReceived mOnUpdateUserResultReceived;

    public static boolean isUserLoggedIn(Context context, String userName, String userEmailId) {
        if (userEmailId == null || userEmailId == "" || userName == null || userName == "") {
            Intent theLoginIntent = new Intent(context, LoginActivity.class);
            //theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

    public static boolean isUserLoggedIn() {
        String theUserEmailId = SessionManager.Instance().getUserEmailId();
        String theUserName = SessionManager.Instance().getUserName();
        //String theUserPhotoURL = SessionManager.Instance().getUserPhotoUrl();

        if (theUserEmailId == null || theUserEmailId == "" || theUserName == null || theUserName == "") {
            return false;
        }
        return true;
    }

    public void saveAuthenticationInfo(User userInfo, final Context context) {
        if (userInfo == null)
            return;

        if (userInfo.getEmailId() == null || userInfo.getEmailId() == "" ||
                userInfo.getUserName() == null || userInfo.getUserName() == "")
            return;

        SessionManager theSessionManager = SessionManager.getInstance(context);
        theSessionManager.setUserName(userInfo.getUserName());
        theSessionManager.setUserEmailId(userInfo.getEmailId());
        theSessionManager.setUserPhotoUrl(userInfo.getPhotoURL());
        theSessionManager.setUserLoginRegdSource(userInfo.getLoginSource());
        theSessionManager.setUserRegdApiKey(userInfo.getApiKey());

        updateUserDetailsOnServer(context, userInfo);
    }

    private boolean postUserUpdate(User userInfo, Context context) {
        NewDataBase newDataBase = new NewDataBase(context, SessionManager.getInstance(context));
        boolean isRecordUpdated = newDataBase.updateUserAuthenticationInfo(userInfo);
        boolean isRecordAddedToAllUsers = newDataBase.addOrUpdateUserToAllUsers(userInfo);
        return isRecordUpdated && isRecordAddedToAllUsers;
    }

    public static boolean CleanAuthenticationInfo() {

        SessionManager theSessionManager = SessionManager.Instance();
        theSessionManager.setUserName(null);
        theSessionManager.setUserEmailId(null);
        theSessionManager.setUserPhotoUrl(null);
        theSessionManager.setUserLoginRegdSource(RegistrationSourceTypes.NONE);
        theSessionManager.setUserRegdApiKey(null);

        return true;
    }

    private void updateUserDetailsOnServer(final Context context, final User userInfo) {
        Gson gson = new Gson();
        UploadUser uploadUser = new UploadUserOtp(
                userInfo.getUserId(),
                userInfo.getEmailId(),
                userInfo.getUserName(),
                userInfo.getPassword());
        final String encodedString = gson.toJson(uploadUser);
        //RequestQueue rq = Volley.newRequestQueue(this);
        String updateUsersDetailsUrl = SessionManager.Instance().getUpdateUserDetailsUrl();
        Log.d("Encoded String", encodedString);
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST,
                updateUsersDetailsUrl, encodedString, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                String resultantCode = null;
                try {
                    resultantCode = response.getString("errorCode");
                } catch (JSONException e) {
                    Log.e("JSON Exception", e.toString());
                }

                Integer errorCode = Integer.parseInt(resultantCode);
                if (errorCode == 0) {
                    postUserUpdate(userInfo, context);
                }

                if (mOnUpdateUserResultReceived != null)
                    mOnUpdateUserResultReceived.onUpdateUserResult(errorCode);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UPLOADUSERDETAILSERROR", "TravelAppError [" + error.getMessage() + "]");
            }

        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(jsonArrayRequest);
    }

    public void setOnUpdateUserResultReceived(OnUpdateUserResultReceived onUpdateUserResultReceived) {
        this.mOnUpdateUserResultReceived = onUpdateUserResultReceived;
    }

    public interface OnUpdateUserResultReceived {
        void onUpdateUserResult(int errorCode);
    }
}
