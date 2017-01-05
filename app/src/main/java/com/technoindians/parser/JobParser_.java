package com.technoindians.parser;

import com.technoindians.constants.Constants;
import com.technoindians.network.JsonArrays_;
import com.technoindians.opportunities.Applicant_;
import com.technoindians.opportunities.Jobs_;
import com.technoindians.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by girish on 26/7/16.
 */
public class JobParser_ {


    public static ArrayList<Jobs_> parseJobs(String response) {
        ArrayList<Jobs_> feedList = new ArrayList<>();
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_JOBS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_JOBS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        result = responseObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            String id = responseObject.getString(Constants.ID);
                            String title = responseObject.getString(Constants.TITLE);
                            String position = responseObject.getString(Constants.POSITION);
                            String description = responseObject.getString(Constants.DESCRIPTION);
                            String organisation = responseObject.getString(Constants.ORGANISATION);
                            String user_id = responseObject.getString(Constants.USER_ID);
                            String name = responseObject.getString(Constants.NAME);
                            String type = responseObject.getString(Constants.TYPE);
                            String profile_pic = responseObject.getString(Constants.PROFILE_PIC);
                            String primary_skill = responseObject.getString(Constants.PRIMARY_SKILL);
                            String secondary_skill = responseObject.getString(Constants.SECONDARY_SKILL);
                            String date_of_post = responseObject.getString(Constants.DATE_OF_POST);
                            String apply_status = responseObject.getString(Constants.IS_APPLY);

                            Jobs_ jobs_ = new Jobs_();
                            jobs_.setStatus(1);
                            jobs_.setId(id);
                            jobs_.setTitle(title);
                            jobs_.setPosition(position);
                            jobs_.setDescription(description);
                            jobs_.setOrganisation(organisation);
                            jobs_.setUserId(user_id);
                            jobs_.setName(name);
                            jobs_.setType(type);
                            jobs_.setPic(profile_pic);
                            jobs_.setSkillPrimary(primary_skill);
                            jobs_.setSkillSecondary(secondary_skill);
                            jobs_.setDate(date_of_post);
                            jobs_.setApply(apply_status);
                            feedList.add(jobs_);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return feedList;
    }

    public static int jobsResult(String response) {
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_JOBS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_JOBS);
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

    public static ArrayList<Jobs_> parseSentJobs(String response) {
        ArrayList<Jobs_> jobList = new ArrayList<>();
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_MY_JOBS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_MY_JOBS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        result = responseObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            String id = responseObject.getString(Constants.ID);
                            String title = responseObject.getString(Constants.TITLE);
                            String position = responseObject.getString(Constants.POSITION);
                            String description = responseObject.getString(Constants.DESCRIPTION);
                            String organisation = responseObject.getString(Constants.ORGANISATION);
                            String user_id = Preferences.get(Constants.USER_ID);
                            String name = responseObject.getString(Constants.NAME);
                            String type = responseObject.getString(Constants.TYPE);
                            String profile_pic = responseObject.getString(Constants.PROFILE_PIC);
                            String primary_skill = responseObject.getString(Constants.PRIMARY_SKILL);
                            String secondary_skill = responseObject.getString(Constants.SECONDARY_SKILL);
                            String date_of_post = responseObject.getString(Constants.DATE_OF_POST);
                            String application_count = responseObject.getString(Constants.TOTAL_APPLICATIONS);

                            Jobs_ jobs_ = new Jobs_();
                            jobs_.setStatus(1);
                            jobs_.setId(id);
                            jobs_.setTitle(title);
                            jobs_.setPosition(position);
                            jobs_.setDescription(description);
                            jobs_.setOrganisation(organisation);
                            jobs_.setUserId(user_id);
                            jobs_.setName(name);
                            jobs_.setType(type);
                            jobs_.setPic(profile_pic);
                            jobs_.setSkillPrimary(primary_skill);
                            jobs_.setSkillSecondary(secondary_skill);
                            jobs_.setDate(date_of_post);
                            jobs_.setApply(application_count);
                            jobList.add(jobs_);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobList;
    }


    public static int jobsSentResult(String response) {
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.GET_MY_JOBS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.GET_MY_JOBS);
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


    public static int jobsApplicantResult(String response) {
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.APPLICANT_LIST)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.APPLICANT_LIST);
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

    public static ArrayList<Applicant_> parseApplicant(String response) {
        ArrayList<Applicant_> applicantList = new ArrayList<>();
        //network error
        int result = 11;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.APPLICANT_LIST)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.APPLICANT_LIST);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        result = responseObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            String application_id = responseObject.getString(Constants.APPLICATION_ID);
                            String applicant_id = responseObject.getString(Constants.APPLICANT_ID);
                            String date_of_apply = responseObject.getString(Constants.DATE_OF_APPLY);
                            String name = responseObject.getString(Constants.NAME);
                            String type = responseObject.getString(Constants.TYPE);
                            String profile_pic = responseObject.getString(Constants.PROFILE_PIC);
                            String primary_skill = responseObject.getString(Constants.PRIMARY_SKILL);

                            Applicant_ applicant_ = new Applicant_();
                            applicant_.setStatus(1);
                            applicant_.setApplicantId(applicant_id);
                            applicant_.setApplicationId(application_id);
                            applicant_.setName(name);
                            applicant_.setType(type);
                            applicant_.setPic(profile_pic);
                            applicant_.setSkillPrimary(primary_skill);
                            applicant_.setDate(date_of_apply);
                            applicantList.add(applicant_);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return applicantList;
    }
}
