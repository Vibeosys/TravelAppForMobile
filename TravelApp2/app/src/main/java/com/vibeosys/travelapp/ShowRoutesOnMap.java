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

/**
 * Created by mahesh on 10/12/2015.
 */
public class ShowRoutesOnMap extends FragmentActivity implements OnMapReadyCallback {
    NewDataBase newDataBase;
    List<Routes> mRouteList;
    //HashMap<Integer, DestLatLong> mHashMapRoutes;
    ArrayList<DestLatLong> mDestinationLatLongs;
    HashMap<String, Integer> mDestinationNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showroutes);
        newDataBase = new NewDataBase(ShowRoutesOnMap.this);
        Intent mIntent = getIntent();
        mDestinationNames = newDataBase.getDestNames();
        setTitle("My Routes");
        String mRouteName = mIntent.getStringExtra("theRouteName");
        Routes mRoute = newDataBase.getRoute(mRouteName);

        //mHashMapRoutes = new HashMap<>();
        mDestinationLatLongs = new ArrayList<>();
        //for (int i = 0; i < mRouteList.size(); i++) {
        //if (mRouteName.equals(mRouteList.get(i).getmRouteName())) {
            JSONArray theJsonArray;
            JSONObject jsonObject;
            DestLatLong mDestLatLong;
            try {
                theJsonArray = new JSONArray(mRoute.getmRoutetripsNames());
                Log.d("SHOWROUTELIST :- JSON", theJsonArray.toString());
                for (int j = 0; j < theJsonArray.length(); j++) {
                    jsonObject = theJsonArray.getJSONObject(j);
                    mDestLatLong = new DestLatLong();
                    mDestLatLong.setLatitude(jsonObject.getDouble("Lat"));
                    mDestLatLong.setLongitude(jsonObject.getDouble("Long"));
                    mDestLatLong.setmDestName(jsonObject.getString("DestName"));
                    int id = jsonObject.getInt("Id");
                    //mHashMapRoutes.put(id, mDestLatLong);
                    mDestinationLatLongs.add(mDestLatLong);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException er) {
                er.printStackTrace();
            }
            //}
        //}

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapNameText);
        mapFragment.getMapAsync(this);

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
        Log.d("INDialog", "" + cDestId);
        TextView mCountPhotos = (TextView) dialog.findViewById(R.id.photocounttext);
        mCountPhotos.setText(String.valueOf(count));
        TextView mCountMsgs = (TextView) dialog.findViewById(R.id.item_counter);
        mCountMsgs.setText(String.valueOf(count1));
        RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.userphoto);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowRoutesOnMap.this, DestinationUsersImages.class);
                intent.putExtra("DestId", cDestId);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "View Photos...", Toast.LENGTH_SHORT).show();
            }
        });
        RelativeLayout relativeLayout1 = (RelativeLayout) dialog.findViewById(R.id.mymessages);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentphoto = new Intent(ShowRoutesOnMap.this, QuestionsFromOthers.class);
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
                Intent theIntent = new Intent(ShowRoutesOnMap.this, DestinationComments.class);
                theIntent.putExtra("DestId", cDestId);
                startActivity(theIntent);
            }
        });
        sendmsg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(ShowRoutesOnMap.this, QuestionSlidingView.class);
                theIntent.putExtra("DestId", cDestId);
                startActivity(theIntent);
                Toast.makeText(getApplicationContext(), "View Messages...", Toast.LENGTH_SHORT).show();
            }
        });
        sendphoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowRoutesOnMap.this, GridViewPhotos.class);
                intent.putExtra("DestId", cDestId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        List<LatLong> theLatLongList = new ArrayList<>();
        LatLong theLatLong;
        LatLng theCurrentLatLong = null;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.0000, 78.0000), 5));

        for(DestLatLong theDestLatLong : mDestinationLatLongs)
        {
            theLatLong = new LatLong();
            //DestLatLong theDestLatLong = entry.getValue();
            theLatLong.setmLat(theDestLatLong.getLatitude());
            theLatLong.setmLong(theDestLatLong.getLongitude());
            theCurrentLatLong = new LatLng(theDestLatLong.getLatitude(), theDestLatLong.getLongitude());
            theLatLongList.add(theLatLong);
            googleMap.addMarker(new MarkerOptions().position(theCurrentLatLong).title(theDestLatLong.getmDestName()));
        }
/*
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
*/
        for (int i = 0; i < theLatLongList.size() - 1; i++) {
            googleMap.addPolyline(new PolylineOptions().geodesic(true)
                            .add(new LatLng(theLatLongList.get(i).getmLat(), theLatLongList.get(i).getmLong()))
                            .add(new LatLng(theLatLongList.get(i + 1).getmLat(), theLatLongList.get(i + 1).getmLong())).width(5).color(Color.BLACK)
            );


        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int mDestId = mDestinationNames.get(marker.getTitle());
                Log.d("MainActivityMarker", "" + mDestId);
                mDestId = mDestinationNames.get(marker.getTitle());
                CustDialog(marker.getTitle(), mDestId);
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
