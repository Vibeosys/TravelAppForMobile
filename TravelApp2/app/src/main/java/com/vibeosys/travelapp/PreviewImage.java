package com.vibeosys.travelapp;

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
import com.vibeosys.travelapp.data.ImageUploadDTO;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.ImageFileUploader;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.UserAuth;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * Created by mahesh on 10/13/2015.
 */

public class PreviewImage extends BaseActivity
        implements View.OnClickListener,
        ImageFileUploader.OnUploadCompleteListener, ImageFileUploader.OnUploadErrorListener {

    private NewDataBase newDataBase = null;
    private String imageData = null;
    private ProgressDialog progress;
    //SessionManager mSessionManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewimage);
        setTitle("Preview Image");
        // Selected image id
        newDataBase = new NewDataBase(getApplicationContext());
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
                    if (in != null) {
                        in.close();
                    }
                    if (buf != null) {
                        buf.close();
                    }
                } catch (Exception e) {
                    Log.e("Error reading file", e.toString());
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

        progress = new ProgressDialog(PreviewImage.this);
        progress.setMessage("Uploading Image...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();

        Gson gson = new Gson();
        ImageUploadDTO imageUploadDTO = new ImageUploadDTO();
        imageUploadDTO.setImageData(imageData);
        String path = getIntent().getExtras().getString("Data");
        Log.d("Path File ", path);
        //String UserId = mSessionManager.Instance().getUserId();
        int DestId = getIntent().getExtras().getInt("DestId");
        imageUploadDTO.setImageName(path);
        String SerializedJsonString = gson.toJson(imageUploadDTO);
        if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext())) {

            String filename = path.substring(path.lastIndexOf("/") + 1);

            ImageFileUploader imageFileUploader = new ImageFileUploader(this);
            imageFileUploader.setOnUploadCompleteListener(this);
            imageFileUploader.setOnUploadErrorListener(this);
            imageFileUploader.uploadDestinationImage(path, filename, DestId);

        } else {
            try {
                newDataBase.addDataToSync("MyImages", mSessionManager.Instance().getUserId(), SerializedJsonString);
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
    public void onUploadComplete(String uploadJsonResponse) {

        Log.i("DestinationUploadJSON", uploadJsonResponse);

        if (progress != null && progress.isShowing())
            progress.dismiss();

        this.finish();
    }

    @Override
    public void onUploadError(VolleyError error) {
        if (progress != null && progress.isShowing())
            progress.dismiss();

        Log.e("DestinationUploadError",error.toString());
        this.finish();
    }
}
