package com.vibeosys.travelapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleMapOptions googleMapOptions;
    private Marker myMarker;
    AutoCompleteTextView text_dest;
    Uri imageURI;
    private static final int IMAGE_CAPTURE_CODE = 100;
    private static final int CAMERA_CAPTURE_REQUEST_CODE = 100;
    private static final int MEDIA_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "TravelPhotos";
    List<Destination> mDestList;

    List<GetTemp> mTempDataList;
    NewDataBase newDataBase;
    List<GetTemp> mTempDataShowList;
    List<MyDestination> mDestinationList;
    SharedPreferences sharedPref;
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text_dest = (AutoCompleteTextView) findViewById(R.id.dest_text);

        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mDestList = new ArrayList<Destination>();
        Destination destination = new Destination();
        destination.setmDestId(101);
        destination.setmDestName("Pune");
        destination.setmLat(18.5203);
        destination.setmLong(73.8567);

        Destination destination1 = new Destination();
        destination1.setmDestId(102);
        destination1.setmDestName("Mumbai");
        destination1.setmLat(18.9750);
        destination1.setmLong(72.8258);

        Destination destination2 = new Destination();
        destination2.setmDestId(103);
        destination2.setmDestName("Hyderabad");
        destination2.setmLat(17.3700);
        destination2.setmLong(78.4800);

        Destination destination3 = new Destination();
        destination3.setmDestId(104);
        destination3.setmDestName("Chennai");
        destination3.setmLat(13.0827);
        destination3.setmLong(80.2707);

        Destination destination4 = new Destination();
        destination4.setmDestId(105);
        destination4.setmDestName("VijayWada");
        destination4.setmLat(16.5083);
        destination4.setmLong(80.6417);

        Destination destination5 = new Destination();
        destination5.setmDestId(106);
        destination5.setmDestName("Nanded");
        destination5.setmLat(19.1500);
        destination5.setmLong(77.3000);
        mDestList.add(destination);
        mDestList.add(destination1);
        mDestList.add(destination2);
        mDestList.add(destination3);
        mDestList.add(destination4);
        mDestList.add(destination5);

        List<String> mDestinationNames = new ArrayList<String>();
        UserDetails userDetails = new UserDetails();
        newDataBase = new NewDataBase(getApplicationContext());
        // newDataBase.AddUser(UserId,UserName);
//        newDataBase.GetUser();

        newDataBase.addDestinations(mDestList);
        mDestinationNames = newDataBase.getDestNames();
        mDestinationList = new ArrayList<>();
        mDestinationList = newDataBase.GetFromTempLatLong();
      /* boolean temp=true;
        if(temp) newDataBase.DeleteTempMaps();
        else temp=false;*/
