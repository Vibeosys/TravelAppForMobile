package com.vibeosys.travelapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahesh.travelapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    String[] list_dest = {"Pune", "Hyderabad", "Mumbai", "Nashik"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text_dest = (AutoCompleteTextView) findViewById(R.id.dest_text);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_dest);
        text_dest.setAdapter(arrayAdapter);
        text_dest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //int id1=(Integer) parent.getSelectedItem();
                String selection = (String) parent.getItemAtPosition(position);
                int itemId = (int) parent.getId();
                Log.d("ItemId", String.valueOf(itemId));
                // Toast.makeText(getApplicationContext(), ,Toast.LENGTH_SHORT).show();
            }
        });

        Button button = (Button) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saved Map..", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
      /*  FloatingActionButton fab;
        fab = (FloatingActionButton) findViewById(R.id.fab);

*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try {
            if (mMap == null) {
                mMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(21.0000, 78.0000))
                    .radius(100)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
           /* CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(18.5203,
                            73.8567));
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(20);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);*/
            mMap.addMarker(new MarkerOptions().position(new LatLng(18.5203, 73.8567)).title("Pune"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(12.9667, 77.5667)).title("Banglore"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(13.0827, 80.2707)).title("Chennai"));
            mMap.addPolyline(new PolylineOptions().geodesic(true)
                    .add(new LatLng(18.5203, 73.8567))  // Pune
                    .add(new LatLng(12.9667, 77.5667))  // Banglore
                    .add(new LatLng(13.0827, 80.2707))  // Mumbai

            );
            //View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
            //   mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                View view = null;
                //  RelativeLayout relativeLayout;
                //View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                @Override
                public View getInfoWindow(Marker mark) {

                    //* View view = getInfoWindow(marker);
                    //view.setLayoutParams(new ViewGroup.LayoutParams().LayoutParams(200, 200));*//*
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


           /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    View theInfo = findViewById(R.id.info_window);
                    if (theInfo.getVisibility() == View.VISIBLE)
                        theInfo.setVisibility(View.GONE);
                    else
                        theInfo.setVisibility(View.VISIBLE);

                    return false;
                }
            });*/

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
   /* @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }*/

    void CustDialog(String title) {
        // Create custom dialog object
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.cust_dialog);
        // Set dialog title
        dialog.setTitle(title);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT );
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
Intent theIntent=new Intent(getApplicationContext(),AnswerQuestion.class);
 startActivity(theIntent);
                Toast.makeText(getApplicationContext(), "View Messages...", Toast.LENGTH_SHORT).show();
            }
        });
        sendphoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageURI = getOutputMedia(MEDIA_IMAGE);
                takephoto.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                startActivityForResult(takephoto, CAMERA_CAPTURE_REQUEST_CODE);
                Toast.makeText(getApplicationContext(), "Send Photo...", Toast.LENGTH_SHORT).show();


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
        window.setLayout(500, 500);
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
            Toast.makeText(getApplicationContext(), "Created Map", Toast.LENGTH_SHORT).show();
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

}
