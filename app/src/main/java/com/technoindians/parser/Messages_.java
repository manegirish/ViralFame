package com.technoindians.parser;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.technoindians.constants.Constants;
import com.technoindians.database.InsertOperations;
import com.technoindians.message.Details_;
import com.technoindians.message.Message_;
import com.technoindians.network.JsonArrays_;
import com.technoindians.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by girish on 20/7/16.
 */
public class Messages_ {

    private static InsertOperations insertOperations;

    public static ArrayList<Message_> parseMessage(String response, Context context) {

        insertOperations = new InsertOperations(context);

        ArrayList<Message_> messageList = new ArrayList<>();
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_MESSAGES)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_MESSAGES);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        result = responseObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            String id = responseObject.getString(Constants.ID);
                            String user_id = responseObject.getString(Constants.USER_ID);
                            String msg = responseObject.getString(Constants.MSG);
                            String name = responseObject.getString(Constants.NAME);
                            String profile_pic = responseObject.getString(Constants.PROFILE_PIC);
                            String user_type = responseObject.getString(Constants.USER_TYPE);
                            String skills = responseObject.getString(Constants.SKILL);
                            String is_read = responseObject.getString(Constants.IS_READ);
                            String total_unread = responseObject.getString(Constants.TOTAL_UNREAD);
                            String send_date = responseObject.getString(Constants.SEND_DATE);
                            String message_type = "1";
                            String self = "0";
                            if (Integer.parseInt(user_id)
                                    == Integer.parseInt(Preferences.get(Constants.USER_ID))) {
                                self = "1";
                            }

                            Message_ messages_ = new Message_();
                            messages_.setStatus(1);
                            messages_.setId(id);
                            messages_.setUserId(user_id);
                            messages_.setMessage(msg);
                            messages_.setName(name);
                            messages_.setProfile_pic(profile_pic);
                            messages_.setType(user_type);
                            messages_.setSkill(skills);
                            messages_.setRead(is_read);
                            messages_.setCount(total_unread);
                            messages_.setSelf(self);
                            messages_.setDate(send_date);
                            messages_.setMessageType(message_type);

                            insertOperations.insertMessage(id, user_id, msg, name,
                                    profile_pic, user_type, skills, is_read, total_unread, send_date, message_type, self);

                            messageList.add(messages_);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public static int messageResult(String response) {
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_MESSAGES)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_MESSAGES);
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

    public static ArrayList<Details_> parseDetails(String response) {
        ArrayList<Details_> replyList = new ArrayList<>();
        int result = 11;
        if (response!=null){
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has(JsonArrays_.MESSAGE_DETAILS)){
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.MESSAGE_DETAILS);
                    for (int i = 0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.getInt(Constants.STATUS)==1){

                            String id = object.getString(Constants.ID);
                            String msg = object.getString(Constants.MSG);
                            String user_id = object.getString(Constants.FROM_ID);
                            String name = object.getString(Constants.NAME);
                            String type = "1";//masterObject.getString(Constants.TYPE);
                            String time = object.getString(Constants.SEND_DATE);
                            String self = "0";

                            if (Integer.parseInt(user_id)
                                    == Integer.parseInt(Preferences.get(Constants.USER_ID))) {
                                self = "1";
                            }
                            //Log.e("message ->",""+msg+" self -> "+self+" user_id -> "+user_id);
                            Details_ details_ = new Details_();
                            details_.setId(id);
                            details_.setDescription(msg);
                            details_.setName(name);
                            details_.setDate(time);
                            details_.setSelf(self);
                            replyList.add(details_);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.reverse(replyList);
        return replyList;
    }

    public static int detailsResult(String response) {
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.has(JsonArrays_.MESSAGE_DETAILS)){
                JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.MESSAGE_DETAILS);
                    JSONObject object = jsonArray.getJSONObject(0);
                    result = object.getInt(Constants.STATUS);
                } else {
                    result = 12;
                }

        } catch (JSONException e) {
            e.printStackTrace();
            result = 11;
        }
        return result;
    }

    public static ArrayList<Message_> databaseMessage(Cursor mCursor) {
        ArrayList<Message_> messageList = new ArrayList<>();

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                while (mCursor.isAfterLast() == false) {
                    String id = mCursor.getString(mCursor.getColumnIndex(Constants.ID));
                    String user_id = mCursor.getString(mCursor.getColumnIndex(Constants.USER_ID));
                    String msg = mCursor.getString(mCursor.getColumnIndex(Constants.MSG));
                    String name = mCursor.getString(mCursor.getColumnIndex(Constants.NAME));
                    String profile_pic = mCursor.getString(mCursor.getColumnIndex(Constants.PROFILE_PIC));
                    String user_type = mCursor.getString(mCursor.getColumnIndex(Constants.USER_TYPE));
                    String skills = mCursor.getString(mCursor.getColumnIndex(Constants.SKILL));
                    String is_read = mCursor.getString(mCursor.getColumnIndex(Constants.IS_READ));
                    String total_reply = mCursor.getString(mCursor.getColumnIndex(Constants.TOTAL_REPLY));
                    String last_updated = mCursor.getString(mCursor.getColumnIndex(Constants.LAST_UPDATED));
                    String message_type = "1";
                    String self = "0";
                    if (Integer.parseInt(user_id)
                            == Integer.parseInt(Preferences.get(Constants.USER_ID))) {
                        self = "1";
                    }
                    Log.e("databaseMessage()", "id => " + id);
                    Message_ messages_ = new Message_();
                    messages_.setStatus(1);
                    messages_.setId(id);
                    messages_.setUserId(user_id);
                    messages_.setMessage(msg);
                    messages_.setName(name);
                    messages_.setProfile_pic(profile_pic);
                    messages_.setType(user_type);
                    messages_.setSkill(skills);
                    messages_.setRead(is_read);
                    messages_.setCount(total_reply);
                    messages_.setSelf(self);
                    messages_.setDate(last_updated);
                    messages_.setMessageType(message_type);

                    messageList.add(messages_);

                    mCursor.moveToNext();
                }
            }
        }
        return messageList;
    }
}
