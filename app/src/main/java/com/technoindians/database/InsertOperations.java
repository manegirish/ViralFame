package com.technoindians.database;

import android.content.Context;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 28/7/16.
 * Last modified 27/08/2016
 *
 */

public class InsertOperations {

    private Context context;
    private MainDatabaseHelper mainDatabaseHelper;

    public InsertOperations(Context context){
        this.context=context;
        mainDatabaseHelper = new MainDatabaseHelper(this.context);
    }


    public long insertProfile(String user_id, String name, String skill_primary,
                              String skill_secondary, String is_login,
                              String profile_pic, String local_image,String user_type){
        long result = 0;

        mainDatabaseHelper.insertProfile(user_id, name, skill_primary, skill_secondary,
                is_login, profile_pic, local_image,user_type);
        mainDatabaseHelper.closeDB();

        return result;
    }

    public void insertSkillPrimary(String id, String skill,String tableName){

        mainDatabaseHelper.insertSkill(id,skill,tableName);
        mainDatabaseHelper.closeDB();

    }
    public void insertUser(String id,String user_id, String name, String skill_primary,
                           String user_type,String is_follow,String follower,String profile_pic){


        mainDatabaseHelper.insertUser(id, user_id, name, skill_primary, user_type,
                is_follow, follower, profile_pic);
        mainDatabaseHelper.closeDB();

    }

    public void insertFeed(String id, String user_id, String name, String type, String post_text,String media_type,
                           String media_file, String media_size, String total_comments,String total_likes,
                           String is_like, String date_of_post, String last_updated,String profile_pic,
                           String skill,String is_follow) {

        mainDatabaseHelper.insertFeed(id, user_id, name, type, post_text,media_type,media_file, media_size,
                total_comments,total_likes,is_like, date_of_post, last_updated,profile_pic, skill,is_follow);
        mainDatabaseHelper.closeDB();
    }

    public void insertMessage(String id,String user_id, String msg, String name, String profile_pic,String user_type,
                              String skills, String is_read, String total_reply,String last_updated, String message_type, String self) {

        mainDatabaseHelper.insertMessage(id,user_id, msg, name,
                profile_pic,user_type, skills, is_read, total_reply,last_updated, message_type, self);
        mainDatabaseHelper.closeDB();

    }

}

