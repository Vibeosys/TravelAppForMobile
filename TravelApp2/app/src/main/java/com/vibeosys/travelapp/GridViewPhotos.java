package com.vibeosys.travelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.vibeosys.travelapp.data.DbImageDTO;
import com.vibeosys.travelapp.data.ImageUploadDTO;
import com.vibeosys.travelapp.data.TravelAppError;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.ImageFileUploader;
import com.vibeosys.travelapp.util.ImageUploadNameConstants;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.SessionManager;
import com.vibeosys.travelapp.util.UserAuth;
import com.vibeosys.travelapp.view.LoaderImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mahesh on 10/12/2015.
 */
public class GridViewPhotos extends BaseActivity
        implements ImageFileUploader.OnUploadCompleteListener, ImageFileUploader.OnUploadErrorListener {

    private GridView mGridViewPhotos;
    //List<String> mImages;
    //private static final int IMAGE_CAPTURE_CODE = 100;
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
    //private NewDataBase mNewDataBase;
    private int DestId;
    //SessionManager mSessionManager;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridviewphotos);
        setTitle("Choose Photos");
        //SessionManager.Instance();
        mSessionManager = SessionManager.getInstance(getApplicationContext());
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
            mGridViewPhotos.invalidate();

        }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Uploading Image...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final String imagePath = imageUri.getPath();
                //String date = DateFormat.getDateTimeInstance().format(new Date());
                mNewDataBase.saveInMyImages(imagePath, new Date());
                Gson gson = new Gson();
                ImageUploadDTO imageUploadDTO = new ImageUploadDTO();
                imageUploadDTO.setImageData(imagePath);

                imageUploadDTO.setImageName(imagePath);
                String SerializedJsonString = gson.toJson(imageUploadDTO);
                if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext())) {
                    final String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);

                    final ImageFileUploader imageFileUploader = new ImageFileUploader(this);
                    imageFileUploader.setOnUploadCompleteListener(this);
                    imageFileUploader.setOnUploadErrorListener(this);

                    new Thread() {
                        public void run() {
                            try {
                                imageFileUploader.uploadDestinationImage(imagePath, filename, DestId);
                            } catch (Exception ex) {
                                Log.e("ExceptionGridImgUp", "TravelAppError uploading the captured photo");
                            } finally {
                                if (mProgressDialog != null && mProgressDialog.isShowing())
                                    mProgressDialog.dismiss();
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

    private void captureImage() {
        if (!UserAuth.isUserLoggedIn(getApplicationContext()))
            return;

        Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        takephoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takephoto, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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
                Log.e("DestImageAwsSave", "AWS image " + travelAppError.getMessage() + " for " + DestId + " not saved in db");
        }

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

    private class GalleryAdapter extends CursorAdapter implements AdapterView.OnItemClickListener {

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

    static class ViewHolder {
        LoaderImageView image;
    }
}
