package com.technoindians.database;

import com.technoindians.constants.Constants;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 28/7/16.
 * Last modified 27/08/2016
 *
 */


public class GetQuery {

    public static String countWhere(String table_name,String whereCondition){
        String sql =  "SELECT COUNT(*) AS COUNTS FROM "+table_name+" WHERE "+whereCondition;
        return sql;
    }

    public static String count(String table_name){
        String sql =  "SELECT COUNT(*) AS COUNTS FROM "+table_name;
        return sql;
    }

    //SELECT * FROM message ORDER BY  last_updated  DESC LIMIT 5
    public static String message(String count){
        String sql =  "SELECT * FROM "+TableList.TABLE_MESSAGE+" ORDER BY "+ Constants.LAST_UPDATED +
                " DESC LIMIT "+count;
        return sql;
    }

    public static String feed(String count, String last_updated){
        //SELECT * FROM wall_feed WHERE id > 21 ORDER BY last_updated DESC LIMIT 5
        if (last_updated.equalsIgnoreCase("0")){
            String sql = "SELECT * FROM " + TableList.TABLE_WALL_FEED + " WHERE " + Constants.LAST_UPDATED + ">" + last_updated + " ORDER BY " + Constants.LAST_UPDATED +
                    " DESC LIMIT " + count;
            return sql;
        }else {
            String sql = "SELECT * FROM " + TableList.TABLE_WALL_FEED + " WHERE " + Constants.LAST_UPDATED + "<" + last_updated + " ORDER BY " + Constants.LAST_UPDATED +
                    " DESC LIMIT " + count;
            return sql;
        }
    }

    //SELECT media_duration FROM wall_feed WHERE id=7
    public static String feedItem(String column,String id){
    String sql =  "SELECT "+column+" FROM "+TableList.TABLE_WALL_FEED+" WHERE " + Constants.ID +
            " = "+id;
    return sql;
}
    //SELECT wall_feed FROM utc WHERE user_id=5
    public static String utc(String column,String user_id){
        String sql =  "SELECT "+column+" FROM "+TableList.TABLE_UTC+" WHERE "+ Constants.USER_ID+"="+user_id;
        return sql;
    }
}
