package com.vibeosys.travelapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by anand on 03-11-2015.
 */
public final class ImageFileUploader {
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
                UUID.randomUUID().toString() + ".jpg", SessionManager.Instance().getUserId(),
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

        StringRequest imageUploadRequest = new StringRequest(Request.Method.POST,
                SessionManager.Instance().getUploadImagesUrl(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (mOnUploadCompleteListener != null)
                    mOnUploadCompleteListener.onUploadComplete(response, dataMap);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mOnUploadErrorListener != null)
                    mOnUploadErrorListener.onUploadError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return dataMap;
            }

        };

        imageUploadRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(imageUploadRequest);
    }

    private Map<String, String> getDestinationImageParams
            (String encodedString, String filename, String destId,
             String userId, String userEmailId, String userName) {
        UUID uuid = UUID.randomUUID();
        JSONObject object;

        Map<String, String> params = new HashMap<>();
        params.put(ImageUploadNameConstants.IMAGE_ID, uuid.toString());
        params.put(ImageUploadNameConstants.UPLOAD, encodedString);
        params.put(ImageUploadNameConstants.IMAGE_NAME, filename);
        params.put(ImageUploadNameConstants.DEST_ID, String.valueOf(destId));
        params.put(ImageUploadNameConstants.USER_ID, userId);
        params.put(ImageUploadNameConstants.EMAIL_ID, userEmailId);
        params.put(ImageUploadNameConstants.USER_NAME, userName);
        return params;
    }

    private Map<String, String> getUserProfileImageParams
            (String encodedString, String filename,
             String userId, String userEmailId, String userName) {
        UUID uuid = UUID.randomUUID();
        Map<String, String> params = new HashMap<>();
        params.put(ImageUploadNameConstants.IMAGE_ID, uuid.toString());
        params.put(ImageUploadNameConstants.UPLOAD, encodedString);
        params.put(ImageUploadNameConstants.IMAGE_NAME, filename);
        params.put(ImageUploadNameConstants.USER_ID, userId);
        params.put(ImageUploadNameConstants.EMAIL_ID, userEmailId);
        params.put(ImageUploadNameConstants.USER_NAME, userName);
        return params;
    }

    public void setOnUploadCompleteListener(@Nullable OnUploadCompleteListener onUploadCompleteListener) {
        mOnUploadCompleteListener = onUploadCompleteListener;
    }

    public void setOnUploadErrorListener(@Nullable OnUploadErrorListener onUploadErrorListener) {
        mOnUploadErrorListener = onUploadErrorListener;
    }

    public interface OnUploadCompleteListener {
        void onUploadComplete(String uploadJsonResponse, Map<String, String> inputParameters);
    }

    public interface OnUploadErrorListener {
        void onUploadError(VolleyError error);
    }
}
