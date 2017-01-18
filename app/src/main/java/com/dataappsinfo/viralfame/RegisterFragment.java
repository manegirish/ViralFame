
/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.dataappsinfo.viralfame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.database.RetrieveOperation;
import com.technoindians.database.TableList;
import com.technoindians.library.Age;
import com.technoindians.library.AgeCalculator;
import com.technoindians.library.SkillSet;
import com.technoindians.network.CheckInternet;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.SkillParser;
import com.technoindians.pops.ShowSnack;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.validation.LoginValidation_;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * @author Girish M
 *         Created on 21/6/16.
 *         Last modified 01/08/2016
 */

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    ImageView closeButton;
    Button registerButton;
    EditText firstBox, lastBox, emailBox, numberBox, cityBox, passwordBox, confirmBox;
    AutoCompleteTextView skillsBox;
    TextView ageText;
    Spinner genderSpinner;
    ArrayList<HashMap<String, String>> skillArray;
    ArrayList<String> skill;
    Calendar c;
    private CheckedTextView artist, recruiter;
    private String gender, age;
    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0) {
                gender = parent.getItemAtPosition(position).toString();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            gender = null;
        }
    };
    private String skills = null;
    private int skill_id = -1;
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            skills = (String) parent.getItemAtPosition(position);
            Log.e("skills", "-> " + skills);
            String selection = (String) parent.getItemAtPosition(position);
            skill_id = -1;

            for (int i = 0; i < skill.size(); i++) {
                if (skill.get(i).equals(selection)) {
                    Log.e("skill i -> " + i, "skill.size() -> " + skill.size() + " skillArray.size() -> " + skillArray.size());
                    skill_id = Integer.parseInt(skillArray.get(i).get(Constants.ID));
                    Log.e("skill_id", "-> " + skill_id);
                    break;
                }
            }
        }
    };
    private int sYear, sMonth, sDay;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private TextInputLayout inputLayoutFirstName, inputLayoutLastName,
            inputLayoutEmail, inputLayoutPass, inputLayoutConfirmPass, inputLayoutCity;
    private RetrieveOperation retrieveOperation;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration_layout, null);

        activity = getActivity();

        skillArray = new ArrayList<>();
        skill = new ArrayList<>();

        retrieveOperation = new RetrieveOperation(activity.getApplicationContext());

        closeButton = (ImageView) view.findViewById(R.id.registration_close);
        closeButton.setOnClickListener(this);

        registerButton = (Button) view.findViewById(R.id.registration_button);
        registerButton.setOnClickListener(this);

        inputLayoutFirstName = (TextInputLayout) view.findViewById(R.id.registration_layout_first_name);
        inputLayoutCity = (TextInputLayout) view.findViewById(R.id.registration_layout_city);
        inputLayoutLastName = (TextInputLayout) view.findViewById(R.id.registration_layout_last_name);
        inputLayoutEmail = (TextInputLayout) view.findViewById(R.id.registration_layout_email);
        inputLayoutPass = (TextInputLayout) view.findViewById(R.id.registration_layout_password);
        inputLayoutConfirmPass = (TextInputLayout) view.findViewById(R.id.registration_layout_confirm_password);

        firstBox = (EditText) view.findViewById(R.id.registration_first_name);
        lastBox = (EditText) view.findViewById(R.id.registration_last_name);
        emailBox = (EditText) view.findViewById(R.id.registration_email);
        numberBox = (EditText) view.findViewById(R.id.registration_number);
        cityBox = (EditText) view.findViewById(R.id.registration_city);
        skillsBox = (AutoCompleteTextView) view.findViewById(R.id.registration_skills);
        passwordBox = (EditText) view.findViewById(R.id.registration_password);
        confirmBox = (EditText) view.findViewById(R.id.registration_confirm_password);

        artist = (CheckedTextView) view.findViewById(R.id.registration_artist_checkbox);
        artist.setOnClickListener(this);
        recruiter = (CheckedTextView) view.findViewById(R.id.registration_recruiter_checkbox);
        recruiter.setOnClickListener(this);

        genderSpinner = (Spinner) view.findViewById(R.id.registration_gender);
        genderSpinner.setOnItemSelectedListener(onItemSelectedListener);

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

            }
        });

        ageText = (TextView) view.findViewById(R.id.registration_age);
        ageText.setOnClickListener(this);

        c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        return view;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void removeFragment(int i) {
        Log.e(TAG, "position: " + i);
        getFragmentManager().popBackStack();
    }

    private void loginCall(String f_name, String l_name, String email, String number, String city,
                           String password, String confirm_password) {
        if (!artist.isChecked() && !recruiter.isChecked()) {
            ShowToast.toast(activity.getApplicationContext(), "Provide Login Type");
            return;
        }
        if (LoginValidation_.isName(f_name) == 0) {
            inputLayoutFirstName.setError("Invalid First Name");
            requestFocus(firstBox);
            return;
        } else {
            inputLayoutFirstName.setErrorEnabled(false);
        }
        if (LoginValidation_.isName(l_name) == 0) {
            inputLayoutLastName.setError("Invalid Last Name");
            requestFocus(lastBox);
            return;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }
        if (LoginValidation_.isEmail(email) == 0) {
            inputLayoutEmail.setError("Invalid Email");
            requestFocus(emailBox);
            return;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        if (LoginValidation_.isMobile(number) == 0) {
            numberBox.setError("Invalid Number");
            return;
        }
        if (LoginValidation_.isCity(city) == 0) {
            inputLayoutCity.setError("Invalid City");
            requestFocus(cityBox);
            return;
        } else {
            inputLayoutCity.setErrorEnabled(false);
        }
        if (age == null) {
            ShowToast.toast(activity.getApplicationContext(), "Invalid Birth date");
            return;
        }
        if (gender == null || gender.length() <= 0) {
            ShowToast.toast(activity.getApplicationContext(), "Select Gender");
            return;
        }
        if (skills == null || skills.length() <= 0) {
            skillsBox.setError("Select Valid Primary Skill");
            return;
        }
        if (LoginValidation_.isPassword(password) == 0) {
            inputLayoutPass.setError("Invalid Password\n Password should contain following:\n1.Capital letter" +
                    "\n2.Special Character\n3.One Number\n4.Min 6 char and max 10 char");
            requestFocus(passwordBox);
            return;
        } else {
            inputLayoutPass.setErrorEnabled(false);
        }
        if (!password.equals(confirm_password)) {
            inputLayoutConfirmPass.setError("Password not matching");
            requestFocus(confirmBox);
            return;
        } else {
            inputLayoutConfirmPass.setErrorEnabled(false);
        }
        new RegisterCall(f_name, l_name, email, number, city, gender, age, password, confirm_password).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registration_artist_checkbox:
                if (artist.isChecked()) {
                    artist.setChecked(false);
                } else {
                    artist.setChecked(true);
                    recruiter.setChecked(false);
                }
                break;
            case R.id.registration_recruiter_checkbox:
                if (recruiter.isChecked()) {
                    recruiter.setChecked(false);
                } else {
                    recruiter.setChecked(true);
                    artist.setChecked(false);
                }
                break;
            case R.id.registration_close:
                removeFragment(1);
                break;
            case R.id.registration_button:
                String f_name = firstBox.getText().toString().trim();
                String l_name = lastBox.getText().toString().trim();
                String email = emailBox.getText().toString().trim();
                String number = numberBox.getText().toString().trim();
                String city = cityBox.getText().toString().trim();
                String password = passwordBox.getText().toString().trim();
                String confirm_password = confirmBox.getText().toString().trim();
                if (CheckInternet.check()) {
                    loginCall(f_name, l_name, email, number, city, password, confirm_password);
                } else {
                    ShowSnack.noInternet(v);
                }
                break;
            case R.id.registration_age:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                age = sYear + "-" + checkDigit(sMonth) + "-" + checkDigit(sDay);
                                int result = getAge(age);
                                if (result == 1) {
                                    ageText.setText(age);
                                } else if (result == 2) {
                                    ageText.setText("Select DOB");
                                    age = null;
                                    ShowToast.toast(getActivity().getApplicationContext(), "Min age required is 18");
                                } else if (result == 3) {
                                    ageText.setText("Select DOB");
                                    age = null;
                                    ShowToast.toast(getActivity().getApplicationContext(), "Max age allowed is 100");
                                } else if (result == 1) {
                                    ageText.setText("Select DOB");
                                    age = null;
                                    ShowToast.toast(getActivity().getApplicationContext(), "Invalid date of birth");
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
        }
    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @SuppressLint("SimpleDateFormat")
    private String formatDate(String dob) {
        String date = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = inputFormat.parse(dob);
            date = outputFormat.format(d);
        } catch (Exception e) {
            date = null;
        }
        return date;
    }

    private int getAge(String dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = null; //Yeh !! It's my date of birth :-)
        try {
            birthDate = sdf.parse(dob);
            Age age = AgeCalculator.calculateAge(birthDate);
            Log.e("age", " -> " + age);
            if (age.getYears() >= 18 && age.getYears() < 100) {
                return 1;
            } else if (age.getYears() < 18) {
                return 2;
            } else if (age.getYears() > 99) {
                return 3;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    private void setFacebook() {
        firstBox.setText(Preferences.get(Constants.FIRST_NAME));
        lastBox.setText(Preferences.get(Constants.LAST_NAME));
        if (Preferences.contains(Constants.CITY)) {
            cityBox.setText(Preferences.get(Constants.CITY));
        }
        emailBox.setText(Preferences.get(Constants.EMAIL));
        Log.e("RegisterFragment", "" + Preferences.get(Constants.GENDER));
        if (Preferences.get(Constants.GENDER).equalsIgnoreCase("male")) {
            genderSpinner.setSelection(0);
            gender = "Male";
        } else {
            gender = "Female";
            genderSpinner.setSelection(1);
        }
    }


    private void setGoogle() {
        firstBox.setText(Preferences.get(Constants.FIRST_NAME));
        lastBox.setText(Preferences.get(Constants.LAST_NAME));
        emailBox.setText(Preferences.get(Constants.EMAIL));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!CheckInternet.check()) {
            ShowToast.noNetwork(activity.getApplicationContext());
            removeFragment(2);
            return;
        }
        new GetSkills().execute();
        if (Preferences.contains(Constants.FACEBOOK) && Preferences.get(Constants.FACEBOOK) != null) {
            setFacebook();
            return;
        }
        if (Preferences.contains(Constants.GOOGLE) && Preferences.get(Constants.GOOGLE) != null) {
            setGoogle();
            return;
        }

    }

    private class RegisterCall extends AsyncTask<Void, Void, Integer> {

        String f_name, l_name, email, number, city, gender, dob, password, confirm_password, type;

        RegisterCall(String f_name, String l_name, String email, String number,
                     String city, String gender, String dob, String password, String confirm_password) {
            Log.e("RegisterCall", " -> " + dob);
            this.f_name = f_name;
            this.l_name = l_name;
            this.email = email;
            this.number = number;
            this.city = city;
            this.gender = gender;
            this.dob = dob;
            this.password = password;
            this.confirm_password = confirm_password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (artist.isChecked()) {
                type = "1";
            }
            if (recruiter.isChecked()) {
                type = "2";
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (gender.equalsIgnoreCase("Male")) {
                gender = "M";
            } else {
                gender = "F";
            }
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.FIRST_NAME, f_name)
                    .add(Constants.LAST_NAME, l_name)
                    .add(Constants.EMAIL, email)
                    .add(Constants.MOBILE_NO, number)
                    .add(Constants.DOB, formatDate(dob))
                    .add(Constants.GENDER, gender)
                    .add(Constants.PASSWORD, password)
                    .add(Constants.SKILL, "" + skill_id)
                    .add(Constants.TYPE, type)
                    .build();

            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.SIGN_UP_URL, requestBody, TAG);

                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.SIGN_UP)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.SIGN_UP);
                        JSONObject responseObject = jsonArray.getJSONObject(0);
                        return responseObject.getInt(Constants.STATUS);
                    } else {
                        return 10;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 10;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 0:
                    ShowToast.networkProblemToast(getActivity().getApplicationContext());
                    break;
                case 1:
                    ShowToast.toast(getActivity().getApplicationContext(), Warnings.SUCCESSFUL);
                    removeFragment(3);
                    break;
                case 2:
                    ShowToast.toast(getActivity().getApplicationContext(), "Invalid data");
                    break;
                case 4:
                    ShowToast.toast(getActivity().getApplicationContext(), "Number already registered");
                    break;
                case 10:
                    ShowToast.internalErrorToast(getActivity().getApplicationContext());
                    break;
                default:
                    ShowToast.toast(getActivity().getApplicationContext(), "Invalid data");
                    break;
            }
        }
    }

    private class GetSkills extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.ACTION, Actions_.PRIMARY)
                    .build();

            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.GET_SKILLS, requestBody, TAG);
                return SkillParser.skill(response, TableList.TABLE_SKILL_PRIMARY, activity.getApplicationContext());
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
                    registerButton.setEnabled(false);
                    registerButton.setBackgroundResource(R.drawable.disable_rounded_button);
                    break;
                case 1:
                    skillArray = retrieveOperation.getSkill(TableList.TABLE_SKILL_PRIMARY);
                    skill = SkillSet.set(skillArray);

                    registerButton.setEnabled(true);
                    registerButton.setBackgroundResource(R.drawable.primary_rounded_button);

                    ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, skill);
                    skillsBox.setAdapter(adapter);
                    skillsBox.setThreshold(1);
                    break;
                case 2:
                    ShowToast.internalErrorToast(activity.getApplicationContext());

                    registerButton.setEnabled(false);
                    registerButton.setBackgroundResource(R.drawable.disable_rounded_button);
                    break;
                case 11:
                    ShowToast.internalErrorToast(activity.getApplicationContext());

                    registerButton.setEnabled(false);
                    registerButton.setBackgroundResource(R.drawable.disable_rounded_button);
                    break;
                case 12:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    registerButton.setEnabled(false);
                    registerButton.setBackgroundResource(R.drawable.disable_rounded_button);
                    break;
            }
        }
    }
}