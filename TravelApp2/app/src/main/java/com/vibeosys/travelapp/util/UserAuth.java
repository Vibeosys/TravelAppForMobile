package com.vibeosys.travelapp.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vibeosys.travelapp.LoginActivity;
import com.vibeosys.travelapp.data.UploadUser;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anand on 02-11-2015.
 */
public class UserAuth {

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

    public static boolean saveAuthenticationInfo(User userInfo, final Context context) {
        if (userInfo == null)
            return false;

        if (userInfo.getEmailId() == null || userInfo.getEmailId() == "" ||
                userInfo.getUserName() == null || userInfo.getUserName() == "")
            return false;

        SessionManager theSessionManager = SessionManager.getInstance(context);
        theSessionManager.setUserName(userInfo.getUserName());
        theSessionManager.setUserEmailId(userInfo.getEmailId());
        theSessionManager.setUserPhotoUrl(userInfo.getPhotoURL());
        theSessionManager.setUserLoginRegdSoure(userInfo.getLoginSource());
        theSessionManager.setUserRegdApiKey(userInfo.getApiKey());

                /*new Thread(
                new Runnable() {
                    @Override
                    public void run() {*/
        updateUserDetailsOnServer(context);
                    /*}
                }).run();
*/
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
        theSessionManager.setUserLoginRegdSoure(RegistrationSourceTypes.NONE);
        theSessionManager.setUserRegdApiKey(null);

        return true;
    }

    public static void updateUserDetailsOnServer(Context context) {
        Gson gson = new Gson();
        String UserId = SessionManager.Instance().getUserId();
        String EmailId = SessionManager.Instance().getUserEmailId();

        String UserName = SessionManager.Instance().getUserName();
        UploadUser uploadUser = new UploadUser(UserId, EmailId, UserName);

        final String encodedString = gson.toJson(uploadUser);
        //RequestQueue rq = Volley.newRequestQueue(this);
        String updateUsersDetailsUrl = SessionManager.Instance().getUpdateUserDetailsUrl();
        Log.d("Encoded String", encodedString);
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST,
                updateUsersDetailsUrl, encodedString, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jresponse = response;//.getJSONObject(0);
                    Log.i("updateUserDetails", jresponse.toString());

                    String res = jresponse.getString("message");
                    String code = jresponse.getString("errorCode");
                    if (code.equals("0")) {
                        Log.i("UpdateUserDetails", response.toString());
                    }

                    if (code.equals("100")) {
                        Log.e("TravelAppError", "User Not Authenticated..");
                    }
                    if (code.equals("101")) {
                        Log.e("TravelAppError", "User Id is Blanck");
                    }
                    if (code.equals("102")) {
                        Log.e("TravelAppError", "Unknown TravelAppError");
                    }


                } catch (JSONException e) {
                    Log.e("JSON Exception", e.toString());

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UPLOADUSERDETAILSERROR", "TravelAppError [" + error.getMessage() + "]");
            }

        });

        rq.add(jsonArrayRequest);
    }

}
