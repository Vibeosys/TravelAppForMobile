package com.vibeosys.travelapp;

import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vibeosys.travelapp.activities.DestinationComments;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mahesh on 10/12/2015.
 */
public class ShowRoutesOnMap extends FragmentActivity implements OnMapReadyCallback {
    NewDataBase newDataBase;
    List<Routes> mRouteList;
    HashMap<Integer, DestLatLong> mHashMapRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showroutes);
        newDataBase = new NewDataBase(ShowRoutesOnMap.this);
        mRouteList = newDataBase.getRouteList();
        Intent mIntent = getIntent();
        String mRouteName = mIntent.getStringExtra("theRouteName");
        mHashMapRoutes = new HashMap<>();
        for (int i = 0; i < mRouteList.size(); i++) {
            if (mRouteName.equals(mRouteList.get(i).getmRouteName())) {
                JSONArray theJsonArray;
                JSONObject jsonObject;
                DestLatLong mDestLatLong;
                try {
                    theJsonArray = new JSONArray(mRouteList.get(i).getmRoutetripsNames());
                    Log.d("SHOWROUTELIST :- JSON", theJsonArray.toString());
                    for (int j = 0; j < theJsonArray.length(); j++) {
                        jsonObject = theJsonArray.getJSONObject(j);
                        mDestLatLong = new DestLatLong();
                        mDestLatLong.setLatitude(jsonObject.getDouble("Lat"));
                        mDestLatLong.setLongitude(jsonObject.getDouble("Long"));
                        mDestLatLong.setmDestName(jsonObject.getString("DestName"));
                        int id = jsonObject.getInt("Id");
                        mHashMapRoutes.put(id, mDestLatLong);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException er) {
                    er.printStackTrace();
                }
            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapNameText);
        mapFragment.getMapAsync(this);
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
        TextView mCountPhotos = (TextView) dialog.findViewById(R.id.photocounttext);

        TextView mCountMsgs = (TextView) dialog.findViewById(R.id.item_counter);

        dialog.show();
        RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.userphoto);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DestinationUsersImages.class);

                startActivity(intent);
                Toast.makeText(getApplicationContext(), "View Photos...", Toast.LENGTH_SHORT).show();
            }
        });
        RelativeLayout relativeLayout1 = (RelativeLayout) dialog.findViewById(R.id.mymessages);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentphoto = new Intent(getApplicationContext(), QuestionsFromOthers.class);

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

                startActivity(theIntent);
            }
        });
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
                Intent intent = new Intent(getApplicationContext(), GridViewPhotos.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        List<LatLong> theLatLongList = new ArrayList<>();
        LatLong theLatLong;
        LatLng theCurrentLatLong = null;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.0000, 78.0000),5));

        for (Map.Entry<Integer, DestLatLong> entry : mHashMapRoutes.entrySet()) {
            int theId = entry.getKey();
            theLatLong = new LatLong();
            DestLatLong theDestLatLong = entry.getValue();
            theLatLong.setmLat(theDestLatLong.getLatitude());
            theLatLong.setmLong(theDestLatLong.getLongitude());
            theCurrentLatLong = new LatLng(theDestLatLong.getLatitude(), theDestLatLong.getLongitude());
            theLatLongList.add(theLatLong);
            googleMap.addMarker(new MarkerOptions().position(theCurrentLatLong).title(theDestLatLong.getmDestName()));


        }
        for (int i = 0; i < theLatLongList.size() - 1; i++) {
            googleMap.addPolyline(new PolylineOptions().geodesic(true)
                            .add(new LatLng(theLatLongList.get(i).getmLat(), theLatLongList.get(i).getmLong()))
                            .add(new LatLng(theLatLongList.get(i + 1).getmLat(), theLatLongList.get(i + 1).getmLong())).width(5).color(Color.BLACK)
            );


        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                CustDialog(marker.getTitle().toString());
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                                // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
