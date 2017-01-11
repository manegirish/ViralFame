package com.technoindians.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.MainActivity_new;
import com.dataappsinfo.viralfame.R;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.database.RetrieveOperation;
import com.technoindians.database.TableList;
import com.technoindians.library.SkillSet;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Login_;
import com.technoindians.parser.SkillParser;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.SkillAutoCompleteTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by shonali on 10/08/16.
 */
public class PreferencesFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = PreferencesFragment.class.getSimpleName();
    ArrayList<HashMap<String, String>> skillArray;
    ArrayList<String> skill;

    ArrayList<HashMap<String, String>> skillArraySecondary;
    ArrayList<JSONObject> skill_list;
    ArrayList<String> skillSecondary;
    TextView saveButton;
    SwitchCompat switchCompat;

    private String skills = null;
    private int skill_id = -1;

    ArrayList<String> secondary_skill;
    ArrayList<String> secondary_skill_id;

    SkillAutoCompleteTextView secondaryView;
    AutoCompleteTextView skillsBox;

    private RetrieveOperation retrieveOperation;
    ArrayAdapter adapterPrimary, adapterSecondary;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.preferences_fragment_layout, null);

        activity = getActivity();

        skillArray = new ArrayList<>();
        skill = new ArrayList<>();
        skillArraySecondary = new ArrayList<>();
        skillSecondary = new ArrayList<>();
        secondary_skill = new ArrayList<>();
        secondary_skill_id = new ArrayList<>();

        retrieveOperation = new RetrieveOperation(activity.getApplicationContext());

        skillArray = retrieveOperation.getSkill(TableList.TABLE_SKILL_PRIMARY);
        skill = SkillSet.set(skillArray);

        switchCompat = (SwitchCompat) view.findViewById(R.id.preferences_notification_toggle);
        switchCompat.setOnClickListener(this);

        Preferences.initialize(activity.getApplicationContext());
        MainActivity_new.mainActivity.setTitle(activity.getApplicationContext()
                .getResources().getString(R.string.preferences));

        skillsBox = (AutoCompleteTextView) view.findViewById(R.id.preferences_primary_skill);
        skillsBox.setOnItemClickListener(onItemClickListener);
        skillsBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                skills = null;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Preferences.get(Constants.PRIMARY_SKILL).equalsIgnoreCase(s.toString())) {
                    saveButton.setVisibility(View.VISIBLE);
                }
            }
        });

        secondaryView = (SkillAutoCompleteTextView) view.findViewById(R.id.preferences_secondary_skill);
        secondaryView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (skill.isEmpty() || skill == null) {
            new GetSkills().execute(Actions_.PRIMARY);
        }

        adapterPrimary = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, skill);

        skillsBox.setAdapter(adapterPrimary);
        skillsBox.setThreshold(1);

        secondaryView.setAdapter(adapterPrimary);
        secondaryView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        saveButton = (TextView) view.findViewById(R.id.preferences_skill_save_button);
        saveButton.setOnClickListener(this);

        setSkill();
        return view;
    }

    private void setSkill() {

        skills = Preferences.get(Constants.PRIMARY_SKILL);
        skillsBox.setText(skills);

        String response = Preferences.get(Constants.SECONDARY_SKILL);
        ArrayList<HashMap<String, String>> list = Login_.parseSecondarySkills(response);
        ArrayList<String> skill_set = new ArrayList<>();
        if (list != null && list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                skill_set.add(list.get(i).get(Constants.SKILL));
            }
        }
        secondaryView.setText(skill_set.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
        secondaryView.setChips();

        saveButton.setVisibility(View.GONE);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            skills = (String) parent.getItemAtPosition(position);
            String selection = (String) parent.getItemAtPosition(position);
            skill_id = -1;
            getPrimarySkill(selection);
            if (!Preferences.get(Constants.PRIMARY_SKILL).equalsIgnoreCase(selection)) {
                saveButton.setVisibility(View.VISIBLE);
            } else {
                saveButton.setVisibility(View.GONE);
            }
        }
    };

    private void getPrimarySkill(String selection) {
        for (int i = 0; i < skill.size(); i++) {
            if (skill.get(i).equals(selection)) {
                skill_id = Integer.parseInt(skillArray.get(i).get(Constants.ID));
                break;
            }
        }
    }

    private void getSecondarySkills(String secondary_skills) {
        secondary_skill.clear();
        secondary_skill_id.clear();

        if (skill_list!=null){
            skill_list.clear();
        }else {
            skill_list = new ArrayList<>();
        }

        String chips[] = secondary_skills.toString().trim().split(",");

        ArrayList<String> list = new ArrayList<>();
        for (String c : chips) {
            list.add(c);
        }

        for (String skill_name : list) {
            skill_name = skill_name.trim().toLowerCase();
            for (int j = 0; j < skillSecondary.size(); j++) {
                String main_name = skillSecondary.get(j).toLowerCase().trim();
                //Log.e("getSecondarySkills","main_name --> "+main_name+" skill_name -> "+skill_name+" if -> "+main_name.equalsIgnoreCase(skill_name));
                if (main_name.equals(skill_name)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Constants.ID, skillArraySecondary.get(j).get(Constants.ID));
                        jsonObject.put(Constants.SKILL, skill_name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    secondary_skill.add(skill_name);
                    secondary_skill_id.add(skillArraySecondary.get(j).get(Constants.ID));

                    skill_list.add(jsonObject);
                    break;
                }
            }
        }
    }

    private RequestBody updateSkill() {
        if (skills == null || skills.length() <= 0) {
            skills = Preferences.get(Constants.PRIMARY_SKILL);
        }
        getSecondarySkills(secondaryView.getText().toString());
        String second_ids = secondary_skill_id.toString().replaceAll("\\[", "").replaceAll("\\]", "");
        Log.e("save button", "primary_skill -> " + skills + "\nsecondary_id ->" + second_ids);
        if (skills != null && skills.length() > 0) {
            getPrimarySkill(skills);
        } else {
            skillsBox.setError("Select Valid Primary Skill");
            return null;
        }
        if (secondary_skill_id == null || secondary_skill_id.size() <= 0) {
            secondaryView.setError("Select Valid Secondary Skill");
            return null;
        }
        RequestBody requestBody = new FormBody.Builder()
                .add(Constants.ACTION, Actions_.CHANGE_SKILLS)
                .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                .add(Constants.PRIMARY_SKILL, "" + skill_id)
                .add(Constants.SECONDARY_SKILL, "" + second_ids)
                .build();

        return requestBody;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preferences_skill_save_button:
                RequestBody requestBody = updateSkill();
                if (requestBody != null) {
                    new Operation().execute(requestBody);
                }
                break;
            case R.id.preferences_notification_toggle:
                boolean isNotify = switchCompat.isChecked();
                Log.e("isNotify", " -> " + isNotify);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetSkills().execute(Actions_.SECONDARY);
    }

    private class Operation extends AsyncTask<RequestBody, Void, Integer> {
        @Override
        protected Integer doInBackground(RequestBody... params) {
            int result = 12;
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.GET_SKILLS, params[0],TAG);
                // {"skill_data":{"msg":"succesfully updated.","status":1}}
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.SKILL_DATA)) {
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.SKILL_DATA);
                        return responseObject.getInt(Constants.STATUS);
                    } else {
                        result = 11;
                    }
                }
            } catch (Exception e) {
                result = 11;
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case 0:
                    ShowToast.actionFailed(activity.getApplicationContext());
                    break;
                case 1:
                    ShowToast.successful(activity.getApplicationContext());
                    hideKeyboard();
                    saveButton.setVisibility(View.GONE);
                    Preferences.save(Constants.PRIMARY_SKILL, skills);
                    Preferences.save(Constants.SECONDARY_SKILL, skill_list.toString());
                    break;
                case 2:
                    ShowToast.actionFailed(activity.getApplicationContext());
                    break;

                case 11:
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;

                case 12:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    break;
            }
        }
    }

    private class GetSkills extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.ACTION, params[0])
                    .build();

            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.GET_SKILLS, requestBody,TAG);
                if (params[0].equals(Actions_.PRIMARY)) {
                    return SkillParser.skill(response, TableList.TABLE_SKILL_PRIMARY, activity.getApplicationContext());
                } else {
                    return SkillParser.skill(response, TableList.TABLE_SKILL_SECONDARY, activity.getApplicationContext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 12;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case 0:
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;
                case 1:
                    skillArray = retrieveOperation.getSkill(TableList.TABLE_SKILL_PRIMARY);
                    skill = SkillSet.set(skillArray);

                    adapterPrimary = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, skill);
                    skillsBox.setAdapter(adapterPrimary);
                    skillsBox.setThreshold(1);

                    skillArraySecondary = retrieveOperation.getSkill(TableList.TABLE_SKILL_SECONDARY);
                    skillSecondary = SkillSet.set(skillArraySecondary);

                    adapterSecondary = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, skillSecondary);

                    secondaryView.setAdapter(adapterSecondary);
                    secondaryView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                    break;
                case 2:
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;
                case 11:
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;
                case 12:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    break;
            }
        }
    }

    private void hideKeyboard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
