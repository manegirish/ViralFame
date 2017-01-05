package com.technoindians.parser;

import android.content.Context;
import com.technoindians.constants.Constants;
import com.technoindians.database.InsertOperations;
import com.technoindians.network.JsonArrays_;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by girish on 8/8/16.
 */

public class SkillParser {

    private static InsertOperations insertOperations;

    public static int skill(String response,String tableName, Context context) {

        insertOperations = new InsertOperations(context);

        int result = 12;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                if (jsonObject.has(JsonArrays_.SKILL_DATA)) {
                    JSONArray skillArray = jsonObject.getJSONArray(JsonArrays_.SKILL_DATA);
                    for (int i = 0; i < skillArray.length(); i++) {
                        JSONObject skillObject = skillArray.getJSONObject(i);
                        result = 1;
                        String id = skillObject.getString(Constants.ID);
                        String skill = skillObject.getString(Constants.SKILL);

                        insertOperations.insertSkillPrimary(id,skill,tableName);
                    }
                } else {
                    result = 11;
                }
            } else {
                result = 12;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = 11;
        }
        return result;
    }
}
