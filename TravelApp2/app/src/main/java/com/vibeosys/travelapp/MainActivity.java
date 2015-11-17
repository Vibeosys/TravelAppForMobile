package com.vibeosys.travelapp;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vibeosys.travelapp.activities.ShowDestinationDetailsMain;
import com.vibeosys.travelapp.activities.ViewProfileActivity;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.UserAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleMapOptions googleMapOptions;
    private Marker myMarker;
    private UUID uuid;
    private AutoCompleteTextView text_dest;
    private List<Destination> mDestList;
    private HashMap<String, Integer> mDestinationNames = new HashMap<>();
    private List<GetTemp> mTempDataList;
    private List<MyDestination> mDestinationList;
    private LayoutInflater layoutInflater;

    //Facebook User Profile Image
    //private ImageView userProfileImage;


    @Override
    protected void onResume() {
        super.onResume();
        List<GetTemp> mList = null;
        mList = mNewDataBase.GetFromTemp();
        if (!mList.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(mList.get(i).getLat(), mList.get(i).getLong())).title(mList.get(i).getDestName()));
                /*if (i < mList.size() - 1) {
                    mMap.addPolyline(new PolylineOptions().geodesic(true)
                            .add(new LatLng(mList.get(i).getLat(), mList.get(i).getLong()))
                            .add(new LatLng(mList.get(i + 1).getLat(), mList.get(i + 1).getLong())).width(5).color(Color.BLACK));
                }*/
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mSessionManager = SessionManager.getInstance(getBaseContext());
        setTitle(null);

        //  mNewDataBase.insertComment(commentList);

        //Sample of USAGE of session manager

        if (NetworkUtils.isActiveNetworkAvailable(this)) {
            ContextWrapper ctw = new ContextWrapper(getApplicationContext());
            File directory = ctw.getDir(mSessionManager.getDatabaseDirPath(), Context.MODE_PRIVATE);
            File internalfile = new File(directory, mSessionManager.getDatabaseFileName());
            if (!internalfile.exists()) {
                downloadDatabase(internalfile);
            } else if (internalfile.exists() && (mSessionManager.getUserId() == null || mSessionManager.getUserId() == "")) {
                downloadDatabase(internalfile);
            }
            //String UserId = mSessionManager.getUserId();
            //Log.d("UserId", UserId);
            //super.fetchData(UserId, true);//id 1=>download 2=>upload
            //mServerSyncManager.downloadDataFromServer(true);
            //mNewDataBase.updateUserInfo(String.valueOf(UserId));
        } else {

            layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.cust_toast, null);
            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setView(view);//setting the view of custom toast layout
            toast.show();
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text_dest = (AutoCompleteTextView) findViewById(R.id.dest_text);
        mDestList = new ArrayList<>();
        //UserDetails userDetails = new UserDetails();

        // mNewDataBase.AddUser(UserId,UserName);
//        mNewDataBase.GetUser();

        //       mNewDataBase.addDestinations(mDestList);
        mDestinationNames = mNewDataBase.getDestNames();
        mDestinationList = new ArrayList<>();
        mDestinationList = mNewDataBase.GetFromTempLatLong();
      /* boolean temp=true;
        if(temp) mNewDataBase.DeleteTempMaps();
        else temp=false;*/
