package com.technoindians.opportunities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.database.RetrieveOperation;
import com.technoindians.database.TableList;
import com.technoindians.library.ShowLoader;
import com.technoindians.library.SkillSet;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 18/07/2016
 * Last modified 25/07/2016
 *
 */

public class OpportunityPostActivity extends Activity implements View.OnClickListener{

    private static final String TAG = OpportunityPostActivity.class.getSimpleName();

    EditText titleBox,postBox,companyBox,descriptionBox;
    AutoCompleteTextView skillPrimary;
    MultiAutoCompleteTextView skillSecondary;
    TextView postButton;
    private String primary_skill = null;
    private String primary_skill_id = null;
    private ArrayList<String> secondary_skill = null;
    private ArrayList<String> secondary_skill_id = null;
    ShowLoader showLoader;
    ImageView backButton;
    RetrieveOperation retrieveOperation;
    ArrayList<HashMap<String,String>> skillArray;
    ArrayList<String> skill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_job_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        secondary_skill = new ArrayList<>();
        secondary_skill_id = new ArrayList<>();

        retrieveOperation = new RetrieveOperation(getApplicationContext());
        showLoader = new ShowLoader(OpportunityPostActivity.this);

        skillArray = retrieveOperation.getSkill(TableList.TABLE_SKILL_PRIMARY);
        skill = SkillSet.set(skillArray);

        titleBox = (EditText)findViewById(R.id.create_job_title);
        postBox = (EditText)findViewById(R.id.create_job_position);
        companyBox = (EditText)findViewById(R.id.create_job_company);
        descriptionBox = (EditText)findViewById(R.id.create_job_description);

        backButton = (ImageView)findViewById(R.id.create_job_back);
        backButton.setOnClickListener(this);

        skillPrimary = (AutoCompleteTextView)findViewById(R.id.create_job_primary_skill);
        skillSecondary = (MultiAutoCompleteTextView)findViewById(R.id.create_job_secondary_skill);

        postButton = (TextView)findViewById(R.id.create_job_button);
        postButton.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.job_primary_skill_item, skill);
        skillPrimary.setAdapter(adapter);
        skillPrimary.setThreshold(1);
        skillPrimary.setOnItemClickListener(onItemClickListener);
        skillPrimary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                primary_skill = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        skillSecondary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()<=0){
                    secondary_skill_id.clear();
                    secondary_skill.clear();
                }
            }
        });
        skillSecondary.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
