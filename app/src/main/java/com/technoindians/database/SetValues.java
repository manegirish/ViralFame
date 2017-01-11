package com.technoindians.database;

import android.content.ContentValues;

import com.technoindians.constants.Constants;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 28/7/16.
 *         Last modified 27/08/2016
 */
public class SetValues {

    public static ContentValues profile(String user_id, String name, String skill_primary,
                                        String skill_secondary, String is_login,
                                        String profile_pic, String local_image, String user_type) {

        ContentValues profileValues = new ContentValues();
        profileValues.put(Constants.USER_ID, user_id);
        profileValues.put(Constants.NAME, name);
        profileValues.put(Constants.PRIMARY_SKILL, skill_primary);
        profileValues.put(Constants.SECONDARY_SKILL, skill_secondary);
        profileValues.put(Constants.LOGIN, is_login);
        profileValues.put(Constants.PROFILE_PIC, profile_pic);
        profileValues.put(Constants.LOCAL_PATH, local_image);
        profileValues.put(Constants.USER_TYPE, user_type);
        profileValues.put(Constants.ABOUT_ME, "-NA-");

        return profileValues;
    }

    public static ContentValues primarySkill(String id, String skill) {

        ContentValues profileValues = new ContentValues();
        profileValues.put(Constants.ID, id);
        profileValues.put(Constants.SKILL, skill);

        return profileValues;
    }

    public static ContentValues users(String id, String user_id, String name, String skill_primary,
                                      String user_type, String is_follow, String follower, String profile_pic) {

        ContentValues profileValues = new ContentValues();
        profileValues.put(Constants.ID, id);
        profileValues.put(Constants.USER_ID, user_id);
        profileValues.put(Constants.NAME, name);
        profileValues.put(Constants.PRIMARY_SKILL, skill_primary);
        profileValues.put(Constants.USER_TYPE, user_type);
        profileValues.put(Constants.IS_FOLLOW, is_follow);
        profileValues.put(Constants.FOLLOWER, follower);
        profileValues.put(Constants.PROFILE_PIC, profile_pic);

        return profileValues;
    }

    public static ContentValues utc(String user_id, String skill_primary,
                                    String skill_secondary, String profile, String wall_feed) {

        ContentValues profileValues = new ContentValues();
        profileValues.put(Constants.USER_ID, user_id);
        profileValues.put(TableList.TABLE_SKILL_PRIMARY, skill_primary);
        profileValues.put(TableList.TABLE_SKILL_SECONDARY, skill_secondary);
        profileValues.put(TableList.TABLE_PROFILE, profile);
        profileValues.put(TableList.TABLE_WALL_FEED, wall_feed);

        return profileValues;
    }

    public static ContentValues feed(String id, String user_id, String name, String type, String post_text,
                                     String media_type, String media_file, String media_size, String total_comments,
                                     String total_likes, String is_like, String date_of_post, String last_updated,
                                     String profile_pic, String skill, String is_follow) {

        ContentValues feedValues = new ContentValues();

        feedValues.put(Constants._ID, id);
        feedValues.put(Constants.USER_ID, user_id);
        feedValues.put(Constants.NAME, name);
        feedValues.put(Constants.USER_TYPE, type);
        feedValues.put(Constants.POST_TEXT, post_text);
        feedValues.put(Constants.MEDIA_TYPE, media_type);
        feedValues.put(Constants.MEDIA_FILE, media_file);
        feedValues.put(Constants.MEDIA_SIZE, media_size);
        feedValues.put(Constants.MEDIA_DURATION, "0");
        feedValues.put(Constants.TOTAL_COMMENTS, total_comments);
        feedValues.put(Constants.TOTAL_LIKES, total_likes);
        feedValues.put(Constants.IS_LIKE, is_like);
        feedValues.put(Constants.DATE_OF_POST, date_of_post);
        feedValues.put(Constants.LAST_UPDATED, last_updated);
        feedValues.put(Constants.PROFILE_PIC, profile_pic);
        feedValues.put(Constants.SKILL, skill);
        feedValues.put(Constants.IS_FOLLOW, is_follow);

        return feedValues;
    }

    public static ContentValues message(String id, String user_id, String msg, String name, String profile_pic,
                                        String user_type, String skills, String is_read, String total_reply,
                                        String last_updated, String message_type, String self) {

        ContentValues messageValues = new ContentValues();

        messageValues.put(Constants.ID, id);
        messageValues.put(Constants.USER_ID, user_id);
        messageValues.put(Constants.NAME, name);
        messageValues.put(Constants.MSG, msg);
        messageValues.put(Constants.PROFILE_PIC, profile_pic);
        messageValues.put(Constants.USER_TYPE, user_type);
        messageValues.put(Constants.SKILL, skills);
        messageValues.put(Constants.IS_READ, is_read);
        messageValues.put(Constants.TOTAL_REPLY, total_reply);
        messageValues.put(Constants.LAST_UPDATED, last_updated);
        messageValues.put(Constants.TYPE, message_type);
        messageValues.put(Constants.SELF, self);

        return messageValues;
    }
}