//       Log.d("MainActivity",String.valueOf(mDestinationList.size()));
        //List<String> mDestNames = new ArrayList<>();

        ArrayAdapter<String> arrayAdapter = null;
        try {
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,
                    Collections.list(Collections.enumeration(mDestinationNames.keySet())));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        text_dest.setAdapter(arrayAdapter);
        text_dest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //int id1=(Integer) parent.getSelectedItem();
                Log.d("Clicked", "Text Button");
                text_dest.setText("");
                text_dest.clearListSelection();
                text_dest.clearFocus();
                //DestinationTempData destinationTempData = null;
                String mDestName = null;
                List<TempData> mCurrentDestinationData = new ArrayList<>();
                mDestName = (String) parent.getItemAtPosition(position);
                int mDestId = mDestinationNames.get(mDestName);//Get DestId of Selected Location
                Log.d("MainActivity", String.valueOf(mDestId));
                mCurrentDestinationData = mNewDataBase.GetLatLong(mDestId);//Get Lat Long of DestName
                Log.d("MainActivitymTempData ", mCurrentDestinationData.toString());
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(mCurrentDestinationData.get(0).getmLat(),
                                mCurrentDestinationData.get(0).getmLong())).title(mDestName));
                Log.d("MainActivity", String.valueOf(mCurrentDestinationData.get(0).getmLat()));


                mNewDataBase.SaveMapInTemp(mCurrentDestinationData, mDestName);

                //destinationTempData = mNewDataBase.mGetLatLongFromTemp(
                //        mCurrentDestinationData.get(0).getmDestId());//Get Last Known Lat Long from Temp
                final CameraUpdate center = CameraUpdateFactory.newLatLng(
                        new LatLng(mCurrentDestinationData.get(0).getmLat(),
                                mCurrentDestinationData.get(0).getmLong()));
                mMap.moveCamera(center);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(7);
                mMap.animateCamera(zoom);
                //Log.d("TempData",String.valueOf(mTempData.size()));
                //if (destinationTempData != null) {

                //  mMap.addPolyline(new PolylineOptions().geodesic(true)
                //                  .add(new LatLng(mCurrentDestinationData.get(0).getmLat(),
                //                          mCurrentDestinationData.get(0).getmLong()))
                //                  .add(new LatLng(destinationTempData.getmLat(),
                //                          destinationTempData.getmLong())).width(5).color(Color.BLACK)
                //  );
                //}

            }
        });

        Button button = (Button) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNewDataBase.CheckTempData()) {
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
                                mTempDataList = mNewDataBase.GetFromTemp();
                                JSONArray jsonArray = new JSONArray();
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
                                if (mNewDataBase.SaveinMapTable(mMapName, jsonArray.toString(), date)) {
                                    mNewDataBase.DeleteTempMaps();
                                    Log.d("DATABSE", "DELETED DATA FROm TEMPDATA TABLE");
                                    Toast.makeText(getApplicationContext(), "Saved Map..", Toast.LENGTH_SHORT).show();
                                    mMap.clear();
                                    dialog.dismiss();
                                } else {
                                    Log.d("ERROR", "TravelAppError During Inserting in MyMap");
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.0000, 78.0000), 5));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);

                    setProfileInfoInNavigationBar(drawerView);
                    //getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

            };
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(21.0000, 78.0000));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(7);
            mMap.animateCamera(zoom);
            mMap.moveCamera(center);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(final Marker marker) {
                    final Dialog dlg = new Dialog(MainActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    View view = getLayoutInflater().inflate(R.layout.activity_location_details, null);
                    dlg.setContentView(view);
                    dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    //view = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                    //view.setLayoutParams(new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    //View phtotosView = view.findViewById(R.id.item_title);
                    final int mDestId = mDestinationNames.get(marker.getTitle());
                    final String destName = marker.getTitle();
                    TextView photoLabel = (TextView) view.findViewById(R.id.photo_label);
                    TextView detailsLable = (TextView) view.findViewById(R.id.details_label);
                    detailsLable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIntendedActivity(mDestId, destName, -1, ShowDestinationDetailsMain.class);
                        }
                    });

                    LinearLayout commentsrowLayout = (LinearLayout) view.findViewById(R.id.comments_row);
                    LinearLayout sendPhotos = (LinearLayout) view.findViewById(R.id.photos_row);
                    LinearLayout sendReviews = (LinearLayout) view.findViewById(R.id.rating);
                    sendPhotos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIntendedActivity(mDestId, destName, 0, ShowDestinationDetailsMain.class);
                        }
                    });
                    ImageView sendMessages = (ImageView) view.findViewById(R.id.sendDestinatiomMessages);
                    sendMessages.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIntendedActivity(mDestId, destName, -1, QuestionSlidingView.class);
                        }
                    });
                    sendReviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIntendedActivity(mDestId, destName, 3, ShowDestinationDetailsMain.class);
                        }
                    });

                    commentsrowLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIntendedActivity(mDestId, destName, 1, ShowDestinationDetailsMain.class);
                        }
                    });


                    TextView commentsLabel = (TextView) view.findViewById(R.id.comments_label);
                    TextView rattingsLabel = (TextView) view.findViewById(R.id.ratings_label);
                    long imagesCount = mNewDataBase.getImageCount(mDestId, false);
                    long reviewCount = mNewDataBase.getReviewCount(String.valueOf(mDestId));
                    //int msgCount = 0;
                    //int destCommentcount = 0;
                    long commentCount = mNewDataBase.getCommentCount(mDestId);
                    //if (destinationComment != null) destCommentcount = destinationComment.size();
                    //reviewCount  = listofQuestion.size();

                    photoLabel.setText(imagesCount + " Photos uploaded ...");
                    commentsLabel.setText(commentCount + " People have commented ...");
                    rattingsLabel.setText(reviewCount + " Reviews ..");

                    view.findViewById(R.id.overlay).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View aView) {
                                    dlg.dismiss();
                                }
                            });

                    view.findViewById(R.id.send_photos).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent theIntent = new Intent(MainActivity.this, GridViewPhotos.class);
                                    theIntent.putExtra("DestId", mDestId);
                                    startActivity(theIntent);
                                }
                            }
                    );

                    TextView title = (TextView) view.findViewById(R.id.dlg_title);
                    title.setText(marker.getTitle());
                    dlg.show();
                    return false;
                }

            });

        } catch (Exception e) {
            Log.e("GeneralException", e.toString());
        }
    }

    private void startIntendedActivity(int destId, String destination, int id, Class<?> cls) {
        Intent activityIntent = new Intent(getApplicationContext(), cls);
        activityIntent.putExtra("DestId", destId);
        activityIntent.putExtra("DestName", destination);
        activityIntent.putExtra("Id", id);
        startActivity(activityIntent);
    }

    private void downloadDatabase(File internalfile) {
        NetworkUtils n = new NetworkUtils();
        java.net.URL url = null;
        HttpURLConnection urlConnection = null;
        OutputStream myOutput = null;
        byte[] buffer = null;
        InputStream inputStream = null;

        if (!n.isActiveNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Could not connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        uuid = UUID.randomUUID();
        mSessionManager.setUserId(uuid.toString());
        String downloadDBURL = mSessionManager.getDownloadDbUrl(mSessionManager.getUserId());

        try {
            url = new URL(downloadDBURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("STATUS", "Request Sended...");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();

               /* OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(data);
                wr.flush();
               */
            int Http_Result = urlConnection.getResponseCode();
            String res = urlConnection.getResponseMessage().toString();
            Log.d("ResponseMessage", res);
            Log.e("RESPONSE CODE", String.valueOf(Http_Result));
            if (Http_Result == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                buffer = new byte[1024];
                myOutput = new FileOutputStream(internalfile);
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                inputStream.close();
            }

        } catch (ConnectException cEx) {
            Log.e("DbDownloadException", "TravelAppError while downloading database" + cEx.toString());
        } catch (MalformedURLException eMf) {
            Log.e("DbDownloadException", "TravelAppError while downloading database" + eMf.toString());
        } catch (FileNotFoundException eFn) {
            Log.e("DbDownloadException", "TravelAppError while downloading database" + eFn.toString());
        } catch (IOException eIo) {
            Log.e("DbDownloadException", "TravelAppError while downloading database" + eIo.toString());
        } catch (Exception ex) {
            Log.e("DbDownloadException", "TravelAppError while downloading database" + ex.toString());
        }

        boolean userCreated = mNewDataBase.createUserId(mSessionManager.getUserId());
        if (!userCreated)
            Log.e("UserCreation", "New user could not be created in DB");

         /*catch (JSONException e) {
            e.printStackTrace();
     /*   }*//* catch (JSONException e) {
                e.printStackTrace();
            }
*/
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
            mNewDataBase.DeleteTempMaps();
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
            Intent routeListIntent = new Intent(getApplicationContext(), ShowRouteList.class);
            startActivity(routeListIntent);


        } else if (id == R.id.nav_gallery) {
            Intent showPhotosIntent = new Intent(getApplicationContext(), ShowMyPhotos.class);
            startActivity(showPhotosIntent);

        } else if (id == R.id.userprofile) {
            Intent userProfileIntent;
            if (UserAuth.isUserLoggedIn()) {
                userProfileIntent = new Intent(getApplicationContext(), ViewProfileActivity.class);
            } else {
                userProfileIntent = new Intent(getApplicationContext(), LoginActivity.class);
            }
            startActivity(userProfileIntent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
