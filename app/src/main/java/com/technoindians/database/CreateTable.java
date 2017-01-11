package com.technoindians.database;

import com.technoindians.constants.Constants;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 28/07/16.
 * Last modified 11/01/2017
 */

class CreateTable {

    static final String CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TableList.TABLE_PROFILE + "("
            + Constants.USER_ID + " INTEGER PRIMARY KEY,"
            + Constants.NAME + " VARCHAR(200),"
            + Constants.PRIMARY_SKILL + " VARCHAR(200),"
            + Constants.ABOUT_ME + " VARCHAR(500),"
            + Constants.SECONDARY_SKILL + " INTEGER(200),"
            + Constants.LOGIN + " INTEGER(10),"
            + Constants.USER_TYPE + " INTEGER(10),"
            + Constants.PROFILE_PIC + " VARCHAR(300),"
            + Constants.LOCAL_PATH + " VARCHAR(300)" + ")";

    static final String CREATE_TABLE_SKILL_PRIMARY = "CREATE TABLE "
            + TableList.TABLE_SKILL_PRIMARY + "("
            + Constants.ID + " INTEGER PRIMARY KEY,"
            + Constants.SKILL + " VARCHAR(300)" + ")";

    static final String CREATE_TABLE_SKILL_SECONDARY = "CREATE TABLE "
            + TableList.TABLE_SKILL_SECONDARY + "("
            + Constants.ID + " INTEGER PRIMARY KEY,"
            + Constants.SKILL + " VARCHAR(300)" + ")";

    static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TableList.TABLE_USERS + "("
            + Constants.ID + " INTEGER PRIMARY KEY,"
            + Constants.USER_ID + " INTEGER (100),"
            + Constants.NAME + " VARCHAR(200),"
            + Constants.PRIMARY_SKILL + " VARCHAR(200),"
            + Constants.USER_TYPE + " INTEGER(10),"
            + Constants.IS_FOLLOW + " INTEGER(10),"
            + Constants.FOLLOWER + " INTEGER(10),"
            + Constants.PROFILE_PIC + " VARCHAR(300)" + ")";

    static final String CREATE_TABLE_UTC = "CREATE TABLE "
            + TableList.TABLE_UTC + "("
            + Constants.USER_ID + " INTEGER PRIMARY KEY,"
            + TableList.TABLE_SKILL_PRIMARY + " VARCHAR(150),"
            + TableList.TABLE_SKILL_SECONDARY + " VARCHAR(150),"
            + TableList.TABLE_PROFILE + " VARCHAR(150),"
            + TableList.TABLE_WALL_FEED + " VARCHAR(150)" + ")";

    static final String CREATE_TABLE_WALL_FEED = "CREATE TABLE "
            + TableList.TABLE_WALL_FEED + "("
            + Constants._ID + " INTEGER PRIMARY KEY,"
            + Constants.USER_ID + " INTEGER(50),"
            + Constants.NAME + " VARCHAR(250),"
            + Constants.USER_TYPE + " INTEGER(10),"
            + Constants.POST_TEXT + " VARCHAR(500),"
            + Constants.MEDIA_TYPE + " INTEGER(50),"
            + Constants.MEDIA_FILE + " VARCHAR(500),"
            + Constants.MEDIA_SIZE + " VARCHAR(100),"
            + Constants.MEDIA_DURATION + " VARCHAR(300),"
            + Constants.TOTAL_COMMENTS + " INTEGER(150),"
            + Constants.TOTAL_LIKES + " INTEGER(150),"
            + Constants.IS_LIKE + " INTEGER(50),"
            + Constants.IS_FOLLOW + " INTEGER(50),"
            + Constants.DATE_OF_POST + " INTEGER(250),"
            + Constants.LAST_UPDATED + " INTEGER(250),"
            + Constants.PROFILE_PIC + " VARCHAR(500),"
            + Constants.SKILL + " VARCHAR(250)" + ")";

    static final String CREATE_TABLE_MESSAGE = "CREATE TABLE "
            + TableList.TABLE_MESSAGE + "("
            + Constants.ID + " INTEGER(100) ,"
            + Constants.USER_ID + " INTEGER(50) PRIMARY KEY,"
            + Constants.NAME + " VARCHAR(250),"
            + Constants.MSG + " VARCHAR(500),"
            + Constants.USER_TYPE + " INTEGER(50),"
            + Constants.IS_READ + " INTEGER(50),"
            + Constants.TOTAL_REPLY + " INTEGER(100),"
            + Constants.TYPE + " INTEGER(50),"
            + Constants.LAST_UPDATED + " INTEGER(250),"
            + Constants.SELF + " INTEGER(50),"
            + Constants.PROFILE_PIC + " VARCHAR(500),"
            + Constants.SKILL + " VARCHAR(250)" + ")";
}
