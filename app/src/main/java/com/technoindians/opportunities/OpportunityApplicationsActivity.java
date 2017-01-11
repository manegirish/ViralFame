package com.technoindians.opportunities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.ApplicantListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Fragment_;
import com.technoindians.constants.Heading;
import com.technoindians.constants.Warnings;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.JobParser_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 18/07/2016
 * Last modified 25/07/2016
 *
 */

public class OpportunityApplicationsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = OpportunityApplicationsActivity.class.getSimpleName();

    private String job_id = "0",job_title,job_description,job_position,job_primary_skill,job_secondary_skill,job_company;
    private ImageView searchButton,backButton;
    private TextView titleText;
    private EditText searchBox;
    private boolean openSearch = false;
    private ListView listView;
    private TextView warningText;
    private FloatingActionButton openButton;
    private ArrayList<Applicant_> applicantList;
    private ApplicantListAdapter applicantListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_list_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent data = getIntent();
        if (data!=null){
            job_id = data.getStringExtra(Constants.ID);
            job_title = data.getStringExtra(Constants.TITLE);
            job_position = data.getStringExtra(Constants.POSITION);
            job_company = data.getStringExtra(Constants.ORGANISATION);
            job_primary_skill = data.getStringExtra(Constants.PRIMARY_SKILL);
            job_secondary_skill = data.getStringExtra(Constants.SECONDARY_SKILL);
            job_description = data.getStringExtra(Constants.DESCRIPTION);

            Log.e(TAG,"job_title -> "+job_title);
        }

        applicantList = new ArrayList<>();

        warningText = (TextView)findViewById(R.id.liked_list_warning);

        searchButton = (ImageView)findViewById(R.id.activity_toolbar_search_button);
        searchButton.setOnClickListener(this);
        backButton = (ImageView)findViewById(R.id.activity_toolbar_search_back);
        backButton.setOnClickListener(this);

        searchBox = (EditText)findViewById(R.id.activity_toolbar_search_box);
        searchBox.setVisibility(View.GONE);

        titleText = (TextView)findViewById(R.id.activity_toolbar_search_title);
        titleText.setText(Heading.JOB_APPLICATION_HEADING);
        titleText.setVisibility(View.VISIBLE);

        openButton = (FloatingActionButton)findViewById(R.id.liked_list_float);
        openButton.setOnClickListener(this);
        openButton.setVisibility(View.VISIBLE);

        listView = (ListView)findViewById(R.id.list_view);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                String text = searchBox.getText().toString().toLowerCase(Locale.getDefault());
                applicantListAdapter.filter(text);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!job_id.equalsIgnoreCase("0")){
            new Fetch().execute(job_id);
        }else {
            onBackPressed();
        }
    }

    protected void setWarning(String message,int image){
        listView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    private void toggleSearch(){
        if (openSearch==false){
            openSearch = true;
            searchButton.setImageResource(R.drawable.ic_cancel);
            searchBox.setVisibility(View.VISIBLE);
            titleText.setVisibility(View.GONE);
        }else {
            searchButton.setImageResource(R.drawable.ic_search_white);
            openSearch = false;
            applicantListAdapter.filter("");
            searchBox.setText("");
            searchBox.setVisibility(View.GONE);
            titleText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_toolbar_search_button:
                toggleSearch();
                break;
            case R.id.activity_toolbar_search_back:
                if (openSearch == true){
                    toggleSearch();
                }else {
                    onBackPressed();
                }
                break;
            case R.id.liked_list_float:
                openPostFragment();
                break;
        }
    }

    public void openPostFragment() {
        Fragment f = getFragmentManager().findFragmentByTag(Fragment_.JOBS);
        if (f != null) {
            getFragmentManager().popBackStack();
        } else {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            OpportunityPostFragment opportunityPostFragment = new OpportunityPostFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.ID,job_id);
            bundle.putString(Constants.TITLE, job_title);
            bundle.putString(Constants.DESCRIPTION, job_description);
            bundle.putString(Constants.ORGANISATION, job_company);
            bundle.putString(Constants.PRIMARY_SKILL, job_primary_skill);
            bundle.putString(Constants.SECONDARY_SKILL, job_secondary_skill);
            bundle.putString(Constants.POSITION, job_position);
            opportunityPostFragment.setArguments(bundle);
            ft.add(R.id.liked_list_frame, opportunityPostFragment, Fragment_.JOBS);
            ft.addToBackStack(null);
            ft.commit();
          //  Log.e("OAA","********------> "+OpportunityPostFragment.class.getName());
        }
    }


    @Override
    protected void onDestroy() {
        Fragment f = getFragmentManager().findFragmentByTag(Constants.TOTAL_APPLICATIONS);
        if (f != null) {
            getFragmentManager().popBackStack();
        }
        super.onDestroy();
    }

    private class Fetch extends AsyncTask<String, Void, Integer> {
        String response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setWarning("Loading....",R.drawable.ic_data);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ACTION, Actions_.APPLICANT_LIST)
                    .add(Constants.ID, job_id)
                    .build();

                try {
                    response = MakeCall.post(Urls.DOMAIN + Urls.JOB_OPERATIONS, requestBody, TAG);
                    if (response!=null){
                        result = JobParser_.jobsApplicantResult(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result = 11;
                }

            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer){
                case 0:
                    ShowToast.internalErrorToast(getApplicationContext());
                    break;
                case 1:
                    applicantList = JobParser_.parseApplicant(response);
                    if (applicantList!=null&&applicantList.size()>0){
                        listView.setVisibility(View.VISIBLE);
                        warningText.setVisibility(View.GONE);

                        applicantListAdapter = new ApplicantListAdapter(OpportunityApplicationsActivity.this,applicantList);
                        listView.setAdapter(applicantListAdapter);
                    }else {
                        setWarning("No Application Found",R.drawable.ic_no_data);
                    }
                    break;
                case 2:
                    ShowToast.noData(getApplicationContext());
                    setWarning("No Application Found",R.drawable.ic_no_data);
                    break;
                case 11:
                    ShowToast.internalErrorToast(getApplicationContext());
                    setWarning(Warnings.INTERNAL_ERROR_WARNING,R.drawable.ic_no_data);
                    break;
                case 12:
                    ShowToast.networkProblemToast(getApplicationContext());
                    setWarning(Warnings.NETWORK_ERROR_WARNING,R.drawable.ic_no_data);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
    }
}
