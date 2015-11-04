package com.vibeosys.travelapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by anand on 03-11-2015.
 */
public class ImageFileUploader {
    private Context mContext;
    //private Activity mActivity;
    private OnUploadCompleteListener mOnUploadCompleteListener;
    private OnUploadErrorListener mOnUploadErrorListener;

    public ImageFileUploader(Context context) {
        mContext = context;
        //mActivity = activity;
    }

    public void uploadDestinationImage(final String filePath,
                                       final String filename, final int destId) {
        String base64EncodedString = getBase64EncodedStringFromImage(filePath);
        Map<String, String> dataMap = getDestinationImageParams(base64EncodedString,
                filename, String.valueOf(destId), SessionManager.Instance().getUserId(),
                SessionManager.Instance().getUserEmailId(), SessionManager.Instance().getUserName());
        uploadImage(dataMap);
    }

    public void uploadUserProfileImage(final Bitmap bitmapFile) {
        String base64EncodedString = getBase64EncodedStringFromImage(bitmapFile);
        Map<String, String> dataMap = getUserProfileImageParams(base64EncodedString,
                UUID.randomUUID().toString(), SessionManager.Instance().getUserId(),
                SessionManager.Instance().getUserEmailId(), SessionManager.Instance().getUserName());
        uploadImage(dataMap);
    }

    private String getBase64EncodedStringFromImage(String filePath) {
        Bitmap bitmapFile = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmapFile.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        String encodedString = Base64.encodeToString(byte_arr, 0);
        return encodedString;
    }

    private String getBase64EncodedStringFromImage(Bitmap bitmapFile) {
        //Bitmap bitmapFile = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmapFile.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        String encodedString = Base64.encodeToString(byte_arr, 0);
        return encodedString;
    }

    private void uploadImage(final Map<String, String> dataMap) {
        RequestQueue rq = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                SessionManager.Instance().getUploadImagesUrl(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    Log.e("RESPONSE", response);
                    //progressDialog.dismiss();
                    Toast.makeText(mContext,
                            "The image is upload", Toast.LENGTH_SHORT)
                            .show();


                } catch (Exception e) {
                    Log.d("JSON Exception", e.toString());
                    Toast.makeText(mContext,
                            "Error while loadin data!",
                            Toast.LENGTH_LONG).show();

                } finally {
                    if (mOnUploadCompleteListener != null)
                        mOnUploadCompleteListener.onUploadComplete(response);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Error [" + error + "]");
                //progressDialog.dismiss();
                Toast.makeText(mContext,
                        "Cannot connect to server", Toast.LENGTH_LONG)
                        .show();

                if (mOnUploadErrorListener != null)
                    mOnUploadErrorListener.onUploadError(error);

            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                return dataMap;
            }

        };

        rq.add(stringRequest);
    }

    private Map<String, String> getDestinationImageParams
            (String encodedString, String filename, String destId,
             String userId, String userEmailId, String userName) {
        UUID uuid = UUID.randomUUID();
        Map<String, String> params = new HashMap<>();
        params.put("imageId", uuid.toString());
        params.put("upload", encodedString);
        params.put("imagename", filename);
        params.put("destId", String.valueOf(destId));
        params.put("userId", userId);
        params.put("emailId", userEmailId);
        params.put("userName", userName);
        return params;
    }

    private Map<String, String> getUserProfileImageParams
            (String encodedString, String filename,
             String userId, String userEmailId, String userName) {
        UUID uuid = UUID.randomUUID();
        Map<String, String> params = new HashMap<>();
        params.put("imageId", uuid.toString());
        params.put("upload", encodedString);
        params.put("imagename", filename);
        params.put("userId", userId);
        params.put("emailId", userEmailId);
        params.put("userName", userName);
        return params;
    }

    public void setOnUploadCompleteListener(@Nullable OnUploadCompleteListener onUploadCompleteListener) {
        mOnUploadCompleteListener = onUploadCompleteListener;
    }

    public void setOnUploadErrorListener(@Nullable OnUploadErrorListener onUploadErrorListener) {
        mOnUploadErrorListener = onUploadErrorListener;
    }

    public interface OnUploadCompleteListener {
        void onUploadComplete(String uploadJsonResponse);
    }

    public interface OnUploadErrorListener {
        void onUploadError(VolleyError error);
    }
}
