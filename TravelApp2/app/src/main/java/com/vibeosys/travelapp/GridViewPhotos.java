package com.vibeosys.travelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
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
import com.vibeosys.travelapp.view.LoaderImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mahesh on 10/12/2015.
 */
public class GridViewPhotos extends AppCompatActivity {

    GridView mGridViewPhotos;
    List<String> mImages;
    private static final int IMAGE_CAPTURE_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MEDIA_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "TravelPhotos";
    private ProgressDialog ProgressDialog = null;
    private static Uri[] mUrls = null;
    private static String[] strUrls = null;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private String[] mNames = null;
    private Cursor cc = null;
    private Uri imageUri;
    private NewDataBase newDataBase;
    int DestId;
SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridviewphotos);
        setTitle("Choose Photos");
        mGridViewPhotos = (GridView) findViewById(R.id.showgridphotos);
        DestId = getIntent().getExtras().getInt("DestId");
        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";
        cc = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                orderBy);

        if (cc != null) {

            if (cc.moveToFirst()) {
                ProgressDialog = new ProgressDialog(GridViewPhotos.this);
                ProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                ProgressDialog.setMessage("Please Wait");
                //myProgressDialog.setIcon(R.drawable.blind);
                ProgressDialog.show();

                new Thread() {
                    public void run() {
                        try {
                            mUrls = new Uri[cc.getCount()];
                            strUrls = new String[cc.getCount()];
                            mNames = new String[cc.getCount()];
                            for (int i = 0; i < cc.getCount(); i++) {
                                cc.moveToPosition(i);
                                mUrls[i] = Uri.parse(cc.getString(1));
                                strUrls[i] = cc.getString(1);
                                mNames[i] = cc.getString(3);
                                //Log.e("mNames[i]",mNames[i]+":"+cc.getColumnCount()+ " : " +cc.getString(3));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ProgressDialog.dismiss();
                    }
                }.start();

            }

            GalleryAdapter theAdapter = new GalleryAdapter(this, cc);
            mGridViewPhotos.setAdapter(theAdapter);
            mGridViewPhotos.setOnItemClickListener(theAdapter);

            sharedPreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            mGridViewPhotos.invalidate();

        }

        /*mGridViewPhotos.setAdapter(new ShowImages(this));
        mGridViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long aId) {

                Intent theIntent = new Intent(getApplicationContext(), PreviewImage.class);
                theIntent.putExtra("Data", mUrls[position].getPath());
                theIntent.putExtra("id", position);
                theIntent.putExtra("DestId", DestId);
                Log.d("GridViewPhotos DestId", "" + DestId);
                startActivity(theIntent);

            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.get_photo) {
            captureImage();
        }

        return super.onOptionsItemSelected(item);
    }

    private void captureImage() {
        Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        takephoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takephoto, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        Toast.makeText(GridViewPhotos.this, "", Toast.LENGTH_SHORT).show();

    }

    private Uri getOutputMediaFileUri(int mediafile) {
        return Uri.fromFile(getOutputMediaFile(mediafile));

    }

    private File getOutputMediaFile(int mediafile) {

        File fileDIr;
        fileDIr = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!fileDIr.exists()) {
            if (!fileDIr.mkdir()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create"
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = null;
        if (mediafile == MEDIA_IMAGE)
            mediaFile = new File(fileDIr.getAbsolutePath(), "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String date = DateFormat.getDateTimeInstance().format(new Date());
                newDataBase = new NewDataBase(getApplicationContext());
                newDataBase.mSaveMyImages(imageUri.getPath(), date);
                Gson gson = new Gson();
                ImageUploadDTO imageUploadDTO = new ImageUploadDTO();
                imageUploadDTO.setImageData(imageUri.getPath());
               ProgressDialog progress = new ProgressDialog(GridViewPhotos.this);
                progress.setMessage("Uploading Image...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
                String UserId = sharedPreferences.getString("UserId", null);
                imageUploadDTO.setImageName(imageUri.getPath());
                String SerializedJsonString = gson.toJson(imageUploadDTO);
                if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext())) {
                    String url = getResources().getString(R.string.URL);
                    Log.d("UserId", UserId);
                    String filename = imageUri.getPath().substring(imageUri.getPath().lastIndexOf("/") + 1);
                    Bitmap myImg = BitmapFactory.decodeFile(imageUri.getPath());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Must compress the Image to reduce image size to make upload easy
                    myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
                    byte[] byte_arr = stream.toByteArray();
                    // Encode Image to String
                    String encodedString = Base64.encodeToString(byte_arr, 0);
                    uploadImage(encodedString, filename, DestId, UserId);

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(),
                        "User image capture" + imageUri.getPath(), Toast.LENGTH_SHORT)
                        .show();

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void uploadImage(final String encodedString, final String filename, int destId, final String userId) {

        RequestQueue rq = Volley.newRequestQueue(this);
        final String url = getResources().getString(R.string.URL);

        // final String fileName = filename.replaceAll(" ", "");

        Log.d("FileName", filename);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url + "images/upload1", new Response.Listener<String>() {

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
                Log.e("ERROR", "Error [" + error + "]");
                Log.e("URL Called", url);
                Toast.makeText(getBaseContext(),
                        "Cannot connect to server", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("upload", encodedString);
                params.put("imagename", filename);
                params.put("DestId", String.valueOf(DestId));
                params.put("UserId", userId);

                return params;

            }

        };

        rq.add(stringRequest);

    }

    public Bitmap decodeURI(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if (options.outHeight * options.outWidth * 2 >= 16384) {
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int) Math.pow(2d, Math.floor(
                            Math.log(sampleSize) / Math.log(2d)));
        }

        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);

        return output;
    }

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
   /* public static void getCameraImages(Context context) {
        final String[] projection = { MediaStore.usersImages.Media.DATA };
        final String selection = MediaStore.usersImages.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };

        }
    }*/


    private class GalleryAdapter extends CursorAdapter implements AdapterView.OnItemClickListener{

        public GalleryAdapter(Context context, Cursor aCur) {
            super(context, aCur, true);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Cursor cc = getCursor();
            cc.moveToPosition(position);
            Uri theUrl = Uri.parse(cc.getString(1));
            //String theName = cc.getString(3);
            View row = convertView;
            if (row == null) {
                LayoutInflater theLayoutInflator = getLayoutInflater();
                row = theLayoutInflator.inflate(R.layout.gridviewsource, null);
            }

            LoaderImageView image = (LoaderImageView) row.findViewById(R.id.viewImage);
            image.loadImageFromFile(theUrl.getPath());
            return row;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View row = null;
            LayoutInflater theLayoutInflator = getLayoutInflater();
            row = theLayoutInflator.inflate(R.layout.gridviewsource, null);
            //ViewHolder viewHolder = new ViewHolder();
            //row.setTag(viewHolder);
            return row;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long aId) {

            Intent theIntent = new Intent(getApplicationContext(), PreviewImage.class);
            Cursor theCur = getCursor();
            theCur.moveToPosition(position);
            Uri theUri = Uri.parse(theCur.getString(1));
            theIntent.putExtra("Data", theUri.getPath());
            theIntent.putExtra("id", position);
            theIntent.putExtra("DestId", DestId);
            Log.d("GridViewPhotos DestId", "" + DestId);
            startActivity(theIntent);

        }


        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }


    /*
    private class ShowImages extends BaseAdapter {

        Context theContext;

        public ShowImages(Context context) {
            theContext = context;
        }

        @Override
        public int getCount() {
            return cc.getCount();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View row = convertView;
            ViewHolder viewHolder = null;

            if (row == null) {
                LayoutInflater theLayoutInflator = getLayoutInflater();
                row = theLayoutInflator.inflate(R.layout.gridviewsource, null);
                viewHolder = new ViewHolder();
                viewHolder.image = (LoaderImageView) row.findViewById(R.id.viewImage);
                row.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) row.getTag();
            }

            viewHolder.image.loadImageFromFile(mUrls[position].getPath());
            return row;
        }

    }
    */
    static class ViewHolder {
        LoaderImageView image;
    }

}
