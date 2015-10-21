package com.vibeosys.travelapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vibeosys.travelapp.databaseHelper.NewDataBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/3/2015.
 */
public class ShowRouteList extends AppCompatActivity {
    ListView listView;
    Context context;
    NewDataBase newDataBase;
    List<Routes> mRouteList;
    int REQUESTCODE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routelist_layout);
        getSupportActionBar().setTitle("My Routes");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView=(ListView)findViewById(R.id.routelistview);
        context=getApplicationContext();
        newDataBase=new NewDataBase(ShowRouteList.this);
        mRouteList=newDataBase.getRouteList();
        listView.setAdapter(new TravelCustomAdaptor(mRouteList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent theRouteNameIntent = new Intent(ShowRouteList.this, ShowRoutesOnMap.class);
                theRouteNameIntent.putExtra("theRouteName", mRouteList.get(position).getmRouteName());
                startActivity(theRouteNameIntent);
            }
        });
    }


    class TravelCustomAdaptor extends BaseAdapter {
        List<Routes> theRouteList;

        TravelCustomAdaptor(List<Routes> cListRoutes) {
            theRouteList = cListRoutes;
        }

        @Override
        public int getCount() {
            return theRouteList.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.cust_routes, parent, false);
            TextView title, detail, textdatetext;

            title = (TextView) row.findViewById(R.id.item_title);
            detail = (TextView) row.findViewById(R.id.to_destination);
            textdatetext = (TextView) row.findViewById(R.id.textdate);
            title.setText(theRouteList.get(position).getmRouteName());
            String date= theRouteList.get(position).getmRouteDate();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat();

            textdatetext.setText(date.substring(0,14));
            List<String> theRouteNames = new ArrayList<>();
            JSONArray theJsonArray;
            JSONObject jsonObject;
            try {
                theJsonArray = new JSONArray(theRouteList.get(position).getmRoutetripsNames());
                Log.d("SHOWROUTELIST :- JSON",theJsonArray.toString());
                for (int i = 0; i < theJsonArray.length(); i++) {
                    jsonObject = theJsonArray.getJSONObject(i);
                    theRouteNames.add(jsonObject.getString("DestName"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < theRouteNames.size(); i++) {
                detail.append(theRouteNames.get(i) + "\t");
                if (i < theRouteNames.size() - 1) detail.append(Html.fromHtml("&#8594;"));

            }

            return row;
        }

    }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

          if (id==R.id.home){
                 NavUtils.getParentActivityIntent(this);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
