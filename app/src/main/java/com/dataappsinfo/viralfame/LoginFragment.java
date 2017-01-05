
/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.dataappsinfo.viralfame;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.directory.Make_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Login_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.validation.LoginValidation_;
import com.technoindians.views.SlidingRelativeLayout;

import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Girish M
 *         Created on 21/6/16.
 *         Last modified 01/08/2016
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Activity activity;
    private boolean isVisible = false;

    ImageView closeButton;
    TextView showButton, guestButton;
    EditText numberBox, passwordBox;
    Button loginButton;
    SlidingRelativeLayout mainLayout;
    private TextInputLayout emailLayout, passwordLayout;

    private ProgressDialog nDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment_layout, null);

        Preferences.initialize(getActivity().getApplicationContext());
        activity = getActivity();

        closeButton = (ImageView) view.findViewById(R.id.login_fragment_close);
        closeButton.setOnClickListener(this);

        loginButton = (Button) view.findViewById(R.id.login_fragment_button);
        loginButton.setOnClickListener(this);

        emailLayout = (TextInputLayout) view.findViewById(R.id.login_layout_email);
        passwordLayout = (TextInputLayout) view.findViewById(R.id.login_layout_password);

        showButton = (TextView) view.findViewById(R.id.login_fragment_password_toggle);
        showButton.setOnClickListener(this);
        numberBox = (EditText) view.findViewById(R.id.login_fragment_email);
        passwordBox = (EditText) view.findViewById(R.id.login_fragment_password);

        mainLayout = (SlidingRelativeLayout) view.findViewById(R.id.login_fragment_layout);
        mainLayout.setOnClickListener(this);

        guestButton = (TextView) view.findViewById(R.id.login_fragment_guest_button);
        guestButton.setOnClickListener(this);
        return view;
    }

    public void removeFragment() {
        getFragmentManager().popBackStack();
    }

    private int isUsername(String username, String password) {
        if (LoginValidation_.isEmail(username) == 0 && LoginValidation_.isMobile(username) == 0) {
            requestFocus(numberBox);
            return 0;
        } else {
            emailLayout.setErrorEnabled(false);
        }
        if (password == null || password.length() <= 5) {
            passwordLayout.setError("Invalid Password");
            requestFocus(passwordBox);
            return 0;
        } else {
            passwordLayout.setErrorEnabled(false);
        }
        return 1;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_fragment_guest_button:
                deleteConfirmation();
                break;
            case R.id.login_fragment_close:
                removeFragment();
                break;
            case R.id.login_fragment_button:
                String email = numberBox.getText().toString().trim();
                String password = passwordBox.getText().toString();

                if (isUsername(email, password) != 1) {
                    if (LoginValidation_.isEmail(email) == 0) {
                        emailLayout.setError("Invalid email");
                    } else if (isUsername(email, password) == 3) {
                        emailLayout.setError("Invalid number");
                    }
                } else {
                    new LoginCall(email, password, Actions_.LOGIN).execute();
                }
                break;
            case R.id.login_fragment_password_toggle:
                String pass = passwordBox.getText().toString();
                if (isVisible == false) {
                    isVisible = true;
                    showButton.setText("Hide");
                    passwordBox.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    isVisible = false;
                    passwordBox.setInputType(129);
                    showButton.setText("Show");
                }
                if (pass != null || pass.length() > 0) {
                    passwordBox.setSelection(pass.length());
                }
                break;
            case R.id.login_fragment_layout:
                break;
        }
    }

    private void deleteConfirmation() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title, warning;
        Button fwdAccept, fwdDecline;
        title = (TextView) dialog.findViewById(R.id.confirmation_dialog_title);
        title.setText("Guest Login");
        warning = (TextView) dialog.findViewById(R.id.confirmation_dialog_description);
        warning.setText("Guest Login will not have full features enabled!");
        fwdAccept = (Button) dialog.findViewById(R.id.confirmation_dialog_ok_button);
        final EditText numberBox = (EditText)dialog.findViewById(R.id.confirmation_dialog_number_box);

        fwdAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = numberBox.getText().toString().trim();
                if (LoginValidation_.isMobile(number) == 0) {
                    numberBox.setError("Invalid Number");
                    return;
                }
                new LoginCall(number, "", Actions_.GUEST_LOGIN).execute();
                dialog.dismiss();

            }
        });

        fwdDecline = (Button) dialog.findViewById(R.id.confirmation_dialog_cancel_button);
        fwdDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void nextActivity() {
        Intent next = new Intent(activity, MainActivity_new.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(next);
        getActivity().finish();
    }

    private class LoginCall extends AsyncTask<Void, Void, Integer> {

        String email, password, action;

        public LoginCall(String email, String password, String action) {
            this.email = email;
            this.password = password;
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(getActivity()); //Here I get an error: The constructor ProgressDialog(PFragment) is undefined
            nDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(false);
            nDialog.setCanceledOnTouchOutside(false);
            nDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.EMAIL, email)
                    .add(Constants.PASSWORD, password)
                    .add(Constants.ACTION, action)
                    .build();

            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.LOGIN_URL, requestBody);
                return Login_.parse(response, activity.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
                return 12;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 0:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    break;
                case 1:
                    Make_.makeDirectories();
                    ShowToast.toast(activity.getApplicationContext(), Warnings.SUCCESSFUL);
                    nextActivity();
                    Preferences.save(Constants.TIMEZONE, getTimeZone());
                    break;
                case 2:
                    ShowToast.toast(activity.getApplicationContext(), "Invalid data");
                    break;
                case 3:
                    ShowToast.toast(activity.getApplicationContext(), "Mobile number already used");
                    break;
                case 11:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    break;
                case 12:
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;
                default:
                    ShowToast.toast(activity.getApplicationContext(), "Invalid data");
                    break;
            }
            if (nDialog != null && nDialog.isShowing()) {
                nDialog.dismiss();
            }
        }
    }

    protected static String getTimeZone() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        String name = tz.getID();
        return name;
    }
}
