package com.vibeosys.travelapp;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.google.gson.Gson;
import com.vibeosys.travelapp.activities.ShowDestinationDetailsMain;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onFailure(String aData, int id) {
        super.onFailure(aData, id);
        try {
         //   Log.d("Failed to Load", "Data" + aData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String aData, int id) {
        super.onSuccess(aData, id);

    }

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleMapOptions googleMapOptions;
    private Marker myMarker;
    UUID uuid;
    AutoCompleteTextView text_dest;
    Uri imageURI;
    private static final int IMAGE_CAPTURE_CODE = 100;
    private static final int CAMERA_CAPTURE_REQUEST_CODE = 100;
    private static final int MEDIA_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "TravelPhotos";
    List<Destination> mDestList;
    protected static String DB_NAME = "TravelApp";
    static final String DB_PATH = "databases";
    HashMap<String, Integer> mDestinationNames = new HashMap<>();
    List<GetTemp> mTempDataList;
    NewDataBase newDataBase;
    List<GetTemp> mTempDataShowList;
    List<MyDestination> mDestinationList;
    SharedPreferences sharedPref;
    public static final String MyPREFERENCES = "MyPrefs";
    LayoutInflater layoutInflater;
    SharedPreferences.Editor editor;

    @Override
    protected void onResume() {
        super.onResume();
        List<GetTemp> mList = new ArrayList<>();
        mList = newDataBase.GetFromTemp();
        if (!mList.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(mList.get(i).getLat(), mList.get(i).getLong())).title(mList.get(i).getDestName()));
                if (i > mList.size() - 1) {
                    mMap.addPolyline(new PolylineOptions().geodesic(true)
                            .add(new LatLng(mList.get(i).getLat(), mList.get(i).getLong()))
                            .add(new LatLng(mList.get(i + 1).getLat(), mList.get(i + 1).getLong())).width(5).color(Color.BLACK));
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Gson gson = new Gson();
        newDataBase = new NewDataBase(getApplicationContext());
       //  newDataBase.insertComment(commentList);

        if (NetworkUtils.isActiveNetworkAvailable(this)) {
            ContextWrapper ctw = new ContextWrapper(getApplicationContext());
            File directory = ctw.getDir(DB_PATH, Context.MODE_PRIVATE);
            File internalfile = new File(directory, DB_NAME);
            if (!internalfile.exists()) {
                copyDatabase(internalfile);
            }

            String url = getResources().getString(R.string.URL);
            String UserId = sharedPref.getString("UserId", null);
            Log.d("UserId", UserId);
            super.fetchData(url + "download?userid=" + UserId, true, 1);//id 1=>download 2=>upload
            Log.d("Download Calling..", "DownloadUrl:-" + url + "download");
            newDataBase.updateUserInfo(String.valueOf(UserId));
            newDataBase.updateUser("abc", "abc@abc.com", String.valueOf(UserId));
        } else {

            layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.cust_toast, null);
            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setView(view);//setting the view of custom toast layout
            toast.show();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text_dest = (AutoCompleteTextView) findViewById(R.id.dest_text);
        mDestList = new ArrayList<>();
        UserDetails userDetails = new UserDetails();

        // newDataBase.AddUser(UserId,UserName);
//        newDataBase.GetUser();

        //       newDataBase.addDestinations(mDestList);
        mDestinationNames = newDataBase.getDestNames();
        mDestinationList = new ArrayList<>();
        mDestinationList = newDataBase.GetFromTempLatLong();
      /* boolean temp=true;
        if(temp) newDataBase.DeleteTempMaps();
        else temp=false;*/
//       Log.d("MainActivity",String.valueOf(mDestinationList.size()));
        List<String> mDestNames = new ArrayList<>();

        ArrayAdapter<String> arrayAdapter = null;
        try {
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Collections.list(Collections.enumeration(mDestinationNames.keySet())));
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
                DestinationTempData destinationTempData = null;
                String mDestName = null;
                List<TempData> mCurrentDestinationData = new ArrayList<>();
                mDestName = (String) parent.getItemAtPosition(position);
                int mDestId = mDestinationNames.get(mDestName);//Get DestId of Selected Location
                Log.d("MainActivity", String.valueOf(mDestId));
                mCurrentDestinationData = newDataBase.GetLatLong(mDestId);//Get Lat Long of DestName
                Log.d("MainActivitymTempData ", mCurrentDestinationData.toString());
                mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentDestinationData.get(0).getmLat(), mCurrentDestinationData.get(0).getmLong())).title(mDestName));
                Log.d("MainActivity", String.valueOf(mCurrentDestinationData.get(0).getmLat()));


                newDataBase.SaveMapInTemp(mCurrentDestinationData, mDestName);

                destinationTempData = newDataBase.mGetLatLongFromTemp(mCurrentDestinationData.get(0).getmDestId());//Get Last Known Lat Long from Temp
                final CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(mCurrentDestinationData.get(0).getmLat(), mCurrentDestinationData.get(0).getmLong()));
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
                                    mMap.clear();
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.0000, 78.0000), 5));
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
                    TextView photoLabel = (TextView) view.findViewById(R.id.photo_label);
                    TextView detailsLable=(TextView) view.findViewById(R.id.details_label);
                    detailsLable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent startMoreDetailsActivitty = new Intent(getApplicationContext(), ShowDestinationDetailsMain.class);
                            startMoreDetailsActivitty.putExtra("DestId", mDestId);
                            startMoreDetailsActivitty.putExtra("DestName", marker.getTitle());
                            startActivity(startMoreDetailsActivitty);
                        }
                    });

                    LinearLayout commentsrowLayout=(LinearLayout)view.findViewById(R.id.comments_row);
                    LinearLayout sendPhotos=(LinearLayout)view.findViewById(R.id.photos_row);
                    LinearLayout sendReviews=(LinearLayout)view.findViewById(R.id.rating);
                    sendPhotos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent startPhotosActivitty = new Intent(getApplicationContext(), ShowDestinationDetailsMain.class);
                            startPhotosActivitty.putExtra("DestId", mDestId);
                            startPhotosActivitty.putExtra("DestName", marker.getTitle());
                            startActivity(startPhotosActivitty);
                        }
                    });
                    ImageView sendMessages=(ImageView)view.findViewById(R.id.sendDestinatiomMessages);
                    sendMessages.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent startSendMessagesActivitty = new Intent(getApplicationContext(), QuestionSlidingView.class);
                            startSendMessagesActivitty.putExtra("DestId", mDestId);
                            startActivity(startSendMessagesActivitty);

                        }
                    });
                    sendReviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           Intent startReviewsActivitty = new Intent(getApplicationContext(), ShowDestinationDetailsMain.class);
                            startReviewsActivitty.putExtra("DestId", mDestId);
                            startReviewsActivitty.putExtra("DestName", marker.getTitle());
                            startActivity(startReviewsActivitty);
                       }
                    });

                    commentsrowLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent startCommentActivitty=new Intent(getApplicationContext() ,ShowDestinationDetailsMain.class);
                            startCommentActivitty.putExtra("DestId",mDestId);
                            startCommentActivitty.putExtra("DestName", marker.getTitle());
                            startActivity(startCommentActivitty);
                        }
                    });


                    TextView commentsLabel = (TextView) view.findViewById(R.id.comments_label);
                    TextView rattingsLabel = (TextView) view.findViewById(R.id.ratings_label);
                    int count = newDataBase.Images(mDestId,false).size();
                    int count1 = newDataBase.MsgCount(mDestId);
                    photoLabel.setText(String.valueOf(count1)+" Photos uploaded");
                    commentsLabel.setText(String.valueOf(count)+" Peoples are talking about it.");
                    rattingsLabel.setText("Rated 2034 times");

                    view.findViewById(R.id.overlay).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View aView) {
                            dlg.dismiss();
                        }
                    });

                    view.findViewById(R.id.send_photos).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            Intent theIntent = new Intent(MainActivity.this, GridViewPhotos.class);
                            theIntent.putExtra("DestId", mDestId);
                            startActivity(theIntent);
                        }
                                                                           }
                    );

                    /*
                    phtotosView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                                }
                    });
                    view.setLayoutParams(new ViewGroup.LayoutParams(200, 200));*/
                    TextView title = (TextView) view.findViewById(R.id.dlg_title);
                    title.setText(marker.getTitle());
                    dlg.show();
                    return false;
                }

            });

            /*
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                View view = null;
                @Override
                public View getInfoWindow(Marker mark) {
                    //if (view == null) {
                        view = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                        view.setLayoutParams(new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        View phtotosView = view.findViewById(R.id.item_title);
                        int mDestId = mDestinationNames.get(mark.getTitle());
                        TextView countimages = (TextView) view.findViewById(R.id.item_counter);
                        TextView countmsgs = (TextView) view.findViewById(R.id.countmsgs);
                        int count = newDataBase.Images(mDestId,false).size();
                        int count1 = newDataBase.MsgCount(mDestId);
                        countimages.setText(String.valueOf(count1));
                        countmsgs.setText(String.valueOf(count));
                        phtotosView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                            }
                        });
                            view.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
                        TextView title = (TextView) view.findViewById(R.id.textView3);
                        title.setText(mark.getTitle());

                    //}
                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                public void onInfoWindowClick(Marker mark) {
                    int mDestId = mDestinationNames.get(mark.getTitle());
                    Log.d("MainActivityMarker", "" + mDestId);
                    showDestinationInfoDialog (mark.getTitle(), mDestId);
                }
            });

            */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyDatabase(File internalfile) {
        NetworkUtils n = new NetworkUtils();
        java.net.URL url = null;
        HttpURLConnection urlConnection = null;
        OutputStream myOutput = null;
        byte[] buffer = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader;
        try {
            if (n.isActiveNetworkAvailable(getApplicationContext())) {
                uuid = UUID.randomUUID();
                String data = "userid"
                        + "=" + String.valueOf(uuid);
                Log.d("UUId",data);
                String callurl = getResources().getString(R.string.URL);
                Log.d("called Url", callurl +"downloadDb"+ "?" + data);
                url = new URL(callurl +"downloadDb"+ "?" + data);
                editor.putString("UserId", String.valueOf(uuid));

                editor.putString("UserName", "abc");
                editor.putString("EmailId", "abc@abc.com");

                editor.commit();
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
                Log.d("RESPONSE CODE", String.valueOf(Http_Result));
                switch (Http_Result) {
                    case HttpURLConnection.HTTP_OK:
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
                        break;
                    case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                        Log.d("STATUS ", "Time Out Occours During Connecting to server..");
                        break;
                    case HttpURLConnection.HTTP_BAD_GATEWAY:
                        Log.d("STATUS ", "BAD GATEWAY REQUEST ...");
                        break;
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        Log.d("STATUS ", "HTTP INTERNAL ERROR");
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        Log.d("STATUS ", "HTTP UNAUTHORIZED.");
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        Log.d("STATUS", "HTTP NOT FOUND..");
                        break;
                    case HttpURLConnection.HTTP_BAD_METHOD:
                        Log.d("STATUS", "HTTP_BAD_METHOD");
                        break;

                }
                //  content=stringBuilder.toString();

            } else {
                Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


}
