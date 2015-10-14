package com.vibeosys.travelapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/9/2015.
 */

public class NewDataBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "TravelAppDb";

    private final Context mContext;

    public NewDataBase(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ANSWERS_TABLE = "CREATE TABLE Answer(" +
                "   AnswerId INT PRIMARY KEY     NOT NULL," +
                "   UserId                INT    NOT NULL," +
                "   DestId                INT    NOT NULL," +
                "   OptionId              INT    NOT NULL," +
                "   CreatedDate           DATETIME   NOT NULL" +
                " )";
        String CREATE_COMMENTSANDLIKES = "CREATE TABLE CommentAndLike(" +
                "   UserId       INT            NOT NULL," +
                "   DestId       INT            NOT NULL," +
                "   LIkeCount          INT     NOT NULL," +
                "   CommentText        TEXT    NULL," +
                "   UpdatedDate        DATETIME    NOT NULL" +
                ")";
        String CREATE_CONFIG_TABLE = "CREATE TABLE Config(" +
                "Key  TEXT PRIMARY KEY NOT NULL," +
                "Value     TEXT NOT NULL" +
                ")";
        String CREATE_DESTINATION_TABLE = "CREATE TABLE Destination(\n" +
                "   DestId INT PRIMARY KEY     NOT NULL," +
                "   DestName           TEXT    NOT NULL," +
                "   Lat              DOUBLE    NOT NULL," +
                "   Long             DOUBLE    NOT NULL" +
                " )";
        String CREATE_IMAGES_TABLE = "CREATE TABLE Images(" +
                "   ImageId INTEGER PRIMARY KEY    AUTOINCREMENT," +
                "   ImagePath           TEXT    NOT NULL," +
                "   UserId              INT     NOT NULL," +
                "   DestId              INT     NOT NULL," +
                "   ImageSeen           BOOLEAN NOT NULL" +
                ")";
        String CREATE_MYMAP_TABLE = "CREATE TABLE MyMap(" +
                "MapId    INTEGER PRIMARY KEY AUTOINCREMENT," +
                "RouteName     TEXT NOT NULL," +
                "RouteJson     TEXT NOT NULL," +
                "CreatedDate   DATETIME NOT NULL" +
                ")";
        String CREATE_MYIMAGES_TABLE = "CREATE TABLE My_Images(" +
                "   ImageId INTEGER PRIMARY KEY  AUTOINCREMENT," +
                "   ImagePath           TEXT   NOT NULL," +
                "   CreateDate          DATETIME   NOT NULL" +
                "    )";
        String CREATE_OPTION_TABLE = "CREATE TABLE Option(" +
                "   OptionId INT PRIMARY KEY     NOT NULL," +
                "      OptionText         TEXT   NOT NULL," +
                "   QuestionId         INT   NOT NULL" +
                "    )";
        String CREATE_QUESTION_TABLE = "CREATE TABLE Question(" +
                "QuestionId INT PRIMARY KEY NOT NULL," +
                "QuestionText  TeXT NOT NULL" +
                ")";
        String CREATE_SYNC_TABLE = "CREATE TABLE Sync(" +
                "SyncAutoNo INT PRIMARY KEY NOT NULL," +
                "UserId     INT NOT NULL," +
                "JsonSync   TEXT NOT NULL," +
                "TableName  TEXT NOT NULL      " +
                ")";
        String CREATE_TEMPDATA_TABLE = "CREATE TABLE TempData(\n" +
                "   Id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "   DestId           INT   NOT NULL,\n" +
                "   DestName         TEXT   NOT NULL,\n" +
                "   Lat              DOUBLE NOT NULL,\n" +
                "   Long             DOUBLE NOT NULL\n" +
                "    )";
        String CREATE_USER_DATA = "CREATE TABLE User(" +
                "UserId    INT PRIMARY KEY NOT NULL," +
                "UserName     TEXT NOT NULL" +
                ")";
        String CREATE_MYUSER_TABLE = "CREATE TABLE myUser(" +
                "UserId    INT PRIMARY KEY NOT NULL," +
                "UserName     TEXT NOT NULL," +
                "MobileNo     TEXT NOT NULL," +
                "Active       BOOLEAN NOT NULL" +
                ")";
        db.execSQL(CREATE_ANSWERS_TABLE);
        db.execSQL(CREATE_COMMENTSANDLIKES);
        db.execSQL(CREATE_CONFIG_TABLE);
        db.execSQL(CREATE_DESTINATION_TABLE);
        db.execSQL(CREATE_IMAGES_TABLE);
        db.execSQL(CREATE_MYMAP_TABLE);
        db.execSQL(CREATE_MYIMAGES_TABLE);
        db.execSQL(CREATE_OPTION_TABLE);
        db.execSQL(CREATE_QUESTION_TABLE);
        db.execSQL(CREATE_SYNC_TABLE);
        db.execSQL(CREATE_TEMPDATA_TABLE);
        db.execSQL(CREATE_USER_DATA);
        db.execSQL(CREATE_MYUSER_TABLE);

        Log.d("CREATED", "SUCCESS");
    }

    public void AddUser(int cUserId, String cUserName) {
        String Username = cUserName;
        int UserId = cUserId;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("UserId", UserId);
        values.put("UserName", Username);
        Log.d("ADDED", "SUCCESS");

        // Inserting Row
        db.insert("Destination", null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public void GetUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from User ", null);
        cursor.moveToFirst();
        Log.d("USERID", cursor.getString(0));
        Log.d("USERNAME", cursor.getString(1));
        db.close();
    }

    public void addDestinations(List<Destination> cList) {
        List<Destination> theDestList;
        theDestList = cList;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues theContent = new ContentValues();

        for (int i = 0; i < theDestList.size(); i++) {
            theContent.put("DestId", theDestList.get(i).getmDestId());
            theContent.put("DestName", theDestList.get(i).getmDestName());
            theContent.put("Lat", theDestList.get(i).getmLat());
            theContent.put("Long", theDestList.get(i).getmLong());
            db.insert("Destination", null, theContent);
            Log.d("DestId", String.valueOf(theDestList.get(i).getmDestId()));
            Log.d("DestName", String.valueOf(theDestList.get(i).getmDestId()));
            theContent.remove("DestId");
            theContent.remove("DestName");
            theContent.remove("Lat");
            theContent.remove("Long");
        }

        db.close();
    }

    public List<String> getDestNames() {
        List<String> mListDestNames = new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor mgetDest = sqLiteDatabase.rawQuery("select * from Destination", null);
        mgetDest.moveToFirst();
        do {
            mListDestNames.add(mgetDest.getString(mgetDest.getColumnIndex("DestName")));
        } while (mgetDest.moveToNext());

        sqLiteDatabase.close();
        return mListDestNames;
    }

    public List<TempData> GetLatLong(String cDestName) {
        String mDestNameq = cDestName;
        List<TempData> mTempData = new ArrayList<TempData>();
        SQLiteDatabase mGetLatLong = getReadableDatabase();
        String mSql = "select DestId,DestName,Lat,Long from Destination where DestName=?";
        Cursor mgetCursor = mGetLatLong.rawQuery(mSql, new String[]{mDestNameq});

        if (mgetCursor.moveToFirst()) {
            TempData tempData = new TempData();
            tempData.setmDestId(mgetCursor.getInt(mgetCursor.getColumnIndex("DestId")));

            tempData.setmLat(mgetCursor.getDouble(mgetCursor.getColumnIndex("Lat")));
            tempData.setmLong(mgetCursor.getDouble(mgetCursor.getColumnIndex("Long")));
            mTempData.add(tempData);

        }
        mGetLatLong.close();

        return mTempData;
    }


    public void SaveMapInTemp(List<TempData> cListSave, String cDestName) {
        List<TempData> mSaveList = null;
        mSaveList = cListSave;
        String mDestName = cDestName;
        SQLiteDatabase mSaveinTemp = getWritableDatabase();
        TempData mTempDatainMap = new TempData();
        ContentValues mSaveinTempValues = new ContentValues();
        mSaveinTempValues.put("DestId", mSaveList.get(0).getmDestId());
        mSaveinTempValues.put("DestName", mDestName);
        mSaveinTempValues.put("Lat", mSaveList.get(0).getmLat());
        mSaveinTempValues.put("Long", mSaveList.get(0).getmLong());
        Log.d("TempData DestId", mSaveList.get(0).getmDestId() + "");
        Log.d("TempData DestName", "" + mDestName);
        Log.d("TempData DestLat", String.valueOf(mSaveList.get(0).getmLat()) + "");
        Log.d("TempData DestLong", mSaveList.get(0).getmLong() + "");

        mSaveinTemp.insert("TempData", null, mSaveinTempValues);
        Log.d("TEMP TABLE", "SAVED IN TEMP TABLE");
        mSaveinTemp.close();
    }

    public HashMap<String, Double> mGetLatLongFromTemp() {
        HashMap<String, Double> theGetTempLatLong = new HashMap<>();
        try {
            SQLiteDatabase mGetLatLong = getReadableDatabase();
            Cursor mcursor = mGetLatLong.rawQuery("select Lat,Long from TempData ORDER BY Id DESC", null);

            theGetTempLatLong.put("Lat", mcursor.getDouble(mcursor.getColumnIndex("Lat")));
            Log.d("Temp Last Second Lat", String.valueOf(mcursor.getDouble(mcursor.getColumnIndex("Lat"))));
            theGetTempLatLong.put("Long", mcursor.getDouble(mcursor.getColumnIndex("Long")));
            mGetLatLong.close();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return theGetTempLatLong;

    }

    public List<MyDestination> GetFromTempLatLong() {
        List<MyDestination> mGetTempList = new ArrayList<>();
        SQLiteDatabase mGetFromTemp = getReadableDatabase();
        MyDestination mgetTemp;
        Cursor metTempCursor = mGetFromTemp.rawQuery("select * from TempData", null);
        metTempCursor.moveToFirst();
        if (metTempCursor.getCount() > 0) {
            do {
                mgetTemp = new MyDestination();
                // mgetTemp.setDestId(metTempCursor.getInt(metTempCursor.getColumnIndex("DestId")));
                mgetTemp.setDestName(metTempCursor.getString(metTempCursor.getColumnIndex("DestName")));
                //mgetTemp.setId(metTempCursor.getInt(metTempCursor.getColumnIndex("Id")));
                mgetTemp.setLat(metTempCursor.getDouble(metTempCursor.getColumnIndex("Lat")));
                mgetTemp.setLong(metTempCursor.getDouble(metTempCursor.getColumnIndex("Long")));
                mGetTempList.add(mgetTemp);
            } while (metTempCursor.moveToNext());


            mGetFromTemp.close();
            return mGetTempList;
        } else {
            return null;
        }
    }

    boolean mSaveMyImages(String cImagePath, String cDate) {
        SQLiteDatabase thesqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ImagePath", cImagePath);
        contentValues.put("CreatedDate", cDate);
        thesqLiteDatabase.insert("My_Images", null, contentValues);
        return true;
    }

    public void DeleteTempMaps() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("delete from TempData");
        Log.d("TEMPTABLE", "DELETED TEMP DATA");
        sqLiteDatabase.close();
    }

    public List<GetTemp> GetFromTemp() {
        List<GetTemp> mGetTempList = new ArrayList<>();
        SQLiteDatabase mGetFromTemp = getReadableDatabase();

        Cursor metTempCursor = mGetFromTemp.rawQuery("select * from TempData", null);

        if (metTempCursor.moveToFirst()) {
            do {
                GetTemp mgetTemp = new GetTemp();
                mgetTemp.setDestId(metTempCursor.getInt(metTempCursor.getColumnIndex("DestId")));
                mgetTemp.setDestName(metTempCursor.getString(metTempCursor.getColumnIndex("DestName")));
                mgetTemp.setId(metTempCursor.getInt(metTempCursor.getColumnIndex("Id")));
                mgetTemp.setLat(metTempCursor.getDouble(metTempCursor.getColumnIndex("Lat")));
                mgetTemp.setLong(metTempCursor.getDouble(metTempCursor.getColumnIndex("Long")));
                mGetTempList.add(mgetTemp);
            } while (metTempCursor.moveToNext());


            mGetFromTemp.close();
            return mGetTempList;
        } else {
            return null;
        }
    }

    boolean CheckTempData() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from TempData", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    boolean SaveinMapTable(String cMapTitle, String cJsonData, String cDate) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("RouteName", cMapTitle);
        contentValues.put("RouteJson", cJsonData);
        contentValues.put("CreatedDate", cDate);

        long out = sqLiteDatabase.insert("MyMap", null, contentValues);
        if (out == -1) return false;
        else return true;

    }

    List<Routes> getRouteList() {
        List<Routes> mRouteLists = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from MyMap", null);

        if (cursor.moveToFirst()) {
            do {
                Routes mRoutes = new Routes();
                mRoutes.setmRouteName(cursor.getString(cursor.getColumnIndex("RouteName")));
                mRoutes.setmRoutetripsNames(cursor.getString(cursor.getColumnIndex("RouteJson")));
                mRoutes.setmRouteDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                mRouteLists.add(mRoutes);
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return mRouteLists;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP DATABASE IF EXISTS Answer");
        db.execSQL("DROP DATABASE IF EXISTS CommentAndLike");
        db.execSQL("DROP DATABASE IF EXISTS Config");
        db.execSQL("DROP DATABASE IF EXISTS  Destination");
        db.execSQL("DROP DATABASE IF EXISTS Images");
        db.execSQL("DROP DATABASE IF EXISTS MyMap");
        db.execSQL("DROP DATABASE IF EXISTS My_Images");
        db.execSQL("DROP DATABASE IF EXISTS Option");
        db.execSQL("DROP DATABASE IF EXISTS Question");
        db.execSQL("DROP DATABASE IF EXISTS  Sync");
        db.execSQL("DROP DATABASE IF EXISTS  TempData");
        db.execSQL("DROP DATABASE IF EXISTS  User");
        db.execSQL("DROP DATABASE IF EXISTS myUser");
        // Create tables again
        onCreate(db);
    }
}
