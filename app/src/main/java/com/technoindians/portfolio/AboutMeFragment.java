package com.technoindians.portfolio;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.DefaultMeListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.database.UpdateOperations;
import com.technoindians.library.ShowLoader;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Girish M
 *         Created on 03/08/16.
 *         Last modified 04/08/2016
 */

public class AboutMeFragment extends DialogFragment implements View.OnClickListener {

    private Activity activity;
    private String about_me;
    private EditText aboutBox;
    private TextView saveButton;
    private ListView aboutListView;
    private ImageView backButton;
    private boolean changes = false;
    private DefaultMeListAdapter defaultMeListAdapter;
    private ShowLoader showLoader;
    private UpdateOperations updateOperations;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_me_fragment_layout, null);

        activity = getActivity();
        Preferences.initialize(activity.getApplicationContext());

        showLoader = new ShowLoader(activity);
        updateOperations = new UpdateOperations(activity.getApplicationContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            about_me = bundle.getString(Constants.ABOUT_ME);
        } else {
            dismiss();
            ShowToast.internalErrorToast(activity.getApplicationContext());
        }

        aboutBox = (EditText) view.findViewById(R.id.about_me_status);
        aboutBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        aboutListView = (ListView) view.findViewById(R.id.about_me_default_status_list);
        aboutListView.setOnItemClickListener(onItemClickListener);

        backButton = (ImageView) view.findViewById(R.id.about_me_post_back);
        backButton.setOnClickListener(this);

        saveButton = (TextView) view.findViewById(R.id.about_me_post_save);
        saveButton.setOnClickListener(this);

        return view;
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            aboutBox.setText("");
            aboutBox.setText(DefaultAboutList.get().get(position));
            aboutBox.setSelection(DefaultAboutList.get().get(position).length());
        }
    };

    private void setData() {
        aboutBox.setText(about_me);
        aboutBox.setSelection(about_me.length());
        defaultMeListAdapter = new DefaultMeListAdapter(activity, DefaultAboutList.get());
        aboutListView.setAdapter(defaultMeListAdapter);
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
        setData();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_up;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_me_post_back:
                dismiss();
                break;
            case R.id.about_me_post_save:
                String about = aboutBox.getText().toString().trim();
                int validate = validate(about);
                if (validate == 1) {
                    new SetAboutMe().execute(about);
                }
                break;
        }
    }

    private class SetAboutMe extends AsyncTask<String, Void, Integer> {
        String about;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            changes = false;
            showLoader.showPostingDialog();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 12;
            about = params[0];
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ACTION, Actions_.ADD_ABOUT_US)
                    .add(Constants.MSG, about)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.PORTFOLIO_OPERATIONS, requestBody);

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null) {
                    if (jsonObject.has(JsonArrays_.ADD_ABOUT_US)) {
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.ADD_ABOUT_US);
                        return responseObject.getInt(Constants.STATUS);
                    } else {
                        result = 11;
                    }
                } else {
                    result = 12;
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
            showLoader.dismissPostingDialog();
            switch (integer) {
                case 0:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    break;
                case 1:
                    ShowToast.successful(activity.getApplicationContext());
                    changes = true;
                    about_me = about;
                    updateOperations.updateProfile(Preferences.get(Constants.USER_ID), Constants.ABOUT_ME, about_me);
                    dismiss();
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (changes) {
            PortfolioMainActivity.portfolioMainActivity.setAboutMe(about_me);
        }
    }

    private int validate(String about) {
        if (about == null) {
            aboutBox.setText("Invalid data");
            return 0;
        }
        if (about.equals(about_me)) {
            dismiss();
            return 0;
        }
        if (about.length() <= 20) {
            aboutBox.setError("Min char length is 20");
            return 0;
        }
        if (about.length() >= 250) {
            aboutBox.setError("Max char length is 250");
            return 0;
        }
        return 1;
    }
}
