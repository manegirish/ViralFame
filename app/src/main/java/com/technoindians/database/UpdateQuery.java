package com.technoindians.database;

import com.technoindians.constants.Constants;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 25/08/16.
 *         Last modified 27/08/2016
 */

class UpdateQuery {

    static String updateUtc(String utc, String user_id, String column) {

        return "UPDATE " + TableList.TABLE_UTC + " SET "
                + column + " = '" + utc + "' WHERE " + Constants.USER_ID + "=" + user_id;
    }

    public static String profile(String user_id, String column, String value) {

        return "UPDATE " + TableList.TABLE_PROFILE + " SET "
                + column + " = '" + value + "' WHERE " + Constants.USER_ID + " = " + user_id;
    }

    static String feed(String id, String column, String value) {

        return "UPDATE " + TableList.TABLE_WALL_FEED + " SET "
                + column + " = '" + value + "' WHERE " + Constants._ID + " = " + id;
    }
}
