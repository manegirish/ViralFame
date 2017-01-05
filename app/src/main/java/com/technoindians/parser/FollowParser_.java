package com.technoindians.parser;

import android.content.Context;
import android.database.Cursor;

import com.technoindians.constants.Constants;
import com.technoindians.database.InsertOperations;
import com.technoindians.network.JsonArrays_;
import com.technoindians.peoples.Follow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by girish on 12/8/16.
 */

public class FollowParser_ {

    private static  InsertOperations insertOperations;

    public static ArrayList<Follow> parse(String response, String arrayName, Context context) {

        insertOperations = new InsertOperations(context);

        ArrayList<Follow> followList = new ArrayList<>();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(arrayName)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(arrayName);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        int result = responseObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            String id = responseObject.getString(Constants.ID);
                            String user_id = responseObject.getString(Constants.USER_ID);
                            String name = responseObject.getString(Constants.NAME);
                            String skill = responseObject.getString(Constants.PRIMARY_SKILL);
                            String type = responseObject.getString(Constants.USER_TYPE);
                            String is_follow = responseObject.getString(Constants.IS_FOLLOW);
                            String follower = "0";
                            if (arrayName.equalsIgnoreCase(JsonArrays_.FOLLOWER)){
                                follower = "1";
                            }
                            String profile_pic = responseObject.getString(Constants.PROFILE_PIC);
                            String last_updated = responseObject.getString(Constants.LAST_UPDATED);

                            Follow follow = new Follow();
                            follow.setStatus(1);
                            follow.setId(id);
                            follow.setUserId(user_id);
                            follow.setName(name);
                            follow.setType(type);
                            follow.setPhoto(profile_pic);
                            follow.setUpdated(last_updated);
                            follow.setIsFollow(is_follow);
                            follow.setSkill(skill);

                            followList.add(follow);

                            insertOperations.insertUser(id, user_id, name, skill, type,is_follow, follower, profile_pic);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return followList;
    }

    public static ArrayList<Follow> parseDatabaseUsers(Cursor mCursor) {
        ArrayList<Follow> followList = new ArrayList<>();

        if (mCursor!=null) {
            if (mCursor.moveToFirst()) {
                while (mCursor.isAfterLast() == false) {
                    String id = mCursor.getString(mCursor.getColumnIndex(Constants.ID));
                    String user_id = mCursor.getString(mCursor.getColumnIndex(Constants.USER_ID));
                    String name = mCursor.getString(mCursor.getColumnIndex(Constants.NAME));
                    String skill = mCursor.getString(mCursor.getColumnIndex(Constants.PRIMARY_SKILL));
                    String type = mCursor.getString(mCursor.getColumnIndex(Constants.USER_TYPE));
                    String is_follow = mCursor.getString(mCursor.getColumnIndex(Constants.IS_FOLLOW));
                    String profile_pic =  mCursor.getString(mCursor.getColumnIndex(Constants.PROFILE_PIC));
                    String last_updated = "0";
                   // Log.e("parseDatabaseUsers","name -> "+name);

                    Follow follow = new Follow();
                    follow.setStatus(1);
                    follow.setId(id);
                    follow.setUserId(user_id);
                    follow.setName(name);
                    follow.setType(type);
                    follow.setPhoto(profile_pic);
                    follow.setUpdated(last_updated);
                    follow.setIsFollow(is_follow);
                    follow.setSkill(skill);

                    followList.add(follow);

                    mCursor.moveToNext();
                }
            }
        }
        Collections.reverse(followList);
        return followList;
    }
    public static int followResult(String response,String arrayName) {
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(arrayName)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(arrayName);
                    JSONObject responseObject = jsonArray.getJSONObject(0);
                    result = responseObject.getInt(Constants.STATUS);
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
}
