package com.technoindians.portfolio;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.FollowListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.library.ShowLoader;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.FollowParser_;
import com.technoindians.peoples.Follow;
import com.technoindians.preferences.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Girish M
 *         Created on 03/08/16.
 *         Last modified 04/08/2016
 */

public class FollowingDialogFragment extends DialogFragment implements View.OnClickListener {

    private boolean openSearch = false;
    private boolean isFetch = false;
    private static final String TAG = FollowingDialogFragment.class.getSimpleName();

    private String user_id;
    private ListView listView;
    private EditText searchBox;
    private TextView warningText,titleText;
    private ArrayList<Follow> followList;
    private ImageView backButton,searchButton;

    private FollowListAdapter followListAdapter;
    private Activity activity;
    private ShowLoader showLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_dialog_toolbar, null);

        activity = getActivity();
        Preferences.initialize(activity.getApplicationContext());

        activity = getActivity();
        showLoader = new ShowLoader(activity);
        followList = new ArrayList<>();

        Bundle data = getArguments();
        if (data!=null&&data.containsKey(Constants.USER_ID)){
            user_id = data.getString(Constants.USER_ID);
            isFetch = true;
        }else {
            user_id = Preferences.get(Constants.USER_ID);
        }

        listView = (ListView) view.findViewById(R.id.layout_list_view);
        warningText = (TextView) view.findViewById(R.id.list_view_layout_warning);
        titleText = (TextView)view.findViewById(R.id.activity_toolbar_search_title);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(activity.getApplicationContext().
                getResources().getString(R.string.following));

        ColorDrawable color = new ColorDrawable(this.getResources().getColor(R.color.black_45));
        listView.setDivider(color);
        listView.setDividerHeight(2);

        backButton = (ImageView)view.findViewById(R.id.activity_toolbar_search_back);
        backButton.setOnClickListener(this);

        searchButton = (ImageView)view.findViewById(R.id.activity_toolbar_search_button);
        searchButton.setOnClickListener(this);

        searchBox = (EditText)view.findViewById(R.id.activity_toolbar_search_box);
        searchBox.setVisibility(View.GONE);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                String text = searchBox.getText().toString().toLowerCase(Locale.getDefault());
                followListAdapter.filter(text);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        if (isFetch){
            new GetData().execute();
        }else {
          //  followList = retrieveOperation.getUsers(1);
            if (followList==null||followList.isEmpty()||followList.size()<=0){
                new GetData().execute();
            }else {
                new GetData().execute();
            }
        }
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_up;
        return dialog;
    }

    protected void setWarning(String message, int image) {
        listView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    private void toggleSearch(){
        if (openSearch==false){
            openSearch = true;
            searchBox.setVisibility(View.VISIBLE);
            titleText.setVisibility(View.GONE);
        }else {
            openSearch = false;
            searchBox.setVisibility(View.GONE);
            titleText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        d.setCancelable(false);
        d.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        event.getAction() == KeyEvent.ACTION_UP &&
                        !event.isCanceled()) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
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
                    dismiss();
                }
                break;
        }
    }
    private class GetData extends AsyncTask<Void, Void, Integer> {
        String response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.sendLoadingDialog();
            setWarning(Warnings.LOADING, R.drawable.ic_data);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.ACTION, Actions_.GET_FOLLOWING)
                    .build();
            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.FOLLOWER_OPERATIONS, requestBody, TAG);
                if (response != null) {
                    return FollowParser_.followResult(response, JsonArrays_.FOLLOWING);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = 11;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);
            switch (res) {
                case 0:
                    showLoader.dismissSendingDialog();
                    setWarning(Warnings.NO_DATA, R.drawable.ic_no_data);
                    break;
                case 1:
                    new LoadData().execute(response);
                    break;
                case 2:
                    showLoader.dismissSendingDialog();
                    setWarning(Warnings.NO_DATA, R.drawable.ic_no_data);
                    break;
                case 11:
                    showLoader.dismissSendingDialog();
                    setWarning(Warnings.INTERNAL_ERROR_WARNING, R.drawable.ic_network_problem);
                    break;
                case 12:
                    showLoader.dismissSendingDialog();
                    setWarning(Warnings.NETWORK_ERROR_WARNING, R.drawable.ic_network_problem);
                    break;
            }
        }
    }

    private class LoadData extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            followList = FollowParser_.parse(params[0],JsonArrays_.FOLLOWING,activity.getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (followList==null||followList.size()<=0){
                setWarning(Warnings.NO_DATA,R.drawable.ic_no_data);
            }else {
                listView.setVisibility(View.VISIBLE);
                warningText.setVisibility(View.GONE);

                followListAdapter = new FollowListAdapter(activity,followList);
                listView.setAdapter(followListAdapter);
            }
            showLoader.dismissSendingDialog();
        }
    }
}
