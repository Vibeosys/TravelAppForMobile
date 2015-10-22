package com.vibeosys.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.vibeosys.travelapp.data.ImageUploadDTO;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.util.NetworkUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mahesh on 10/13/2015.
 */
public class PreviewImage extends FragmentActivity{
SharedPreferences sharedPref;
NewDataBase newDataBase=null;
String imageData=null;
    public static final String MyPREFERENCES = "MyPrefs";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewimage);
        Bitmap bitmap=null;
        // Selected image id
        newDataBase=new NewDataBase(getApplicationContext());
        String path=  getIntent().getExtras().getString("Data");
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Log.d("PreviewImage ImagePath",path);
        Toast.makeText(getApplicationContext(),""+path,Toast.LENGTH_SHORT).show();
        Button uploadImageButton=(Button)findViewById(R.id.uploadImageBtu);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                ImageUploadDTO imageUploadDTO = new ImageUploadDTO();
                imageUploadDTO.setImageData(imageData);
                String path = getIntent().getExtras().getString("Data");
                String UserId = sharedPref.getString("UserId", null);
                imageUploadDTO.setImageName(path);
                String SerializedJsonString = gson.toJson(imageUploadDTO);
                if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext())) {
                    String url = getResources().getString(R.string.URL);
                    Log.d("UserId", UserId);
                    String filename=path.substring(path.lastIndexOf("/") + 1);
                    Bitmap myImg = BitmapFactory.decodeFile(path);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Must compress the Image to reduce image size to make upload easy
                    myImg.compress(Bitmap.CompressFormat.PNG, 10, stream);
                    byte[] byte_arr = stream.toByteArray();
                    // Encode Image to String
                  String  encodedString = Base64.encodeToString(byte_arr, 0);
                    uploadImage(encodedString,filename);

                } else {
                    try {
                        newDataBase.addDataToSync("MyImages", UserId, SerializedJsonString);
                        LayoutInflater layoutInflater = getLayoutInflater();
                        View view = layoutInflater.inflate(R.layout.cust_toast, null);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setView(view);//setting the view of custom toast layout
                        toast.show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.previewmyimage);
        FileInputStream in;
        BufferedInputStream buf;
        Bitmap bMap;
        try {
            in = new FileInputStream(path);
            buf = new BufferedInputStream(in);
            bMap = BitmapFactory.decodeStream(buf);
            imageData=getStringImage(bMap);
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
        /*bitmap = decodeURI(path);
        imageView.setImageBitmap(bitmap);*/
    }

    public void uploadImage(final String encodedString ,final String filename) {

        RequestQueue rq = Volley.newRequestQueue(this);
        String url = "http://192.168.1.6/travelwebapp/api/v1/images/upload1";
        Log.d("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("RESPONSE", response);
                  //  JSONObject json = new JSONObject(response);

                    Toast.makeText(getBaseContext(),
                            "The image is upload", Toast.LENGTH_SHORT)
                            .show();

                } catch (Exception e) {
                    Log.d("JSON Exception", e.toString());
                    Toast.makeText(getBaseContext(),
                            "Error while loadin data!",
                            Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "Error [" + error + "]");
                Toast.makeText(getBaseContext(),
                        "Cannot connect to server", Toast.LENGTH_LONG)
                        .show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("upload", encodedString);
                params.put("imagename", filename);

                return params;

            }

        };

        rq.add(stringRequest);
    }


    public Bitmap decodeURI(String filePath){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if(options.outHeight * options.outWidth * 2 >= 50000){
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int)Math.pow(2d, Math.floor(
                            Math.log(sampleSize)/Math.log(2d)));
        }

        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);

        return output;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


}
