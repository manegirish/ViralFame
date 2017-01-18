package com.technoindians.parser;

import android.content.Context;
import android.database.Cursor;

import com.technoindians.constants.Constants;
import com.technoindians.database.InsertOperations;
import com.technoindians.database.TableList;
import com.technoindians.database.UpdateOperations;
import com.technoindians.network.JsonArrays_;
import com.technoindians.preferences.Preferences;
import com.technoindians.wall.Comment_;
import com.technoindians.wall.Feed_;
import com.technoindians.wall.Liked_;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Girish Mane on 9/7/16.
 */
public class Wall_ {

    private static UpdateOperations updateOperations;
    private static InsertOperations insertOperations;


    public static ArrayList<Feed_> parseFeed(String response, Context context) {
        updateOperations = new UpdateOperations(context);
        insertOperations = new InsertOperations(context);

        ArrayList<Feed_> feedList = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (response != null) {
                if (jsonObject.has(JsonArrays_.POST)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.POST);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        int result = responseObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            String id = responseObject.getString(Constants.ID);
                            String user_id = responseObject.getString(Constants.USER_ID);
                            String name = responseObject.getString(Constants.USER_NAME);
                            String type = responseObject.getString(Constants.USER_TYPE);
                            String post_text = responseObject.getString(Constants.POST_TEXT);
                            String media_type = responseObject.getString(Constants.MEDIA_TYPE);
                            String media_file = responseObject.getString(Constants.MEDIA_FILE);
                            String total_comments = responseObject.getString(Constants.TOTAL_COMMENTS);
                            String total_likes = responseObject.getString(Constants.TOTAL_LIKES);
                            String is_like = responseObject.getString(Constants.IS_LIKE);
                            String date_of_post = responseObject.getString(Constants.DATE_OF_POST);
                            String last_updated = responseObject.getString(Constants.LAST_UPDATED);
                            String profile_pic = responseObject.getString(Constants.PROFILE_PIC);
                            String skills = responseObject.getString(Constants.SKILL);
                            String media_size = "0";
                            String is_follow = responseObject.getString(Constants.IS_FOLLOW);

                            if (responseObject.has(Constants.MEDIA_SIZE)) {
                                media_size = responseObject.getString(Constants.MEDIA_SIZE);
                                if (media_size.contains("N/A")) {
                                    media_size = "0";
                                }
                            }

                            Feed_ feed_ = new Feed_();
                            feed_.setId(id);
                            feed_.setUser_id(user_id);
                            feed_.setName(name);
                            feed_.setType(type);
                            feed_.setPost_text(post_text);
                            feed_.setMedia_type(media_type);
                            feed_.setMedia_file(media_file);
                            feed_.setTotal_comments(total_comments);
                            feed_.setTotal_likes(total_likes);
                            feed_.setIs_like(is_like);
                            feed_.setDate_of_post(date_of_post);
                            feed_.setLast_updated(last_updated);
                            feed_.setProfile_pic(profile_pic);
                            feed_.setSkill(skills);
                            feed_.setSize(media_size);
                            feed_.setFollow(is_follow);

                            insertOperations.insertFeed(id, user_id, name, type, post_text, media_type, media_file, media_size,
                                    total_comments, total_likes, is_like, date_of_post, last_updated, profile_pic, skills, is_follow);

                            feedList.add(feed_);
                        }
                    }
                }
                String utc = "0";
                if (jsonObject.has(Constants.UTC)) {
                    utc = jsonObject.getString(Constants.UTC);
                }
                updateOperations.updateUtc(utc, Preferences.get(Constants.USER_ID), TableList.TABLE_WALL_FEED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return feedList;
    }

    public static int feedResult(String response) {
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.POST)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.POST);
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

    public static ArrayList<Comment_> parseComment(String response) {
        ArrayList<Comment_> commentList = new ArrayList<>();
        //network error
        int result = 11;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.has(JsonArrays_.GET_COMMENTS)) {
                JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_COMMENTS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject responseObject = jsonArray.getJSONObject(i);
                    result = responseObject.getInt(Constants.STATUS);
                    if (result == 1) {
                        String id = responseObject.getString(Constants.ID);
                        String comment = responseObject.getString(Constants.COMMENT);
                        String user_id = responseObject.getString(Constants.USER_ID);
                        String name = responseObject.getString(Constants.NAME);
                        String last_updated = responseObject.getString(Constants.DATE_OF_POST);
                        String profile_pic = responseObject.getString(Constants.PROFILE_PIC);

                        Comment_ comment_ = new Comment_();
                        comment_.setId(id);
                        comment_.setUserId(user_id);
                        comment_.setName(name);
                        comment_.setComment(comment);
                        comment_.setProfile_pic(profile_pic);
                        comment_.setLast_updated(last_updated);

                        commentList.add(comment_);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.reverse(commentList);
        return commentList;
    }

    public static int commentResult(String response) {
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_COMMENTS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_COMMENTS);
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

    public static ArrayList<Liked_> parseLiked(String response) {
        ArrayList<Liked_> likedList = new ArrayList<>();
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.LIKE_USERS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.LIKE_USERS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        result = responseObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            String id = responseObject.getString(Constants.ID);
                            String user_id = responseObject.getString(Constants.USER_ID);
                            String name = responseObject.getString(Constants.NAME);
                            String user_type = responseObject.getString(Constants.TYPE);
                            String is_follow = responseObject.getString(Constants.IS_FOLLOW);
                            String profile_pic = responseObject.getString(Constants.PROFILE_PIC);
                            String skill = responseObject.getString(Constants.SKILLS);

                            Liked_ liked_ = new Liked_();
                            liked_.setId(id);
                            liked_.setUserId(user_id);
                            liked_.setName(name);
                            liked_.setUserType(user_type);
                            liked_.setIsFollow(is_follow);
                            liked_.setProfile_pic(profile_pic);
                            liked_.setSkill(skill);

                            likedList.add(liked_);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return likedList;
    }


    public static int likedResult(String response) {
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.LIKE_USERS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.LIKE_USERS);
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

    public static ArrayList<Feed_> databaseFeed(Cursor mCursor) {
        ArrayList<Feed_> feedList = new ArrayList<>();

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                while (mCursor.isAfterLast() == false) {
                    String id = mCursor.getString(mCursor.getColumnIndex(Constants.ID));
                    String user_id = mCursor.getString(mCursor.getColumnIndex(Constants.USER_ID));
                    String name = mCursor.getString(mCursor.getColumnIndex(Constants.NAME));
                    String type = mCursor.getString(mCursor.getColumnIndex(Constants.USER_TYPE));
                    String post_text = mCursor.getString(mCursor.getColumnIndex(Constants.POST_TEXT));
                    String media_type = mCursor.getString(mCursor.getColumnIndex(Constants.MEDIA_TYPE));
                    String media_file = mCursor.getString(mCursor.getColumnIndex(Constants.MEDIA_FILE));
                    String total_comments = mCursor.getString(mCursor.getColumnIndex(Constants.TOTAL_COMMENTS));
                    String total_likes = mCursor.getString(mCursor.getColumnIndex(Constants.TOTAL_LIKES));
                    String is_like = mCursor.getString(mCursor.getColumnIndex(Constants.IS_LIKE));
                    String date_of_post = mCursor.getString(mCursor.getColumnIndex(Constants.DATE_OF_POST));
                    String last_updated = mCursor.getString(mCursor.getColumnIndex(Constants.LAST_UPDATED));
                    String profile_pic = mCursor.getString(mCursor.getColumnIndex(Constants.PROFILE_PIC));
                    String skills = mCursor.getString(mCursor.getColumnIndex(Constants.SKILL));
                    String media_size = mCursor.getString(mCursor.getColumnIndex(Constants.MEDIA_SIZE));
                    String is_follow = mCursor.getString(mCursor.getColumnIndex(Constants.IS_FOLLOW));

                    Feed_ feed_ = new Feed_();
                    feed_.setId(id);
                    feed_.setUser_id(user_id);
                    feed_.setName(name);
                    feed_.setType(type);
                    feed_.setPost_text(post_text);
                    feed_.setMedia_type(media_type);
                    feed_.setMedia_file(media_file);
                    feed_.setTotal_comments(total_comments);
                    feed_.setTotal_likes(total_likes);
                    feed_.setIs_like(is_like);
                    feed_.setDate_of_post(date_of_post);
                    feed_.setLast_updated(last_updated);
                    feed_.setProfile_pic(profile_pic);
                    feed_.setSkill(skills);
                    feed_.setSize(media_size);
                    feed_.setFollow(is_follow);

                    feedList.add(feed_);

                    mCursor.moveToNext();
                }
            }
        }
        return feedList;
    }

    public static int operationParser(String response, String jsonName) {
        int result = 12;
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has(jsonName)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(jsonName);
                    JSONObject object = jsonArray.getJSONObject(0);
                    result = object.getInt(Constants.STATUS);
                } else {
                    result = 11;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
