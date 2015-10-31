package com.vibeosys.travelapp;

import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vibeosys.travelapp.activities.ShowDestinationDetailsMain;
import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.tasks.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/12/2015.
 */
public class ShowRoutesOnMap extends BaseActivity implements OnMapReadyCallback {
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

        String mRouteName = mIntent.getStringExtra("theRouteName");
        setTitle(mRouteName);
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
                    //int id = jsonObject.getInt("Id");
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
                            .add(new LatLng(theLatLongList.get(i + 1).getmLat(),
                                    theLatLongList.get(i + 1).getmLong())).width(5).color(Color.BLACK));


        }

      googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                final Dialog dlg = new Dialog(ShowRoutesOnMap.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                View view = getLayoutInflater().inflate(R.layout.activity_location_details, null);
                dlg.setContentView(view);
                dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                //view = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                //view.setLayoutParams(new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT));
                //View phtotosView = view.findViewById(R.id.item_title);
                final int mDestId = mDestinationNames.get(marker.getTitle());
                TextView photoLabel = (TextView) view.findViewById(R.id.photo_label);
                TextView detailsLable = (TextView) view.findViewById(R.id.details_label);
                detailsLable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startMoreDetailsActivitty = new Intent(getApplicationContext(), ShowDestinationDetailsMain.class);
                        startMoreDetailsActivitty.putExtra("DestId", mDestId);
                        startMoreDetailsActivitty.putExtra("DestName", marker.getTitle());
                        startActivity(startMoreDetailsActivitty);
                    }
                });

                LinearLayout commentsrowLayout = (LinearLayout) view.findViewById(R.id.comments_row);
                LinearLayout sendPhotos = (LinearLayout) view.findViewById(R.id.photos_row);
                LinearLayout sendReviews = (LinearLayout) view.findViewById(R.id.rating);
                sendPhotos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startPhotosActivitty = new Intent(getApplicationContext(), ShowDestinationDetailsMain.class);
                        startPhotosActivitty.putExtra("DestId", mDestId);
                        startPhotosActivitty.putExtra("DestName", marker.getTitle());
                        startPhotosActivitty.putExtra("Id", 0);
                        startActivity(startPhotosActivitty);
                    }
                });
                ImageView sendMessages = (ImageView) view.findViewById(R.id.sendDestinatiomMessages);
                sendMessages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startSendMessagesActivitty = new Intent(getApplicationContext(), QuestionSlidingView.class);
                        startSendMessagesActivitty.putExtra("DestId", mDestId);
                        startSendMessagesActivitty.putExtra("DestName", marker.getTitle());
                        startActivity(startSendMessagesActivitty);

                    }
                });
                sendReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startReviewsActivitty = new Intent(getApplicationContext(), ShowDestinationDetailsMain.class);
                        startReviewsActivitty.putExtra("DestId", mDestId);
                        startReviewsActivitty.putExtra("DestName", marker.getTitle());
                        startReviewsActivitty.putExtra("Id", 3);
                        startActivity(startReviewsActivitty);
                    }
                });

                commentsrowLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startCommentActivitty = new Intent(getApplicationContext(), ShowDestinationDetailsMain.class);
                        startCommentActivitty.putExtra("DestId", mDestId);
                        startCommentActivitty.putExtra("DestName", marker.getTitle());
                        startCommentActivitty.putExtra("Id", 1);
                        startActivity(startCommentActivitty);
                    }
                });


                TextView commentsLabel = (TextView) view.findViewById(R.id.comments_label);
                TextView rattingsLabel = (TextView) view.findViewById(R.id.ratings_label);
                int imagesCount = newDataBase.Images(mDestId, false).size();
                List<SendQuestionAnswers> listofQuestion = newDataBase.mListQuestions(String.valueOf(mDestId));
                int msgCount = 0;
                int destCommentcount = 0;
                List<CommentsAndLikes> destinationComment = newDataBase.DestinationComments(mDestId);
                if (destinationComment != null) destCommentcount = destinationComment.size();
                if (listofQuestion != null) msgCount = listofQuestion.size();

                photoLabel.setText(String.valueOf(imagesCount) + " Photos uploaded");
                commentsLabel.setText(destCommentcount + " People have commented about the place.");
                rattingsLabel.setText(String.valueOf(msgCount) + " Reviews for this place");

                view.findViewById(R.id.overlay).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View aView) {
                        dlg.dismiss();
                    }
                });

                view.findViewById(R.id.send_photos).setOnClickListener(new View.OnClickListener() {

                                                                           @Override
                                                                           public void onClick(View v) {
                                                                               Intent theIntent = new Intent(ShowRoutesOnMap.this, GridViewPhotos.class);
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
