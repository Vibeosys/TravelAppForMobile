package com.vibeosys.travelapp.databaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vibeosys.travelapp.CommentsAndLikes;
import com.vibeosys.travelapp.Destination;
import com.vibeosys.travelapp.DestinationTempData;
import com.vibeosys.travelapp.GetTemp;
import com.vibeosys.travelapp.MyDestination;
import com.vibeosys.travelapp.MyImageDB;
import com.vibeosys.travelapp.Routes;
import com.vibeosys.travelapp.SendQuestionAnswers;
import com.vibeosys.travelapp.TempData;
import com.vibeosys.travelapp.UserDetails;
import com.vibeosys.travelapp.data.Answer;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.Images;
import com.vibeosys.travelapp.data.Like;
import com.vibeosys.travelapp.data.Option;
import com.vibeosys.travelapp.data.Sync;
import com.vibeosys.travelapp.data.User;
import com.vibeosys.travelapp.usersImages;
import com.vibeosys.travelapp.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahesh on 10/9/2015.
 */

public class NewDataBase extends SQLiteOpenHelper {
    //private static final String DB_NAME = "/data/data/com.vibeosys.travelapp/app_databases/TravelApp";

    private final Context mContext;

    public NewDataBase(Context context) {

        super(context, SessionManager.getInstance(context).getDatabaseName(), null, 1);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public boolean insertComment(List<Comment> listComment) {
        List<Comment> mListComment = null;
        SQLiteDatabase database = null;
        mListComment = listComment;
        ContentValues contentValues = null;
        long id = -1;
        try {
            database = getWritableDatabase();

            contentValues = new ContentValues();
            for (int i = 0; i < mListComment.size(); i++) {
                contentValues.put("commentText", listComment.get(i).getCommentText());
                contentValues.put("DestId", listComment.get(i).getDestId());
                contentValues.put("UserId", listComment.get(i).getUserId());
                id = database.insert("Comment_and_like", null, contentValues);
                Log.d("Updated Databse", String.valueOf(id));
                Log.d("Updated Column", listComment.get(i).getCommentText());
                contentValues.clear();
            }
            database.close();
            Log.d("Comment Table", "Inserted in Comment");
            if (id != -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Sync> getFromSync() {
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        List<Sync> syncTableData = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select * from Sync ", null);
            syncTableData = new ArrayList<>();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    do {
                        Sync sync = new Sync();
                        sync.setJsonSync(cursor.getString(cursor.getColumnIndex("JsonSync")));
                        syncTableData.add(sync);
                    } while (cursor.moveToNext());

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return syncTableData;
    }

    public boolean addDataToSync(String tableName, String UserId, String JsonSync) {
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        long id = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            contentValues.put("UserId", UserId);
            contentValues.put("JsonSync", JsonSync);
            contentValues.put("TableName", tableName);
            id = sqLiteDatabase.insert("Sync", null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.close();
        if (id != -1) {
            Log.d("SyncTable Updated Value", "" + id);
            return true;
        } else return false;
    }


    public int LikeCount(int DestId, String UserId, SQLiteDatabase sqLiteDatabase1) {
        int likeCount = 0;
        SQLiteDatabase sqLiteDatabase = sqLiteDatabase1;
        Cursor cursor = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select LikeCount from comment_and_like where destid=? and userid=?", new String[]{String.valueOf(DestId), String.valueOf(UserId)});
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    likeCount = cursor.getInt(cursor.getColumnIndex("LikeCount"));
                }
                cursor.close();

            }
            Log.d("Like Table Like Count", "" + likeCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return likeCount + 1;
    }

    public boolean insertLikes(List<Like> listLikes) {
        List<Like> mListLikes = null;
        SQLiteDatabase sqLiteDatabase = null;
        SQLiteDatabase sqLiteDatabase1 = null;
        mListLikes = listLikes;
        ContentValues contentValues = null;
        long id = -1;
        int countvalue = 0;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            for (int i = 0; i < mListLikes.size(); i++) {
                countvalue = LikeCount(mListLikes.get(i).getDestId(), mListLikes.get(i).getUserId(), sqLiteDatabase1);
                contentValues = new ContentValues();
                contentValues.put("LikeCount", countvalue);
                id = sqLiteDatabase.update("Comment_and_like", contentValues, "userid=? and DestId=?", new String[]{mListLikes.get(i).getUserId(),
                        String.valueOf(mListLikes.get(i).getDestId())});
                Log.d("Updated Databse", String.valueOf(id));
                contentValues.clear();
            }
            Log.d("Like_and_Comments Table", "Inserted in Like Table" + id);
            if (id != -1) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.close();
        return false;
    }

    public boolean insertDestination(List<com.vibeosys.travelapp.data.Destination> DestList) {
        List<com.vibeosys.travelapp.data.Destination> mDestList = null;
        mDestList = DestList;
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        long count = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();

            for (int i = 0; i < mDestList.size(); i++) {
                contentValues.put("DestId", mDestList.get(i).getDestId());
                contentValues.put("DestName", mDestList.get(i).getDestName());
                contentValues.put("Lat", mDestList.get(i).getLat());
                contentValues.put("Long", mDestList.get(i).getLong());
                count = sqLiteDatabase.insert("Destination", null, contentValues);
                contentValues.clear();
            }
            sqLiteDatabase.close();
            if (count != -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Destination Table", "Inserted in Destination" + count);
        return false;
    }

    public boolean insertImages(List<Images> ImagesList) {
        List<Images> mImagesList = null;
        mImagesList = ImagesList;
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        long count = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            for (int i = 0; i < mImagesList.size(); i++) {
                contentValues.put("DestId", mImagesList.get(i).getDestId());
                contentValues.put("UserId", mImagesList.get(i).getUserId());
                contentValues.put("ImageID", mImagesList.get(i).getImageId());
                contentValues.put("ImagePath", mImagesList.get(i).getImagePath());
                contentValues.put("ImageSeen", mImagesList.get(i).getImageSeen());
                count = sqLiteDatabase.insert("Images", null, contentValues);
                contentValues.clear();
            }
            sqLiteDatabase.close();
            if (count != -1) {
                Log.d("Images Table", "Inserted in Images");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean insertUsers(List<User> UsersList) {
        List<User> musersList = null;
        musersList = UsersList;
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        long count = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            for (int i = 0; i < musersList.size(); i++) {
                contentValues.put("UserId", musersList.get(i).getUserId());
                contentValues.put("UserName", musersList.get(i).getUserName());
                contentValues.put("PhotoURL", musersList.get(i).getPhotoURL());
                count = sqLiteDatabase.insert("User", null, contentValues);
                contentValues.clear();
            }
            sqLiteDatabase.close();
            if (count != -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("User Table", "Inserted in User Table" + count);
        return false;
    }

    public boolean insertQuestions(List<com.vibeosys.travelapp.data.Question> QuestionsList) {
        List<com.vibeosys.travelapp.data.Question> mQuestionsList = null;
        mQuestionsList = QuestionsList;
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        long count = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            for (int i = 0; i < mQuestionsList.size(); i++) {
                contentValues.put("QuestionId", mQuestionsList.get(i).getQuestionId());
                contentValues.put("QuestionText", mQuestionsList.get(i).getQuestionText());
                count = sqLiteDatabase.insert("Question", null, contentValues);
                contentValues.clear();
            }
            sqLiteDatabase.close();
            if (count != -1) {
                return true;
            }
            Log.d("Inserted in Question", "Question Table" + count);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertOptions(List<Option> OptionsList) {
        List<Option> mOptionsList = null;
        mOptionsList = OptionsList;
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        long count = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            for (int i = 0; i < mOptionsList.size(); i++) {
                contentValues.put("QuestionId", mOptionsList.get(i).getQuestionId());
                contentValues.put("OptionText", mOptionsList.get(i).getOptionText());
                contentValues.put("OptionId", mOptionsList.get(i).getOptionId());
                count = sqLiteDatabase.insert("Option", null, contentValues);
                contentValues.clear();
            }
            sqLiteDatabase.close();
            if (count != -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Qptions Table ", "Inserted in Option Table" + count);
        return false;
    }

    public boolean insertAnswers(List<Answer> AnswersList) {
        List<Answer> mAnswersList = null;
        mAnswersList = AnswersList;
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        long count = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            for (int i = 0; i < mAnswersList.size(); i++) {
                contentValues.put("AnswerId", mAnswersList.get(i).getAnswerId());
                contentValues.put("DestId", mAnswersList.get(i).getDestId());
                contentValues.put("OptionId", mAnswersList.get(i).getOptionId());
                contentValues.put("UserId", mAnswersList.get(i).getUserId());
                count = sqLiteDatabase.insert("Answer", null, contentValues);
                contentValues.clear();
            }
            sqLiteDatabase.close();
            if (count != -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Answer Table", "Inserted in Answer Table" + count);
        return false;
    }



    public Images imageUserLikeCount(String imageId) {
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        Images images=null;
        try {
            sqLiteDatabase = getReadableDatabase();
            String qurey="select UserName, comment_and_like.LikeCount,user.userid, comment_and_like.destid " +
                    " from Images inner join comment_and_like on images.DestId = comment_and_like.DestId " +
                    " and images.UserId = comment_and_like.UserId " +
                    " inner join user on images.UserId = user.UserId" +
                    " where images.ImageId = ?";
            cursor = sqLiteDatabase.rawQuery(qurey, new String[]{imageId});

            if (cursor != null) {
                if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                    do{
                        images=new Images(cursor.getString(cursor.getColumnIndex("UserName")),
                                cursor.getInt(cursor.getColumnIndex("LikeCount")), cursor.getInt(cursor.getColumnIndex("DestId")), cursor.getString(cursor.getColumnIndex("UserId")));
                    }while (cursor.moveToNext());

                }

            }
        cursor.close();
sqLiteDatabase.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    return images;
    }

    public List<usersImages> Images(int cDestId, Boolean showSeenImages) {
        List<usersImages> cImagePaths = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            String qureyToAppend = " and ImageSeen = 'false'";
            String mainQurey = "select * from Images natural join user where user.userid=images.userid and destid=? ";
            if (showSeenImages == false) {
                mainQurey += qureyToAppend;
            }
            cursor = sqLiteDatabase.rawQuery(mainQurey,
                    new String[]{String.valueOf(cDestId)});

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                                usersImages theUsersImages = new usersImages(
                                cursor.getString(cursor.getColumnIndex("ImageId")),
                                cursor.getString(cursor.getColumnIndex("ImagePath")),
                                cursor.getInt(cursor.getColumnIndex("DestId")),
                                cursor.getString(cursor.getColumnIndex("UserId")),
                                cursor.getString(cursor.getColumnIndex("UserName"))
                        );
                        cImagePaths.add(theUsersImages);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cImagePaths;
    }

    public List<CommentsAndLikes> DestinationComments(int DestId) {
        List<CommentsAndLikes> DestComments = null;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            DestComments = new ArrayList<>();
            cursor = sqLiteDatabase.rawQuery("select * " +
                    "from comment_and_like NATURAL JOIN user where destid=? and user.userid=comment_and_like.userid;", new String[]{String.valueOf(DestId)});
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        CommentsAndLikes commentsAndLikes = new CommentsAndLikes(
                                cursor.getString(cursor.getColumnIndex("UserId")),
                                cursor.getInt(cursor.getColumnIndex("DestId")),
                                cursor.getString(cursor.getColumnIndex("CommentText")),
                                cursor.getString(cursor.getColumnIndex("UserName")),
                                cursor.getString(cursor.getColumnIndex("PhotoURL"))
                        );
                        DestComments.add(commentsAndLikes);

                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DestComments;
    }

    public int Questions(int DestId) {
        int noOfQuestions = 0;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select * from question", null);
            if (cursor != null) {
                cursor.moveToFirst();
                noOfQuestions = cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noOfQuestions;
    }

    public boolean updateUser(String UserName, String EmailAddress, String UserId) {
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        int rowsid = 0;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            contentValues.put("UserName", UserName);
            contentValues.put("UserEmail", EmailAddress);
            rowsid = sqLiteDatabase.update("MyUser", contentValues, "UserId=?", new String[]{UserId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        contentValues.clear();
        sqLiteDatabase.close();
        if (rowsid > 0) {
            return true;
        } else return false;
    }


    public boolean updateUserInfo(String userId) {
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        long count = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            contentValues = new ContentValues();
            contentValues.put("UserId", userId);
            sqLiteDatabase.insert("MyUser", null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentValues.clear();
        sqLiteDatabase.close();
        if (count == -1) return false;
        else return true;

    }

    public int MsgCount(int cDestId) {
        int count = 0;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select * from answer where DestId=?", new String[]{String.valueOf(cDestId)});
            count = cursor.getCount();
            cursor.close();
            sqLiteDatabase.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    public void GetUser() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("select * from User ", null);
            cursor.moveToFirst();
            Log.d("USERID", cursor.getString(0));
            Log.d("USERNAME", cursor.getString(1));
            db.close();

        } catch (Exception e) {
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
        SQLiteDatabase mSaveinTemp = null;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MyImageDB> mUserImagesList() {
        List<MyImageDB> theUserImagesList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select ImageId,ImagePath,CreateDate from MyImages Order By CreateDate Desc", null);
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

        } catch (Exception e) {
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

    public List<SendQuestionAnswers> mListQuestions(String destId) {
        List<SendQuestionAnswers> mListQuestions = null;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            mListQuestions = new ArrayList<>();
            String qurey = "select distinct Question.QuestionId, Question.QuestionText" +
                    " from Question inner join options on Question.questionId=Options.questionId " +
                    " inner join answer on options.optionid=answer.OptionId " +
                    " where answer.destid=?";
            cursor = sqLiteDatabase.rawQuery(qurey, new String[]{destId});
            SendQuestionAnswers sendQuestionAnswers;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    sendQuestionAnswers = new SendQuestionAnswers();
                    sendQuestionAnswers.setmQuestionId(cursor.getInt(cursor.getColumnIndex("QuestionId")));
                    sendQuestionAnswers.setmQuestionText(cursor.getString(cursor.getColumnIndex("QuestionText")));
                    mListQuestions.add(sendQuestionAnswers);
                } while (cursor.moveToNext());
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mListQuestions;
    }

    public List<SendQuestionAnswers> listQuestions() {
        List<SendQuestionAnswers> listQuestions = null;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = getReadableDatabase();
            listQuestions = new ArrayList<>();
            cursor = sqLiteDatabase.rawQuery("select * from Question "
                    , null);
            SendQuestionAnswers sendQuestionAnswers;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    sendQuestionAnswers = new SendQuestionAnswers();
                    sendQuestionAnswers.setmQuestionId(cursor.getInt(cursor.getColumnIndex("QuestionId")));
                    sendQuestionAnswers.setmQuestionText(cursor.getString(cursor.getColumnIndex("QuestionText")));
                    listQuestions.add(sendQuestionAnswers);
                } while (cursor.moveToNext());
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listQuestions;
    }


    public List<UserDetails> answerlikesUsers(int optionId, String destId) {
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        List<UserDetails> answerUsersList = null;
        try {
            answerUsersList = new ArrayList<>();
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select UserName,UserId,DestId from answer natural join user where optionid=? and destid=?",
                    new String[]{String.valueOf(optionId), destId});
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    UserDetails userDetails = new UserDetails();
                    userDetails.setUsername(cursor.getString(cursor.getColumnIndex("UserName")));
                    userDetails.setUserId(cursor.getString(cursor.getColumnIndex("UserId")));
                    userDetails.setDestId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("DestId"))));
                    answerUsersList.add(userDetails);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return answerUsersList;
    }


    public int CountOfUsers(int cOptionId, String destId) {
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        int count = 0;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select * from answer where optionid=? and destid=?", new String[]{String.valueOf(cOptionId), destId});
            if (cursor != null && cursor.getCount() > 0) {
                count = cursor.getCount();
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    public List<SendQuestionAnswers> listQuestions(int cQuestionId) {
        List<SendQuestionAnswers> theListAskQuestions = null;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        int mQuestionId = cQuestionId;
        try {
            theListAskQuestions = new ArrayList<>();
            sqLiteDatabase = getReadableDatabase();
            SendQuestionAnswers sendQuestionAnswers;
            String qurey = "select OptionId, optionText " +
                    " from  OPTIONS " +
                    "where questionid = ? ";
            cursor = sqLiteDatabase.rawQuery(qurey, new String[]{String.valueOf(cQuestionId)});
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        sendQuestionAnswers = new SendQuestionAnswers();
                        sendQuestionAnswers.setmOptionId(cursor.getInt(0));
                        sendQuestionAnswers.setmOptionText(cursor.getString(1));
                        theListAskQuestions.add(sendQuestionAnswers);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            sqLiteDatabase.close();
            Log.d("theListAskQuestions", "" + theListAskQuestions.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return theListAskQuestions;
    }


    public List<SendQuestionAnswers> mListOptions(int cQuestionId, int destId) {
        List<SendQuestionAnswers> theListAskQuestions = null;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        int mQuestionId = cQuestionId;
        try {
            theListAskQuestions = new ArrayList<>();
            sqLiteDatabase = getReadableDatabase();
            SendQuestionAnswers sendQuestionAnswers;
            String qurey = "select distinct options.OptionId, optionText " +
                    "from  OPTIONS inner join answer  on options.OptionId == answer.OptionId " +
                    "where questionid = ? and DestId = ?;";
            cursor = sqLiteDatabase.rawQuery(qurey, new String[]{String.valueOf(cQuestionId), String.valueOf(destId)});
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        sendQuestionAnswers = new SendQuestionAnswers();
                        sendQuestionAnswers.setmOptionId(cursor.getInt(0));
                        sendQuestionAnswers.setmOptionText(cursor.getString(1));
                        theListAskQuestions.add(sendQuestionAnswers);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            sqLiteDatabase.close();
            Log.d("theListAskQuestions", "" + theListAskQuestions.size());
        } catch (Exception e) {
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


    public boolean mSaveMyImages(String cImagePath, String cDate) {
        try {
            SQLiteDatabase thesqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("ImagePath", cImagePath);
            contentValues.put("CreateDate", cDate);
            long rows = thesqLiteDatabase.insert("MyImages", null, contentValues);
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

    public boolean CheckTempData() {
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = null;
        boolean check = false;
        try {
            sqLiteDatabase = getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery("select * from TempData", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                check = true;
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return check;
    }

    public boolean SaveinMapTable(String cMapTitle, String cJsonData, String cDate) {
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

    public List<Routes> getRouteList() {
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

    public Routes getRoute(String routeName) {
        Routes mRoutes = new Routes();
        try {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from MyMap where RouteName='" + routeName + "'", null);

            if (cursor.moveToFirst()) {
                do {
                    mRoutes.setmRouteName(cursor.getString(cursor.getColumnIndex("RouteName")));
                    mRoutes.setmRoutetripsNames(cursor.getString(cursor.getColumnIndex("RouteJson")));
                    mRoutes.setmRouteDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    //mRouteLists.add(mRoutes);
                } while (cursor.moveToNext());
            }
            cursor.close();
            sqLiteDatabase.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mRoutes;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
