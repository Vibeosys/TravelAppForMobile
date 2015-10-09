package com.vibeosys.travelapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final String DB_NAME="TravelAppDb";

    private final Context mContext;

    public NewDataBase(Context context){
        super(context, DB_NAME, null, 1);
        this.mContext=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String  CREATE_ANSWERS_TABLE="CREATE TABLE Answer(" +
                "   AnswerId INT PRIMARY KEY     NOT NULL," +
                "   UserId                INT    NOT NULL," +
                "   DestId                INT    NOT NULL," +
                "   OptionId              INT    NOT NULL," +
                "   CreatedDate           DATE   NOT NULL" +
                " )";
        String CREATE_COMMENTSANDLIKES="CREATE TABLE CommentAndLike(" +
                "   UserId       INT            NOT NULL," +
                "   DestId       INT            NOT NULL," +
                "   LIkeCount          INT     NOT NULL," +
                "   CommentText        TEXT    NULL," +
                "   UpdatedDate        DATE    NOT NULL" +
                ")";
        String CREATE_CONFIG_TABLE="CREATE TABLE Config(" +
                "Key  TEXT PRIMARY KEY NOT NULL," +
                "Value     TEXT NOT NULL" +
                ")";
        String CREATE_DESTINATION_TABLE="CREATE TABLE Destination(\n" +
                "   DestId INT PRIMARY KEY     NOT NULL," +
                "   DestName           TEXT    NOT NULL," +
                "   Lat              DOUBLE    NOT NULL," +
                "   Long             DOUBLE    NOT NULL" +
                " )";
        String  CREATE_IMAGES_TABLE="CREATE TABLE Images(" +
                "   ImageId INT PRIMARY KEY     NOT NULL," +
                "   ImagePath           TEXT    NOT NULL," +
                "   UserId              INT     NOT NULL," +
                "   DestId              INT     NOT NULL," +
                "   ImageSeen           BOOLEAN NOT NULL" +
                ")";
        String CREATE_MYMAP_TABLE="CREATE TABLE MyMap(" +
                "MapId    INT PRIMARY KEY NOT NULL," +
                "RouteName     TEXT NOT NULL," +
                "RouteJson     TEXT NOT NULL," +
                "CreatedDate   DATE NOT NULL" +
                ")";
        String CREATE_MYIMAGES_TABLE="CREATE TABLE My_Images(" +
                "   ImageId INT PRIMARY KEY     NOT NULL," +
                "   ImagePath           TEXT   NOT NULL," +
                "   CreateDate          DATE   NOT NULL" +
                "    )";
        String CREATE_OPTION_TABLE="CREATE TABLE Option(" +
                "   OptionId INT PRIMARY KEY     NOT NULL," +
                "      OptionText         TEXT   NOT NULL," +
                "   QuestionId         INT   NOT NULL" +
                "    )";
        String CREATE_QUESTION_TABLE="CREATE TABLE Question(" +
                "QuestionId INT PRIMARY KEY NOT NULL," +
                "QuestionText  TeXT NOT NULL" +
                ")";
        String CREATE_SYNC_TABLE="CREATE TABLE Sync(" +
                "SyncAutoNo INT PRIMARY KEY NOT NULL," +
                "UserId     INT NOT NULL," +
                "JsonSync   TEXT NOT NULL," +
                "TableName  TEXT NOT NULL      " +
                ")";
        String CREATE_TEMPDATA_TABLE="CREATE TABLE TempData(\n" +
                "   Id INT AUTOINCREMENT,\n" +
                "   DestId           INT   NOT NULL,\n" +
                "   DestName         TEXT   NOT NULL,\n" +
                "   Lat              DOUBLE NOT NULL,\n" +
                "   Long             DOUBLE NOT NULL\n" +
                "    )";
        String CREATE_USER_DATA="CREATE TABLE User(" +
                "UserId    INT PRIMARY KEY NOT NULL," +
                "UserName     TEXT NOT NULL" +
                ")";
         String CREATE_MYUSER_TABLE="CREATE TABLE myUser(" +
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

        Log.d("CREATED","SUCCESS");
    }

 public  void AddUser(int cUserId,String cUserName){
 String Username=cUserName;
 int UserId=cUserId;

     SQLiteDatabase db = this.getWritableDatabase();

     ContentValues values = new ContentValues();
     values.put("UserId", UserId);
     values.put("UserName",Username);
     Log.d("ADDED", "SUCCESS");

     // Inserting Row
     db.insert("Destination", null, values);
     //2nd argument is String containing nullColumnHack
     db.close(); // Closing database connection
 }

