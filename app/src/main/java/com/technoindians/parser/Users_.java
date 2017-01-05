package com.technoindians.parser;

import com.technoindians.constants.Constants;
import com.technoindians.message.Friends_;
import com.technoindians.network.JsonArrays_;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by girish on 20/7/16.
 */
public class Users_ {

    public static ArrayList<Friends_> parseUsers(String response) {
        ArrayList<Friends_> friendsList = new ArrayList<>();
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_USERS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_USERS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        result = responseObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            String user_id = responseObject.getString(Constants.USER_ID);
                            String name = responseObject.getString(Constants.NAME);
                            String type = responseObject.getString(Constants.TYPE);
                            String skill = responseObject.getString(Constants.SKILL);
                            String image = responseObject.getString(Constants.PROFILE_PIC);

                            Friends_ friends_ = new Friends_();

                            friends_.setStatus(1);
                            friends_.setUser_id(user_id);
                            friends_.setName(name);
                            friends_.setType(type);
                            friends_.setRole(skill);
                            friends_.setImages(image);

                            friendsList.add(friends_);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendsList;
    }

    public static int usersResult(String response) {
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_USERS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_USERS);
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
