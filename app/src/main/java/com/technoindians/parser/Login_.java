package com.technoindians.parser;

import android.content.Context;
import android.util.Log;

import com.technoindians.constants.Constants;
import com.technoindians.database.InsertOperations;
import com.technoindians.directory.DirectoryList;
import com.technoindians.library.FileCheck;
import com.technoindians.library.ImageDownloader;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.Urls;
import com.technoindians.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Girish M
 *         Created on 09/07/16.
 *         Last modified 01/08/2016
 */

public class Login_ {


    public static int parse(String response, Context context) {
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.LOG_IN)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.LOG_IN);
                    JSONObject responseObject = jsonArray.getJSONObject(0);
                    result = responseObject.getInt(Constants.STATUS);
                    if (result == 1) {
                        String user_id = responseObject.getString(Constants.USER_ID);
                        String name = responseObject.getString(Constants.NAME);
                        String skill_primary = responseObject.getString(Constants.SKILL);
                        String skill_secondary = responseObject.getString(Constants.SECONDARY_SKILL);

                        if (skill_secondary!=null&&!skill_secondary.equalsIgnoreCase("N/A")){
                            parseSecondarySkills(skill_secondary);
                        }

                        String is_login = "1";
                        String profile_pic = responseObject.getString(Constants.PROFILE_PIC);

                        ImageDownloader.downloadImage(Urls.DOMAIN + profile_pic, FileCheck.getFileName(profile_pic), false, context, 1);

                        String local_image = DirectoryList.DIR_MAIN + FileCheck.getFileName(profile_pic);
                        String user_type = responseObject.getString(Constants.USER_TYPE);

                        saveData(user_id, name, skill_primary, skill_secondary,
                                is_login, profile_pic, local_image, user_type, context);
                    }
                }
            } else {
                //internal error
                result = 12;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<HashMap<String, String>> parseSecondarySkills(String response) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString(Constants.ID);
                String skill = jsonObject.getString(Constants.SKILL);
                HashMap<String, String> map = new HashMap<>();
                map.put(Constants.ID, id);
                map.put(Constants.SKILL, skill);
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("parseSecondarySkills","-> "+list);
        return list;
    }

    private static void saveData(String user_id, String name, String skill_primary,
                                 String skill_secondary, String is_login,
                                 String profile_pic, String local_image, String user_type, Context context) {
        Preferences.clear();
        Preferences.save(Constants.USER_ID, user_id);
        Preferences.save(Constants.USER_TYPE, user_type);
        Preferences.save(Constants.NAME, name);
        Preferences.save(Constants.LOGIN, is_login);
        Preferences.save(Constants.PRIMARY_SKILL, skill_primary);
        Preferences.save(Constants.SECONDARY_SKILL, skill_secondary);
        Preferences.save(Constants.LOCAL_PATH, local_image);
        Preferences.save(Constants.PROFILE_PIC, profile_pic);

        InsertOperations insertOperations = new InsertOperations(context);
        insertOperations.insertProfile(user_id, name, skill_primary, skill_secondary,
                is_login, profile_pic, local_image, user_type);
    }

}