/*        ArrayAdapter<String> adapterSecondary = new ArrayAdapter<>(getApplicationContext(),
                R.layout.job_primary_skill_item, SkillSet.setSecondary());
            skillSecondary.setAdapter(adapterSecondary);
            skillSecondary.setThreshold(1);
            skillSecondary.setOnItemClickListener(onSecondaryItemClickListener);*/
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            primary_skill = (String) parent.getItemAtPosition(position);
            Log.e("primary_skill","-> "+primary_skill);
            String selection = (String) parent.getItemAtPosition(position);
            primary_skill_id = ""+(-1);

            for (int i = 0; i < skill.size(); i++) {
                if (skill.get(i).equals(selection)) {
                    primary_skill_id = skillArray.get(i).get(Constants.ID);
                    break;
                }
            }
        }
    };

    AdapterView.OnItemClickListener onSecondaryItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String skills = (String) parent.getItemAtPosition(position);
            Log.e("secondary_skill","-> "+skills);
            secondary_skill.add(skills);
            String selection = (String) parent.getItemAtPosition(position);
            int skill_id = -1;

            for (int i = 0; i < skill.size(); i++) {
                if (skill.get(i).equals(selection)) {
                    skill_id = Integer.parseInt(skillArray.get(i).get(Constants.ID));
                    secondary_skill_id.add(""+skill_id);
                    break;
                }
            }
            Log.e("secondary_skill"," -> "+secondary_skill+" \n secondary_skill_id -> "+secondary_skill_id);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_job_button:
                    postJob(2);
                break;
            case R.id.create_job_back:
                onBackPressed();
                break;
        }
    }

    private void postJob(int skip){
        String title = titleBox.getText().toString().trim();
        String position = postBox.getText().toString().trim();
        String company = companyBox.getText().toString().trim();
        String description = descriptionBox.getText().toString().trim();

        if (isValid(title,position, company, description,skip)==1){
            PostJob postJob = new PostJob(title,position,company,
                    description, primary_skill_id, secondary_skill_id.toString());
            postJob.execute();
        }
    }

    private int isValid(String title,String position,String company,String description,int skip){
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        if (title==null){
            titleBox.setError("Invalid Title");
            return 2;
        }
        Matcher titleMatcher = pattern.matcher(position);
        if (!titleMatcher.matches()){
            titleBox.setError("Special Character not allowed");
            return 2;
        }
        if (title.length()<=6||title.length()>150){
            titleBox.setError("Title min length 6 char \n max length 150 char");
            return 2;
        }

        if (position==null){
            postBox.setError("Invalid Position");
            return 2;
        }
        Matcher positionMatcher = pattern.matcher(position);
        if (!positionMatcher.matches()){
            postBox.setError("Special Character not allowed");
            return 2;
        }
        if (position.length()<=3||position.length()>150){
            postBox.setError("Position min length 3 char \n max length 150 char");
            return 2;
        }

        if (company==null){
            companyBox.setError("Invalid Company name");
            return 2;
        }
        if (company.length()<=3||company.length()>150){
            companyBox.setError("Company name min length 3 char \n max length 150 char");
            return 2;
        }

        if (primary_skill==null||primary_skill_id==null){
            skillPrimary.setError("Invalid Primary Skill");
            return 2;
        }

        if (skip==1){
            return 1;
        }else {
            if (secondary_skill_id==null||secondary_skill==null||
                    secondary_skill_id.size()<=0||secondary_skill.size()<=0){
                //deleteConfirmation();
                return 2;
            }
        }

        if (description==null){
            descriptionBox.setError("Invalid Company name");
            return 2;
        }
        if (description.length()<=6||description.length()>150){
            descriptionBox.setError("Description min length 6 char \n max length 150 char");
            return 2;
        }
        return 1;
    }

    private class PostJob extends AsyncTask<Void, Void, Integer> {

        String title,position,company, description, skill_primary, skill_secondary;

        public PostJob(String title,String position,String company,
                       String description,String skill_primary,String skill_secondary){

            this.title=title;
            this.position=position;
            this.company=company;
            this.description=description;
            this.skill_primary=skill_primary;
            this.skill_secondary=skill_secondary;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ACTION, Actions_.POST_JOBS)
                    .add(Constants.TITLE, title)
                    .add(Constants.POSITION, position)
                    .add(Constants.DESCRIPTION, description)
                    .add(Constants.PRIMARY_SKILL, primary_skill_id)
                    .add(Constants.SECONDARY_SKILL, secondary_skill_id.toString().replaceAll("\\[", "").replaceAll("\\]",""))
                    .add(Constants.ORGANISATION, company)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.JOB_OPERATIONS, requestBody, TAG);
                if (response!=null){
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.POST_JOB)){
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.POST_JOB);
                        result =  responseObject.getInt(Constants.STATUS);
                    }else {
                        result = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = 12;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer){
                case 0:
                    ShowToast.actionFailed(getApplicationContext());
                    break;
                case 1:
                    ShowToast.successful(getApplicationContext());
                    onBackPressed();
                    break;
                case 2:
                    ShowToast.actionFailed(getApplicationContext());
                    break;
                case 11:
                    ShowToast.internalErrorToast(getApplicationContext());
                    break;
                case 12:
                    ShowToast.networkProblemToast(getApplicationContext());
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        secondary_skill.clear();
        secondary_skill_id.clear();
        primary_skill_id=null;
        primary_skill=null;

        titleBox.setText("");
        postBox.setText("");
        companyBox.setText("");
        descriptionBox.setText("");

        super.onBackPressed();
        finish();

        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
    }
}
