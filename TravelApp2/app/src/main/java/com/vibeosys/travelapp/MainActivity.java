package com.vibeosys.travelapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    HashMap<String, Integer> mDestinationNames = new HashMap<>();
    List<GetTemp> mTempDataList;
    NewDataBase newDataBase;
    List<GetTemp> mTempDataShowList;
    List<MyDestination> mDestinationList;
    SharedPreferences sharedPref;
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onResume() {
        super.onResume();
        List<GetTemp> mList=new ArrayList<>();
        mList=newDataBase.GetFromTemp();
        if(!mList.isEmpty()){
            for(int i=0;i<mList.size();i++){
                mMap.addMarker(new MarkerOptions().position(new LatLng(mList.get(i).getLat(),mList.get(i).getLong())).title(mList.get(i).getDestName()));
                if(i>mList.size()-1){
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text_dest = (AutoCompleteTextView) findViewById(R.id.dest_text);

        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mDestList = new ArrayList<Destination>();


        UserDetails userDetails = new UserDetails();
        newDataBase = new NewDataBase(getApplicationContext());
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
        List<String> mDestNames=new ArrayList<>();

        ArrayAdapter<String> arrayAdapter=null;
        try {
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Collections.list(Collections.enumeration(mDestinationNames.keySet())));
}catch (NullPointerException e){
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
                Log.d("MainActivity",String.valueOf(mDestId));
                mCurrentDestinationData = newDataBase.GetLatLong(mDestId);//Get Lat Long of DestName
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
                    int mDestId = mDestinationNames.get(mark.getTitle());
                    Log.d("MainActivityMarker",""+mDestId);
                    CustDialog(mark.getTitle(), mDestId);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void CustDialog(String title, final int cDestId) {
        // Create custom dialog object
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.cust_dialog);
        // Set dialog title
        dialog.setTitle(title);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int count = newDataBase.ImageCount(cDestId);
        int count1 = newDataBase.MsgCount(cDestId);
        dialog.show();
        Log.d("INDialog",""+cDestId);
        TextView mCountPhotos = (TextView) dialog.findViewById(R.id.photocounttext);
        mCountPhotos.setText(String.valueOf(count));
        TextView mCountMsgs = (TextView) dialog.findViewById(R.id.item_counter);
        mCountMsgs.setText(String.valueOf(count1));
        RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.userphoto);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DestinationUsersImages.class);
                intent.putExtra("DestId",cDestId);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "View Photos...", Toast.LENGTH_SHORT).show();
            }
        });
        RelativeLayout relativeLayout1 = (RelativeLayout) dialog.findViewById(R.id.mymessages);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentphoto = new Intent(getApplicationContext(), QuestionsFromOthers.class);
                intentphoto.putExtra("DestId", cDestId);
                startActivity(intentphoto);
                Toast.makeText(getApplicationContext(), "View Messages...", Toast.LENGTH_SHORT).show();
            }
        });
        Button sendphoto_button = (Button) dialog.findViewById(R.id.senduserButton);
        Button sendmsg_button = (Button) dialog.findViewById(R.id.sendbutton);
        Button usercomments = (Button) dialog.findViewById(R.id.usercommentsButton);
        usercomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(getApplicationContext(), DestinationComments.class);
                theIntent.putExtra("DestId", cDestId);
                startActivity(theIntent);
            }
        });
        sendmsg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(getApplicationContext(), QuestionSlidingView.class);
                theIntent.putExtra("DestId", cDestId);
                startActivity(theIntent);
                Toast.makeText(getApplicationContext(), "View Messages...", Toast.LENGTH_SHORT).show();
            }
        });
        sendphoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GridViewPhotos.class);
                intent.putExtra("DestId", cDestId);
                startActivity(intent);
            }
        });
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
