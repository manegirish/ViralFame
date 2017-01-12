package com.technoindians.database;

import android.content.Context;
import android.database.Cursor;

import com.technoindians.message.Message_;
import com.technoindians.peoples.Follow;
import com.technoindians.wall.Feed_;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 08/08/16.
 *         Last modified 27/08/2016
 */

public class RetrieveOperation {

    private Context context;
    private MainDatabaseHelper mainDatabaseHelper;

    public RetrieveOperation(Context context){
        this.context=context;
        mainDatabaseHelper = new MainDatabaseHelper(this.context);
    }

    public ArrayList<HashMap<String,String>> getSkill(String tableName){
        ArrayList<HashMap<String,String>> skillArray = mainDatabaseHelper.getSkills(tableName);
        mainDatabaseHelper.closeDB();

        return skillArray;
    }

    public ArrayList<Follow> getUsers(int type){
        ArrayList<Follow> followList = mainDatabaseHelper.getUsers(type);
        mainDatabaseHelper.closeDB();
        return followList;
    }

    public ArrayList<Message_> getMessage(String count){
        ArrayList<Message_> messageList = mainDatabaseHelper.getMessage(count);
        mainDatabaseHelper.closeDB();
        return messageList;
    }

    public ArrayList<Feed_> getFeed(String count, String id){
        ArrayList<Feed_> feedList = mainDatabaseHelper.getFeed(count,id);
        mainDatabaseHelper.closeDB();
        return feedList;
    }
    public String getUtc(String column,String user_id) {

        String date = mainDatabaseHelper.getUtc(column,user_id);
        mainDatabaseHelper.closeDB();

        return date;
    }
    public Cursor fetchFeed() {
        mainDatabaseHelper.openDB();
        return mainDatabaseHelper.fetchFeed();
    }

    public Cursor fetchSingleFeed(String column, String _id) {
        mainDatabaseHelper.openDB();
        return mainDatabaseHelper.fetchSingleFeed(column, _id);
    }

    public String getFeedItem(String column,String id){

        String value = mainDatabaseHelper.getFeedItem(column,id);
        mainDatabaseHelper.closeDB();

        return value;
    }
}