package com.technoindians.parser;

import android.util.Log;

import com.technoindians.constants.Constants;
import com.technoindians.network.JsonArrays_;
import com.technoindians.portfolio.Images_;
import com.technoindians.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by girish on 2/8/16.
 */
public class Portfolio_ {


    public static int portfolioResult(String response) {
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_PORTFOLIO)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_PORTFOLIO);
                    JSONObject responseObject = jsonArray.getJSONObject(0);
                    result = responseObject.getInt(Constants.STATUS);
                    Log.e("portfolioResult","result -> "+result+" responseObject -> "+responseObject);
                } else {
                    result = 12;
                }
            } else {
                //internal error
                result = 12;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = 11;
        }
        return result;
    }

    public static String portfolio(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_PORTFOLIO)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_PORTFOLIO);
                    JSONObject responseObject = jsonArray.getJSONObject(0);
                    String about_me = responseObject.getString(Constants.ABOUT_ME);
                    return about_me;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return "NA";
    }

    public static ArrayList<Images_> galleryImage(String user_id,String response,String jsonName) {
        ArrayList<Images_> imagesList = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.has(jsonName)) {
                JSONArray jsonArray = jsonObject.getJSONArray(jsonName);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject responseObject = jsonArray.getJSONObject(i);

                    String wall_id = responseObject.getString(Constants.WALL_ID);
                    String path = responseObject.getString(Constants.PATH);
                    String like_count = responseObject.getString(Constants.TOTAL_LIKES);

                    Images_ images_ = new Images_();
                    images_.setWallId(wall_id);
                    images_.setPath(path);
                    images_.setLikeCount(like_count);
                    images_.setUserId(user_id);
                    imagesList.add(images_);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imagesList;
    }

    public static int galleryResult(String response,String jsonName) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(jsonName)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(jsonName);
                    JSONObject responseObject = jsonArray.getJSONObject(0);
                    return responseObject.getInt(Constants.STATUS);
                }else {
                    return 11;
                }
            }else {
                return 12;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return 11;
        }
    }

    public static HashMap<String,String> portfolioFriends(String response) {
        HashMap<String,String> profileMap = new HashMap<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_PORTFOLIO)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_PORTFOLIO);
                    JSONObject responseObject = jsonArray.getJSONObject(0);
                    String about_me = responseObject.getString(Constants.ABOUT_ME);
                    String follower_count = responseObject.getString(Constants.FOLLOWER_COUNT);
                    String following_count = responseObject.getString(Constants.FOLLOWING_COUNT);

                    profileMap.put(Constants.ABOUT_ME,about_me);
                    profileMap.put(Constants.FOLLOWER_COUNT,follower_count);
                    profileMap.put(Constants.FOLLOWING_COUNT,following_count);
                    profileMap.put(Constants.STATUS,"1");

                    return profileMap;
                }else {
                    profileMap.put(Constants.STATUS,"11");
                    return profileMap;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
