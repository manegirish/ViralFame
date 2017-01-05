package com.technoindians.database;

import com.technoindians.constants.Constants;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 25/08/16.
 * Last modified 27/08/2016
 *
 */

public class UpdateQuery {

    public static String updateUtc(String utc, String user_id, String column) {

        String updateSql = "UPDATE " + TableList.TABLE_UTC + " SET "
                + column + " = '" + utc + "' WHERE " + Constants.USER_ID + "=" + user_id;

        return updateSql;
    }

    public static String profile(String user_id, String column,String value) {

        String updateSql = "UPDATE " + TableList.TABLE_PROFILE + " SET "
                + column + " = '" + value + "' WHERE " + Constants.USER_ID + " = " + user_id;

        return updateSql;
    }

    //UPDATE wall_feed SET media_duration='20min' WHERE id=7
    public static String feed(String id, String column,String value) {

        String updateSql = "UPDATE " + TableList.TABLE_WALL_FEED + " SET "
                + column + " = '" + value + "' WHERE " + Constants.ID + " = " + id;

        return updateSql;
    }
}