//       Log.d("MainActivity",String.valueOf(mDestinationList.size()));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDestinationNames);
        text_dest.setAdapter(arrayAdapter);
          /*  mTempDataShowList = new ArrayList<>();
            try {
                if (newDataBase.CheckTempData()) {
                    mTempDataShowList = newDataBase.GetFromTemp();//getting saved marker data from user
                    for (int i = 0; i < mTempDataShowList.size(); i++) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mTempDataShowList.get(i).getLat(), mTempDataShowList.get(i).getLong())).title(mTempDataShowList.get(i).getDestName()));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(theCurrentLatLong));
                    }

                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
          */
        text_dest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //int id1=(Integer) parent.getSelectedItem();
                Log.d("Clicked", "Text Button");
                text_dest.setText("");
                text_dest.clearListSelection();
                text_dest.clearFocus();
                DestinationTempData destinationTempData = null;
                String mDestName = null;
                List<TempData> mCurrentDestinationData = new ArrayList<>();
                mDestName = (String) parent.getItemAtPosition(position);
                mCurrentDestinationData = newDataBase.GetLatLong(mDestName);//Get Lat Long of DestName
                Log.d("MainActivitymTempData ", mCurrentDestinationData.toString());
                mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentDestinationData.get(0).getmLat(), mCurrentDestinationData.get(0).getmLong())).title(mDestName));
                Log.d("MainActivity", String.valueOf(mCurrentDestinationData.get(0).getmLat()));


                newDataBase.SaveMapInTemp(mCurrentDestinationData, mDestName);


                destinationTempData = newDataBase.mGetLatLongFromTemp(mCurrentDestinationData.get(0).getmDestId());//Get Last Known Lat Long from Temp

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(mCurrentDestinationData.get(0).getmLat(), mCurrentDestinationData.get(0).getmLong()));
                mMap.moveCamera(center);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(7);
                mMap.animateCamera(zoom);
                //Log.d("TempData",String.valueOf(mTempData.size()));
                if (destinationTempData != null) {

                    mMap.addPolyline(new PolylineOptions().geodesic(true)
                                    .add(new LatLng(mCurrentDestinationData.get(0).getmLat(), mCurrentDestinationData.get(0).getmLong()))
                                    .add(new LatLng(destinationTempData.getmLat(), destinationTempData.getmLong())).width(5).color(Color.BLACK)
                    );
                }

            }
        });

        Button button = (Button) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newDataBase.CheckTempData()) {
                    final Dialog dialog;
                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.save_map_conform);
                    dialog.setTitle("Save Map");
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();
                    Button mSaveMapButton = (Button) dialog.findViewById(R.id.SaveMapButton);
                    Button mCancelMapButton = (Button) dialog.findViewById(R.id.CancelMapButton);
                    final EditText mMapTitle = (EditText) dialog.findViewById(R.id.mapNameText);
                    mSaveMapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mMapTitle.getText().toString().length() > 0 && mMapTitle.getText().toString() != null) {
                                String mMapName = mMapTitle.getText().toString();
                                mTempDataList = new ArrayList<>();
                                mTempDataList = newDataBase.GetFromTemp();
                                JSONArray jsonArray = new JSONArray();
                                ;
                                JSONObject jsonObject;
                                Log.d("mTempDataSize", String.valueOf(mTempDataList.size()));
                                for (int i = 0; i < mTempDataList.size(); i++) {
                                    try {
                                        jsonObject = new JSONObject();
                                        jsonObject.put("Id", mTempDataList.get(i).getId());
                                        jsonObject.put("DestName", mTempDataList.get(i).getDestName());
                                        jsonObject.put("DestId", mTempDataList.get(i).getDestId());
                                        jsonObject.put("Lat", mTempDataList.get(i).getLat());
                                        jsonObject.put("Long", mTempDataList.get(i).getLong());
                                        jsonArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d("MainActivity: JSONDATA", jsonArray.toString());
                                String date = DateFormat.getDateTimeInstance().format(new Date());
                                if (newDataBase.SaveinMapTable(mMapName, jsonArray.toString(), date)) {
                                    newDataBase.DeleteTempMaps();
                                    Log.d("DATABSE", "DELETED DATA FROm TEMPDATA TABLE");
                                    Toast.makeText(getApplicationContext(), "Saved Map..", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                } else {
                                    Log.d("ERROR", "Error During Inserting in MyMap");
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Please Enter Valid Journey Name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mCancelMapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Some Destination Names", Toast.LENGTH_SHORT).show();
                }

            }
        });
      /*  FloatingActionButton fab;
        fab = (FloatingActionButton) findViewById(R.id.fab);
*/
        try {
            if (mMap == null) {
                mMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(21.0000, 78.0000));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(7);
            mMap.animateCamera(zoom);
            mMap.moveCamera(center);


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                View view = null;

                @Override
                public View getInfoWindow(Marker mark) {
                    if (view == null) {

                        view = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                        view.setLayoutParams(new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        View phtotosView = view.findViewById(R.id.item_title);
                        phtotosView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                            }
                        });


                        //    view.setLayoutParams(new ViewGroup.LayoutParams(200,200));
                        TextView title = (TextView) view.findViewById(R.id.textView3);
                        title.setText(mark.getTitle());
                    }
                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }

            });


            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                public void onInfoWindowClick(Marker mark) {


                    if (mark.getTitle().equals("Pune")) {
                        CustDialog("Pune");
                        Toast.makeText(MainActivity.this, mark.getTitle(), Toast.LENGTH_SHORT).show();// display toast

                    }
                    if (mark.getTitle().equals("Chennai")) {
                        CustDialog("Chennai");
                        Toast.makeText(MainActivity.this, mark.getTitle(), Toast.LENGTH_SHORT).show();// display toast

                    }
                    if (mark.getTitle().equals("Banglore")) {
                        CustDialog("Banglore");
                        Toast.makeText(MainActivity.this, mark.getTitle(), Toast.LENGTH_SHORT).show();// display toast

                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void CustDialog(String title) {
        // Create custom dialog object
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.cust_dialog);
        // Set dialog title
        dialog.setTitle(title);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.item1);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentphoto = new Intent(getApplicationContext(), PhotosFromOthers.class);
                startActivity(intentphoto);
                Toast.makeText(getApplicationContext(), "View Photos...", Toast.LENGTH_SHORT).show();
            }
        });
        RelativeLayout relativeLayout1 = (RelativeLayout) dialog.findViewById(R.id.relativeLayout);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentphoto = new Intent(getApplicationContext(), QuestionsFromOthers.class);
                startActivity(intentphoto);
                Toast.makeText(getApplicationContext(), "View Messages...", Toast.LENGTH_SHORT).show();
            }
        });
        Button sendphoto_button = (Button) dialog.findViewById(R.id.button2);
        Button sendmsg_button = (Button) dialog.findViewById(R.id.button);
        sendmsg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(getApplicationContext(), QuestionSlidingView.class);
                startActivity(theIntent);
                Toast.makeText(getApplicationContext(), "View Messages...", Toast.LENGTH_SHORT).show();
            }
        });
        sendphoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GridViewPhotos.class);
                startActivity(intent);


            }
        });


    }

    private Uri getOutputMedia(int mediaImage) {
        return Uri.fromFile(getOutputFile(mediaImage));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewImage();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Image Canceled By User.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Image Failed to Capture.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void previewImage() {
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        BitmapFactory.Options options = new BitmapFactory.Options();

        // downsizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(imageURI.getPath(),
                options);
        dialog.setContentView(R.layout.preview_image);
        // Set dialog title
        dialog.setTitle("Preview Image");
        Window window = dialog.getWindow();
        window.setLayout(600, 600);
        ImageView preview = (ImageView) dialog.findViewById(R.id.preview_image_dialog);
        preview.setImageBitmap(bitmap);
        dialog.show();
        Button sendphoto_button = (Button) dialog.findViewById(R.id.button3);
        // Button sendmsg_button=(Button) dialog.findViewById(R.id.button);
        sendphoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private File getOutputFile(int mediaImage) {
        File fileDIr;
        fileDIr = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!fileDIr.exists()) {
            if (!fileDIr.mkdir()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = null;
        if (mediaImage == MEDIA_IMAGE)
            mediaFile = new File(fileDIr.getAbsolutePath(), "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //       mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
            //          .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //setUpMap();
            }
        }
    }

    /* private void setUpMap() {
         mMap.addCircle(new CircleOptions()
                 .center(new LatLng(21.0000, 78.0000))
                 .radius(100)
                 .strokeColor(Color.RED)
                 .fillColor(Color.BLUE));
         mMap.addMarker(new MarkerOptions().position(new LatLng(18.5203, 73.8567)).title("Pune"));
         mMap.addMarker(new MarkerOptions().position(new LatLng(12.9667, 77.5667)).title("Banglore"));


 //     mMap.getMyLocation();

     }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            mMap.clear();
            newDataBase.DeleteTempMaps();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent intent2 = new Intent(getApplicationContext(), ShowRouteList.class);
            startActivity(intent2);


        } else if (id == R.id.nav_gallery) {
            Intent intent2 = new Intent(getApplicationContext(), ShowMyPhotos.class);
            startActivity(intent2);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {

        newDataBase = new NewDataBase(getApplicationContext());
        newDataBase.DeleteTempMaps();
        super.onDestroy();
    }
}
