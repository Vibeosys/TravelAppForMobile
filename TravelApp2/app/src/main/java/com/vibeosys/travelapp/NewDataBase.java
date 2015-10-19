package com.vibeosys.travelapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/9/2015.
 */

public class NewDataBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "TravelApp";

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
        String CREATE_IMAGES_TABLE = "CREATE TABLE usersImages(" +
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
        /*db.execSQL(CREATE_ANSWERS_TABLE);
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
        db.execSQL(CREATE_MYUSER_TABLE);*/

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

    List<usersImages> Images(int cDestId) {
        List<usersImages> cImagePaths = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=null;
        Cursor cursor=null;
                try {
                    sqLiteDatabase=getReadableDatabase();
                    cursor = sqLiteDatabase.rawQuery("select * from Images where not imageseen and destid=?", new String[]{String.valueOf(cDestId)});
                    if (cursor != null) {
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            do {
                                usersImages theUsersImages = new usersImages(
                                        cursor.getInt(cursor.getColumnIndex("ImageId")),
                                        cursor.getString(cursor.getColumnIndex("ImagePath")),
                                        cursor.getInt(cursor.getColumnIndex("DestId")),
                                        cursor.getInt(cursor.getColumnIndex("UserId"))
                                );
                                cImagePaths.add(theUsersImages);
                            } while (cursor.moveToNext());
                        }
                    }
                    cursor.close();
                    sqLiteDatabase.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

        return cImagePaths;
    }

    int MsgCount(int cDestId) {
        int count = 0;
        SQLiteDatabase sqLiteDatabase=null;
        Cursor cursor=null;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select * from answer where DestId=?", new String[]{String.valueOf(cDestId)});
            count = cursor.getCount();
            cursor.close();
            sqLiteDatabase.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    int ImageCount(int cDestId) {
        int count = 0;
        SQLiteDatabase sqLiteDatabase=null;
        Cursor cursor=null;
        try{
sqLiteDatabase   = getReadableDatabase();
             cursor = sqLiteDatabase.rawQuery("select * from images where not imageseen and DestId=?", new String[]{String.valueOf(cDestId)});
            count = cursor.getCount();
            cursor.close();
            sqLiteDatabase.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public void GetUser() {
        SQLiteDatabase db=null;
        Cursor cursor=null;
        try {
            db = this.getReadableDatabase();
           cursor  = db.rawQuery("select * from User ", null);
            cursor.moveToFirst();
            Log.d("USERID", cursor.getString(0));
            Log.d("USERNAME", cursor.getString(1));
            db.close();

        }catch (Exception e)
        {
e.printStackTrace();
        }
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

    public HashMap<String, Integer> getDestNames() {
        HashMap<String, Integer> mListDestNames = new HashMap<>();
        try {

            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor mgetDest = sqLiteDatabase.rawQuery("select * from Destination", null);
            if (mgetDest != null) {
                if (mgetDest.getCount() > 0) {
                    mgetDest.moveToFirst();
                    do {
                        mListDestNames.put(mgetDest.getString(mgetDest.getColumnIndex("DestName")), mgetDest.getInt(mgetDest.getColumnIndex("DestId")));
                    } while (mgetDest.moveToNext());
                }
                mgetDest.close();
                sqLiteDatabase.close();
                return mListDestNames;
            }
        } catch (CursorIndexOutOfBoundsException e) {
            Log.e("Exception", e.toString());
        } catch (SQLiteException e) {
            Log.e("Exception", e.toString());
        }
        return null;
    }


    public List<TempData> GetLatLong(int cDestId) {
        int mDestNameq = cDestId;
        List<TempData> mTempData = new ArrayList<TempData>();
        try {


            SQLiteDatabase mGetLatLong = getReadableDatabase();
            String mSql = "select DestId,Lat,Long from Destination where DestId=?";
            Cursor mgetCursor = mGetLatLong.rawQuery(mSql, new String[]{String.valueOf(mDestNameq)});
            if (mgetCursor.moveToFirst()) {
                TempData tempData = new TempData();
                tempData.setmDestId(mgetCursor.getInt(mgetCursor.getColumnIndex("DestId")));
                tempData.setmLat(mgetCursor.getDouble(mgetCursor.getColumnIndex("Lat")));
                tempData.setmLong(mgetCursor.getDouble(mgetCursor.getColumnIndex("Long")));
                mTempData.add(tempData);

            }
            mgetCursor.close();
            mGetLatLong.close();
        } catch (CursorIndexOutOfBoundsException e) {
            Log.d("GetLatLong", e.getMessage());
        }
        return mTempData;
    }


    public void SaveMapInTemp(List<TempData> cListSave, String cDestName) {
        List<TempData> mSaveList = null;
        mSaveList = cListSave;
        String mDestName = cDestName;
        SQLiteDatabase mSaveinTemp=null;
        try {
            mSaveinTemp = getWritableDatabase();
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<MyImageDB> mUserImagesList() {
        List<MyImageDB> theUserImagesList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=null;
        Cursor cursor=null;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select ImageId,ImagePath,CreateDDate from MyImages Order By CreateDate Desc", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MyImageDB theMyImages = new MyImageDB(
                            cursor.getInt(cursor.getColumnIndex("ImageId")),
                            cursor.getString(cursor.getColumnIndex("ImagePath")),
                            cursor.getString(cursor.getColumnIndex("CreateDate"))
                    );
                    theUserImagesList.add(theMyImages);
                } while (cursor.moveToNext());

            }
            cursor.close();
            sqLiteDatabase.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return theUserImagesList;
    }

    public DestinationTempData mGetLatLongFromTemp(int theCurrentDestinationId) {
        DestinationTempData destinationTempData = null;
        SQLiteDatabase mGetLatLong = null;
        Cursor mcursor = null;
        try {
            mGetLatLong = getReadableDatabase();
            mcursor = mGetLatLong.rawQuery("select Id, DestId,DestName,Lat,Long from TempData Where DestId != ? ORDER BY Id DESC",
                    new String[]{String.valueOf(theCurrentDestinationId)});
            if (mcursor != null && mcursor.moveToFirst()) {
                destinationTempData = new DestinationTempData(mcursor.getInt(mcursor.getColumnIndex("Id")),
                        mcursor.getInt(mcursor.getColumnIndex("DestId")),
                        mcursor.getDouble(mcursor.getColumnIndex("Lat")),
                        mcursor.getDouble(mcursor.getColumnIndex("Long")),
                        mcursor.getString(mcursor.getColumnIndex("DestName"))
                );

            }
            mcursor.close();
            mGetLatLong.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return destinationTempData;

    }

public List<SendQuestionAnswers> mListQuestions(){
    List<SendQuestionAnswers> mListQuestions=null;
    SQLiteDatabase sqLiteDatabase=null;
    Cursor cursor=null;
    try{
        sqLiteDatabase=getReadableDatabase();
        mListQuestions=new ArrayList<>();
        cursor=sqLiteDatabase.rawQuery("select questionid,questiontext from question",null);
        SendQuestionAnswers sendQuestionAnswers;
        if(cursor!=null&&cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                sendQuestionAnswers=new SendQuestionAnswers();
sendQuestionAnswers.setmQuestionId(cursor.getInt(cursor.getColumnIndex("QuestionId")));
sendQuestionAnswers.setmQuestionText(cursor.getString(cursor.getColumnIndex("QuestionText")));
                mListQuestions.add(sendQuestionAnswers);
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
    }catch (Exception e){
        e.printStackTrace();
    }
return mListQuestions;
}
public int CountOfUsers(int cOptionId){
    SQLiteDatabase sqLiteDatabase=null;
    Cursor cursor=null;
    int count=0;
    try{
        sqLiteDatabase=getReadableDatabase();
        cursor=sqLiteDatabase.rawQuery("select * from answer where optionid=?",new String[]{String.valueOf(cOptionId)});
        if(cursor!=null&&cursor.getCount()>0){
            cursor.moveToFirst();
            count=cursor.getCount();
        }
        cursor.close();
        sqLiteDatabase.close();
    }catch (Exception e){
        e.printStackTrace();
    }
return count;
}



public  List<SendQuestionAnswers> mListOptions(int cQuestionId){
    List<SendQuestionAnswers> theListAskQuestions=null;
    SQLiteDatabase sqLiteDatabase=null;
    Cursor cursor=null;
    int mQuestionId=cQuestionId;
    try{
        theListAskQuestions=new ArrayList<>();
        sqLiteDatabase=getReadableDatabase();
        SendQuestionAnswers sendQuestionAnswers;
        cursor=sqLiteDatabase.rawQuery("select OpTIONID,optionText from  OPTIONS where questionid=?",new String[]{String.valueOf(cQuestionId)});
        if(cursor!=null){
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                do{
                    sendQuestionAnswers=new SendQuestionAnswers();
                    sendQuestionAnswers.setmOptionId(cursor.getInt(0));
                    sendQuestionAnswers.setmOptionText(cursor.getString(1));
                    theListAskQuestions.add(sendQuestionAnswers);
                    }while (cursor.moveToNext());
            }
        }
cursor.close();
sqLiteDatabase.close();
        Log.d("theListAskQuestions", "" + theListAskQuestions.size());
    }catch (Exception e){
        e.printStackTrace();
    }
return theListAskQuestions;
}


    public List<MyDestination> GetFromTempLatLong() {
        List<MyDestination> mGetTempList = new ArrayList<>();
        SQLiteDatabase mGetFromTemp = getReadableDatabase();
        MyDestination mgetTemp;
        try {
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
                metTempCursor.close();
                mGetFromTemp.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mGetTempList;
    }


    boolean mSaveMyImages(String cImagePath, String cDate) {
        try {
            SQLiteDatabase thesqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("ImagePath", cImagePath);
            contentValues.put("CreateDate", cDate);
            long rows = thesqLiteDatabase.insert("My_Images", null, contentValues);
thesqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    public void DeleteTempMaps() {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.execSQL("delete from TempData");
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("TEMPTABLE", "DELETED TEMP DATA");

    }

    public List<GetTemp> GetFromTemp() {
        List<GetTemp> mGetTempList = new ArrayList<>();
        SQLiteDatabase mGetFromTemp = null;
        Cursor metTempCursor = null;
        try {
            mGetFromTemp = getReadableDatabase();

            metTempCursor = mGetFromTemp.rawQuery("select * from TempData", null);
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

                metTempCursor.close();
                mGetFromTemp.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mGetTempList;
    }

    boolean CheckTempData() {
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = null;
        boolean check=false;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select * from TempData", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                check=true;
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return check;
    }

    boolean SaveinMapTable(String cMapTitle, String cJsonData, String cDate) {
        long out = 0;
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("RouteName", cMapTitle);
            contentValues.put("RouteJson", cJsonData);
            contentValues.put("CreatedDate", cDate);

            out = sqLiteDatabase.insert("MyMap", null, contentValues);
sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (out == -1) return false;
        else return true;

    }

    List<Routes> getRouteList() {
        List<Routes> mRouteLists = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from MyMap", null);

            if (cursor.moveToFirst()) {
                do {
                    Routes mRoutes = new Routes();
                    mRoutes.setmRouteName(cursor.getString(cursor.getColumnIndex("RouteName")));
                    mRoutes.setmRoutetripsNames(cursor.getString(cursor.getColumnIndex("RouteJson")));
                    mRoutes.setmRouteDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    mRouteLists.add(mRoutes);
                } while (cursor.moveToNext());
            }
            cursor.close();
            sqLiteDatabase.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mRouteLists;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP DATABASE IF EXISTS Answer");
        db.execSQL("DROP DATABASE IF EXISTS CommentAndLike");
        db.execSQL("DROP DATABASE IF EXISTS Config");
        db.execSQL("DROP DATABASE IF EXISTS  Destination");
        db.execSQL("DROP DATABASE IF EXISTS usersImages");
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