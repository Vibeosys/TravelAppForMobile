package com.vibeosys.travelapp.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.DbImageDTO;
import com.vibeosys.travelapp.data.ImageUploadDTO;
import com.vibeosys.travelapp.data.TravelAppError;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.ImageFileUploader;
import com.vibeosys.travelapp.util.ImageUploadNameConstants;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.UserAuth;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Map;

/**
 * Created by mahesh on 10/13/2015.
 */

public class PreviewImage extends BaseActivity
        implements View.OnClickListener,
        ImageFileUploader.OnUploadCompleteListener, ImageFileUploader.OnUploadErrorListener {

    //private NewDataBase newDataBase = null;
    private String imageData = null;
    private ProgressDialog mProgressDialog;
    //SessionManager mSessionManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewimage);
        setTitle("Preview Image");
        // Selected image id
        //newDataBase = new NewDataBase(getApplicationContext());
        final String path = getIntent().getExtras().getString("Data");
        Log.d("PreviewImage ImagePath", path);

        //Toast.makeText(getApplicationContext(), "" + path, Toast.LENGTH_SHORT).show();
        Button cancelButton = (Button) findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button uploadImageButton = (Button) findViewById(R.id.uploadImageBtu);
        uploadImageButton.setOnClickListener(this);
        final ImageView imageView = (ImageView) findViewById(R.id.previewmyimage);

        imageView.post(new Runnable() {
            @Override
            public void run() {

                FileInputStream in;
                BufferedInputStream buf;
                Bitmap bMap;
                try {
                    in = new FileInputStream(path);
                    buf = new BufferedInputStream(in);
                    bMap = BitmapFactory.decodeStream(buf);
                    imageData = getStringImage(bMap);
                    imageView.setImageBitmap(bMap);
                    //bMap.recycle();
                    if (in != null) {
                        in.close();
                    }
                    if (buf != null) {
                        buf.close();
                    }
                } catch (Exception e) {
                    Log.e("ErrorDownLoad", e.toString());
                }

            }
        });
        /*bitmap = decodeURI(path);
        imageView.setImageBitmap(bitmap);*/
    }

    @Override
    public void onClick(View v) {
        if (!UserAuth.isUserLoggedIn(getApplicationContext()))
            return;

        final String imgLocalPath = getIntent().getExtras().getString("Data");
        Log.d("Path File ", imgLocalPath);

        //final String imagePath = imageUri.getPath();
        //String date = DateFormat.getDateTimeInstance().format(new Date());
        mNewDataBase.saveInMyImages(imgLocalPath, new Date());

        mProgressDialog = new ProgressDialog(PreviewImage.this);
        mProgressDialog.setMessage("Uploading Image...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        Gson gson = new Gson();
        ImageUploadDTO imageUploadDTO = new ImageUploadDTO();
        imageUploadDTO.setImageData(imageData);

        //String UserId = mSessionManager.Instance().getUserId();
        final int DestId = getIntent().getExtras().getInt("DestId");
        imageUploadDTO.setImageName(imgLocalPath);
        String SerializedJsonString = gson.toJson(imageUploadDTO);
        if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext())) {

            final String filename = imgLocalPath.substring(imgLocalPath.lastIndexOf("/") + 1);

            final ImageFileUploader imageFileUploader = new ImageFileUploader(this);
            imageFileUploader.setOnUploadCompleteListener(this);
            imageFileUploader.setOnUploadErrorListener(this);
            new Thread() {
                public void run() {
                    try {
                        imageFileUploader.uploadDestinationImage(imgLocalPath, filename, DestId);
                    } catch (Exception ex) {
                        Log.e("ExceptionGridImgUp", "TravelAppError uploading the captured photo");
                    } finally {
                        //if (mProgressDialog != null && mProgressDialog.isShowing())
                        //    mProgressDialog.dismiss();
                    }
                }
            }.start();

        } else {
            try {
                mNewDataBase.addDataToSync("MyImages", mSessionManager.getUserId(), SerializedJsonString);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.cust_toast, null);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setView(view);//setting the view of custom toast layout
                toast.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onUploadComplete(String uploadJsonResponse, Map<String, String> inputParameters) {

        Log.i("DestinationUploadJSON", uploadJsonResponse);

        TravelAppError travelAppError = new Gson().fromJson(uploadJsonResponse, TravelAppError.class);
        if (travelAppError != null && travelAppError.getMessage() != null && !travelAppError.getMessage().isEmpty()) {
            DbImageDTO awsReceivedImage = new DbImageDTO();
            awsReceivedImage.setImageId(inputParameters.get(ImageUploadNameConstants.IMAGE_ID));
            awsReceivedImage.setImagePath(travelAppError.getMessage());
            awsReceivedImage.setDestId(Integer.parseInt(inputParameters.get(ImageUploadNameConstants.DEST_ID)));
            awsReceivedImage.setUserId(inputParameters.get(ImageUploadNameConstants.USER_ID));
            awsReceivedImage.setImageSeen(false);
            boolean imageSaved = mNewDataBase.insertImage(awsReceivedImage);

            if (!imageSaved)
                Log.e("DestImageAwsSave", "AWS image " + travelAppError.getMessage() + " for " + awsReceivedImage.getDestId() + " not saved in db");
        }

        //mNewDataBase.insertImage
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT)
                .show();

        this.finish();
    }

    @Override
    public void onUploadError(VolleyError error) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        Log.e("DestinationUploadError", error.toString());
        this.finish();
    }
}
