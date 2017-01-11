package com.technoindians.wall;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.LikedListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Heading;
import com.technoindians.constants.Warnings;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Wall_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Girish M (girishmane8692@gmail.com)
 * Created on 15/7/16
 * Last Modified on 11/07/2017
 */
public class LikedListActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = LikedListActivity.class.getSimpleName();

    ImageView searchButton,backButton;
    private TextView titleText;
    private EditText searchBox;
    private ListView listView;
    private TextView warningText;

    private String wall_id;
    private boolean openSearch = false;
    private ArrayList<Liked_> likedList;

    private LikedListAdapter likedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_list_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        if (intent != null) {
            wall_id = intent.getStringExtra(Constants.WALL_ID);
        }

        likedList = new ArrayList<>();

        warningText = (TextView)findViewById(R.id.liked_list_warning);

        searchButton = (ImageView)findViewById(R.id.activity_toolbar_search_button);
        searchButton.setOnClickListener(this);
        backButton = (ImageView)findViewById(R.id.activity_toolbar_search_back);
        backButton.setOnClickListener(this);

        searchBox = (EditText)findViewById(R.id.activity_toolbar_search_box);
        searchBox.setVisibility(View.GONE);

        titleText = (TextView)findViewById(R.id.activity_toolbar_search_title);
        titleText.setText(Heading.LIKED_LIST_HEADING);
        titleText.setVisibility(View.VISIBLE);

        listView = (ListView)findViewById(R.id.list_view);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                String text = searchBox.getText().toString().toLowerCase(Locale.getDefault());
                likedListAdapter.filter(text);
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

    protected void setWarning(String message,int image){

        listView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    private void toggleSearch(){
        if (!openSearch){
            openSearch = true;
            searchBox.setVisibility(View.VISIBLE);
            titleText.setVisibility(View.GONE);
        }else {
            openSearch = false;
            likedListAdapter.filter("");
            searchBox.setText("");

            searchBox.setVisibility(View.GONE);
            titleText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wall_id!=null)
        new GetLiked().execute(wall_id);
    }

    private class GetLiked extends AsyncTask<String,Void,Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setWarning(Warnings.LOADING,R.drawable.ic_data);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.ACTION, Actions_.GET_LIKED_USERS)
                    .add(Constants.ID, params[0])
                    .build();

            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL, requestBody,TAG);
                result = Wall_.likedResult(response);
                if (result==1){
                    likedList = Wall_.parseLiked(response);
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
            //Log.e("onPost","result -> "+integer+" size -> "+likedList.size());
            switch (integer){
                case 0:
                    setWarning("",R.drawable.ic_sad);
                    break;
                case 1:
                    warningText.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);

                    likedListAdapter = new LikedListAdapter(LikedListActivity.this,likedList);
                    listView.setAdapter(likedListAdapter);
                    break;
                case 2:
                    setWarning("",R.drawable.ic_sad);
                    break;
                case 11:
                    setWarning(Warnings.NETWORK_ERROR_WARNING,R.drawable.ic_network_problem);
                    ShowToast.networkProblemToast(getApplicationContext());
                    break;
                case 12:
                    setWarning(Warnings.INTERNAL_ERROR_WARNING,R.drawable.ic_network_problem);
                    ShowToast.internalErrorToast(getApplicationContext());
                    break;
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_toolbar_search_button:
                toggleSearch();
                break;
            case R.id.activity_toolbar_search_back:
                if (openSearch){
                    toggleSearch();
                }else {
                    onBackPressed();
                }
                break;
        }
    }
}
