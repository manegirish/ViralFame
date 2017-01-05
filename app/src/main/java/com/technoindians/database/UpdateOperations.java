package com.technoindians.database;

import android.content.Context;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 22/08/16.
 * Last modified 27/08/2016
 *
 */

public class UpdateOperations {

    private Context context;
    private MainDatabaseHelper mainDatabaseHelper;

    public UpdateOperations(Context context){
        this.context=context;
        mainDatabaseHelper = new MainDatabaseHelper(this.context);
    }

    public void updateUtc(String utc,String user_id,String column){
        mainDatabaseHelper.updateUtc(utc,user_id,column);
        mainDatabaseHelper.closeDB();
    }

    public void updateProfile(String user_id, String column,String value) {
        mainDatabaseHelper.updateProfile(user_id,column,value);
        mainDatabaseHelper.closeDB();
    }

    public void updateFeed(String id, String column,String value) {
        mainDatabaseHelper.updateFeed(id,column,value);
        mainDatabaseHelper.closeDB();
    }
}
