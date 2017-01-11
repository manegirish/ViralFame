package com.technoindians.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.technoindians.constants.Constants;
import com.technoindians.message.Message_;
import com.technoindians.parser.FollowParser_;
import com.technoindians.parser.Messages_;
import com.technoindians.parser.Wall_;
import com.technoindians.peoples.Follow;
import com.technoindians.preferences.Preferences;
import com.technoindians.wall.Feed_;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 28/07/16.
 *         Last modified 27/08/2016
 */

public class MainDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = MainDatabaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "viral_main";
    protected SQLiteDatabase database;
    protected Context context;

    public MainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTable.CREATE_TABLE_PROFILE);
        db.execSQL(CreateTable.CREATE_TABLE_SKILL_PRIMARY);
        db.execSQL(CreateTable.CREATE_TABLE_SKILL_SECONDARY);
        db.execSQL(CreateTable.CREATE_TABLE_USERS);
        db.execSQL(CreateTable.CREATE_TABLE_UTC);
        db.execSQL(CreateTable.CREATE_TABLE_WALL_FEED);
        db.execSQL(CreateTable.CREATE_TABLE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    protected void openDB() {
        database = this.getReadableDatabase();
    }

    public long insertProfile(String user_id, String name, String skill_primary,
                              String skill_secondary, String is_login,
                              String profile_pic, String local_image, String user_type) {
        openDB();
        long result = 0;
        Cursor cursor = database.rawQuery(GetQuery.count(TableList.TABLE_PROFILE), null);
        cursor.moveToFirst();
        int count_one = cursor.getInt(0);

        if (count_one >= 1) {
            flushTables(TableList.TABLE_PROFILE);
            initUtc();
            result = database.insert(TableList.TABLE_PROFILE, null,
                    SetValues.profile(user_id, name, skill_primary, skill_secondary, is_login,
                            profile_pic, local_image, user_type));
            return result;
        }
        if (count_one == 0) {
            initUtc();
            result = database.insert(TableList.TABLE_PROFILE, null,
                    SetValues.profile(user_id, name, skill_primary, skill_secondary, is_login,
                            profile_pic, local_image, user_type));
        } else {
            result = database.update(TableList.TABLE_PROFILE, SetValues.profile(user_id, name, skill_primary,
                    skill_secondary, is_login, profile_pic, local_image, user_type), Constants.USER_ID + "=" + user_id, null);
        }
        Log.e(TAG, "count_one -> " + count_one + " result -> " + result);
        cursor.close();
        return result;
    }

    public void insertSkill(String id, String skill, String tableName) {
        openDB();
        long result = 0;
        Cursor cursor = database.rawQuery(GetQuery.countWhere(tableName,
                Constants.ID + "=" + id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count <= 0) {
            result = database.insert(tableName, null,
                    SetValues.primarySkill(id, skill));
        } else {
            result = database.update(tableName, SetValues.primarySkill(id, skill),
                    Constants.ID + "=" + id, null);
        }
        Log.e("insertSkillPrimary", "result -> " + result);
    }

    public ArrayList<HashMap<String, String>> getSkills(String tableName) {
        openDB();
        ArrayList<HashMap<String, String>> skillArray = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        Cursor mCursor = database.rawQuery(sql, null);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                while (mCursor.isAfterLast() == false) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(Constants.STATUS, "1");
                    map.put(Constants.ID, mCursor.getString(mCursor.getColumnIndex(Constants.ID)));
                    map.put(Constants.SKILL, mCursor.getString(mCursor.getColumnIndex(Constants.SKILL)));
                    skillArray.add(map);
                    mCursor.moveToNext();
                }
            }
        }
        Collections.reverse(skillArray);
        mCursor.close();
        return skillArray;
    }

    public void insertUser(String id, String user_id, String name, String skill_primary,
                           String user_type, String is_follow, String follower, String profile_pic) {

        openDB();
        long result = 0;
        Cursor cursor = database.rawQuery(GetQuery.countWhere(TableList.TABLE_USERS,
                Constants.ID + "=" + id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count <= 0) {
            result = database.insert(TableList.TABLE_USERS, null,
                    SetValues.users(id, user_id, name, skill_primary, user_type,
                            is_follow, follower, profile_pic));
        } else {
            result = database.update(TableList.TABLE_USERS, SetValues.users(id, user_id, name, skill_primary, user_type,
                    is_follow, follower, profile_pic),
                    Constants.ID + "=" + id, null);
        }
        Log.e("insertUser", "result -> " + result);
    }

    public ArrayList<Follow> getUsers(int type) {
        openDB();
        ArrayList<Follow> followList = new ArrayList<>();
        String sql = "SELECT * FROM " + TableList.TABLE_USERS + " WHERE " + Constants.FOLLOWER + " = " + type;
        Cursor mCursor = database.rawQuery(sql, null);
        if (mCursor != null) {
            followList = FollowParser_.parseDatabaseUsers(mCursor);
        }
        mCursor.close();
        return followList;
    }

    private void initUtc() {
        openDB();
        Cursor cursor = database.rawQuery(GetQuery.count(TableList.TABLE_UTC), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count > 1) {
            flushTables(TableList.TABLE_UTC);
            database.insert(TableList.TABLE_UTC, null,
                    SetValues.utc(Preferences.get(Constants.USER_ID), "0", "0", "0", "0"));
        } else if (count == 1) {
            String sql = "SELECT " + Constants.USER_ID + " FROM " + Constants.UTC;
            cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            String user_id = cursor.getString(0);
            if (!user_id.equalsIgnoreCase(Preferences.get(Constants.USER_ID))) {
                flushTables(TableList.TABLE_UTC);
                database.insert(TableList.TABLE_UTC, null,
                        SetValues.utc(Preferences.get(Constants.USER_ID), "0", "0", "0", "0"));
            }
        } else if (count <= 0) {
            database.insert(TableList.TABLE_UTC, null,
                    SetValues.utc(Preferences.get(Constants.USER_ID), "0", "0", "0", "0"));
        }
    }

    public void updateUtc(String utc, String user_id, String column) {
        openDB();
        Cursor cursor = database.rawQuery(UpdateQuery.updateUtc(utc, user_id, column), null);
        cursor.moveToFirst();
        cursor.close();
    }

    public String getUtc(String column, String user_id) {

        openDB();
        String sql = GetQuery.utc(column, user_id);
        Log.e("get utc", "sql => " + sql);
        Cursor mCursor = database.rawQuery(sql, null);
        mCursor.moveToFirst();
        String date = "0";
        if (mCursor != null) {
            date = mCursor.getString(0);
        }
        Log.e("utc()", "date => " + date);
        mCursor.close();
        return date;
    }

    public void updateProfile(String user_id, String column, String value) {
        openDB();
        Cursor cursor = database.rawQuery(UpdateQuery.profile(user_id, column, value), null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void insertFeed(String id, String user_id, String name, String type, String post_text, String media_type,
                           String media_file, String media_size, String total_comments, String total_likes,
                           String is_like, String date_of_post, String last_updated, String profile_pic, String skill, String is_follow) {

        long result;
        openDB();
        Cursor cursor = database.rawQuery(GetQuery.countWhere(TableList.TABLE_WALL_FEED,
                Constants._ID + "=" + id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);

        if (count == 1) {
            result = database.update(TableList.TABLE_WALL_FEED, SetValues.feed(id, user_id, name, type, post_text, media_type, media_file, media_size,
                    total_comments, total_likes, is_like, date_of_post, last_updated, profile_pic, skill, is_follow),
                    Constants._ID + "=" + id, null);
        } else {
            result = database.insert(TableList.TABLE_WALL_FEED, null,
                    SetValues.feed(id, user_id, name, type, post_text, media_type, media_file, media_size,
                            total_comments, total_likes, is_like, date_of_post, last_updated, profile_pic, skill, is_follow));
        }
        Log.e("insertFeed()", "count => " + count + " result => " + result);
    }

    public ArrayList<Feed_> getFeed(String count, String id) {

        openDB();
        ArrayList<Feed_> feedList = new ArrayList<>();

        String sql = GetQuery.feed(count, id);
        Log.e("get feed", "sql => " + sql);
        Cursor mCursor = database.rawQuery(sql, null);
        if (mCursor != null) {
            feedList = Wall_.databaseFeed(mCursor);
        }
        mCursor.close();
        return feedList;
    }

    Cursor fetchFeed() {
        openDB();
        String sql = "SELECT * FROM " + TableList.TABLE_WALL_FEED + " ORDER BY " + Constants.LAST_UPDATED + " DESC";
        Log.e("get feed", "sql => " + sql);
        return database.rawQuery(sql, null);
    }

    Cursor fetchSingleFeed(String column, String _id) {
        String sql = GetQuery.feedItem(column, _id);
        Log.e(TAG, "fetchSingleFeed: " + sql);
        return database.rawQuery(sql, null);
    }

    void updateFeed(String id, String column, String value) {
        openDB();
        Cursor cursor = database.rawQuery(UpdateQuery.feed(id, column, value), null);
        cursor.moveToFirst();
        cursor.close();
    }

    String getFeedItem(String column, String id) {

        openDB();
        String sql = GetQuery.feedItem(column, id);
        Log.e("get Feed Item", "sql => " + sql);
        Cursor mCursor = database.rawQuery(sql, null);
        if (mCursor != null) {
            mCursor.close();
            return "0";
        } else {
            mCursor.moveToFirst();
            String value = mCursor.getString(0);
            Log.e("getFeedItem()", "date => " + value);
            mCursor.close();
            return value;
        }
    }

    public void insertMessage(String id, String user_id, String msg, String name, String profile_pic, String user_type,
                              String skills, String is_read, String total_reply, String last_updated, String message_type, String self) {
        long result;
        openDB();
        Cursor cursor = database.rawQuery(GetQuery.countWhere(TableList.TABLE_MESSAGE,
                Constants.USER_ID + "=" + user_id), null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);

        if (count == 1) {
            result = database.update(TableList.TABLE_MESSAGE, SetValues.message(id, user_id, msg, name,
                    profile_pic, user_type, skills, is_read, total_reply, last_updated, message_type, self),
                    Constants.USER_ID + "=" + user_id, null);
        } else {
            result = database.insert(TableList.TABLE_MESSAGE, null,
                    SetValues.message(id, user_id, msg, name, profile_pic, user_type, skills, is_read,
                            total_reply, last_updated, message_type, self));
        }
        Log.e("insertMessage()", "count => " + count + " result => " + result);
    }

    public ArrayList<Message_> getMessage(String count) {

        openDB();
        ArrayList<Message_> messageList = new ArrayList<>();

        String sql = GetQuery.message(count);
        Log.e("get message", "sql => " + sql);
        Cursor mCursor = database.rawQuery(sql, null);
        if (mCursor != null) {
            messageList = Messages_.databaseMessage(mCursor);
        }
        mCursor.close();
        return messageList;
    }

    public void flushTables(String table_name) {
        openDB();
        Log.e("flushTables", " => " + table_name);
        String deleteQuery = "DELETE FROM " + table_name;
        Cursor cursor = database.rawQuery(deleteQuery, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void closeDB() {
        if (database != null && database.isOpen())
            database.close();
    }
}
