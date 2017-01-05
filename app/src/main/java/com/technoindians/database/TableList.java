package com.technoindians.database;

import java.util.ArrayList;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 27/6/16.
 * Last modified 27/08/2016
 *
 */

public class TableList {

    public static final String TABLE_PROFILE = "profile";
    public static final String TABLE_SKILL_PRIMARY = "primary_skills";
    public static final String TABLE_SKILL_SECONDARY = "secondary_skills";
    public static final String TABLE_USERS = "users_list";
    public static final String TABLE_UTC = "utc";
    public static final String TABLE_WALL_FEED = "wall_feed";
    public static final String TABLE_MESSAGE = "message";


    public static ArrayList<String> getTablelist() {
        ArrayList<String> tablesList = new ArrayList<>();

        tablesList.add(TABLE_PROFILE);
        tablesList.add(TABLE_SKILL_PRIMARY);
        tablesList.add(TABLE_SKILL_SECONDARY);
        tablesList.add(TABLE_USERS);
        tablesList.add(TABLE_UTC);
        tablesList.add(TABLE_MESSAGE);

        return tablesList;
    }

}