public void GetUser(){
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor=db.rawQuery("select * from User ", null);
    cursor.moveToFirst();
    Log.d("USERID", cursor.getString(0));
    Log.d("USERNAME", cursor.getString(1));

}

public void addDestinations(List<Destination> cList){
List<Destination> theDestList;
theDestList=cList;
    SQLiteDatabase db = this.getWritableDatabase();
ContentValues theContent=new ContentValues();

for(int i=0;i<theDestList.size()-1;i++){
    theContent.put("DestId",theDestList.get(i).getmDestId());
    theContent.put("DestName",theDestList.get(i).getmDestName());
    theContent.put("Lat",theDestList.get(i).getmLat());
    theContent.put("Long",theDestList.get(i).getmLong());
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

public List<String> getDestNames(){
List<String> mListDestNames=new ArrayList<String>();
SQLiteDatabase sqLiteDatabase=getReadableDatabase();
Cursor mgetDest=sqLiteDatabase.rawQuery("select * from Destination",null);
mgetDest.moveToFirst();
do{
 mListDestNames.add(mgetDest.getString(1));
} while (mgetDest.moveToNext());

    sqLiteDatabase.close();
    return mListDestNames;
}

public List<TempData> GetLatLong(String cDestName){
String mDestNameq=cDestName;
List<TempData> mTempData=new ArrayList<TempData>();
SQLiteDatabase mGetLatLong=getReadableDatabase();
String mSql="select DestId,DestName,Lat,Long from Destination where DestName=?";
Cursor mgetCursor=mGetLatLong.rawQuery(mSql, new String[]{mDestNameq});

if(mgetCursor.moveToFirst()){
    TempData tempData=new TempData();
tempData.setmDestId(mgetCursor.getInt(mgetCursor.getColumnIndex("DestId")));
tempData.setmLat(mgetCursor.getDouble(mgetCursor.getColumnIndex("Lat")));
tempData.setmLong(mgetCursor.getDouble(mgetCursor.getColumnIndex("Long")));
mTempData.add(tempData);

}
    mGetLatLong.close();

    return mTempData;
}


public void SaveMapInTemp(List<TempData> cListSave,String cDestName) {
List<TempData> mSaveList=new ArrayList<TempData>();
    mSaveList=cListSave;
String mDestName=cDestName;
SQLiteDatabase mSaveinTemp=getWritableDatabase();
TempData mTempDatainMap=new TempData();
ContentValues mSaveinTempValues=new ContentValues();
mSaveinTempValues.put("DestId",mSaveList.get(0).getmDestId());
mSaveinTempValues.put("DestName",mDestName);
mSaveinTempValues.put("Lat",mSaveList.get(0).getmLat());
mSaveinTempValues.put("Long", mSaveList.get(0).getmLong());
    mSaveinTemp.insert("TempData", null, mSaveinTempValues);

    mSaveinTemp.close();
}

public HashMap<String,Double> mGetLatLongFromTemp(){
HashMap<String,Double> theGetTempLatLong=new HashMap<String,Double>();
SQLiteDatabase mGetLatLong=getReadableDatabase();
Cursor mcursor=mGetLatLong.rawQuery("select * from TempData",null);
    mcursor.moveToPosition(mcursor.getCount() - 1);
      theGetTempLatLong.put("Lat",Double.valueOf(mcursor.getString(mcursor.getColumnIndex("Lat"))));

     // theGetTempLatLong.put("Long", mcursor.getDouble(mcursor.getColumnIndex("Long")));
      mGetLatLong.close();
      return theGetTempLatLong;

}


public void DeleteTempMaps(){
SQLiteDatabase sqLiteDatabase=getWritableDatabase();
sqLiteDatabase.execSQL("delete from Temp");
sqLiteDatabase.close();
}

public void SaveinMap(){


}
    @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     //  db.execSQL("DROP DATABASE IF EXISTS "+DB_NAME );

        // Create tables again
        onCreate(db);
    }
}
