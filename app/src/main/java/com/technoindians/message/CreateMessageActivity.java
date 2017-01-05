
/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.technoindians.message;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.UsersListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Heading;
import com.technoindians.constants.Warnings;
import com.technoindians.library.ShowLoader;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Users_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import technoindians.key.emoji.SmileyKeyBoard;
import technoindians.key.emoji.adapter.EmojiGridviewImageAdapter;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 20/07/2016
 *         Last modified 27/10/2016
 */

public class CreateMessageActivity extends AppCompatActivity implements View.OnClickListener,
        EmojiGridviewImageAdapter.EmojiClickInterface {

    ImageView searchButton, backButton;
    TextView titleText, warningText, nameText;
    EditText searchBox, messageBox;
    ImageView sendButton, smileyButton;
    ListView listView;
    LinearLayout footerLayout;
    private boolean openSearch = false;
    private boolean openFooter = false;
    private ArrayList<Friends_> friendList;
    private String friend_id;
    private RelativeLayout chatFooter;
    private static boolean hidden = true;

    private UsersListAdapter usersListAdapter;
    private ShowLoader showLoader;
    private SmileyKeyBoard smiliKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_message_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        friendList = new ArrayList<>();

        showLoader = new ShowLoader(CreateMessageActivity.this);

        searchButton = (ImageView) findViewById(R.id.activity_toolbar_search_button);
        searchButton.setOnClickListener(this);

        backButton = (ImageView) findViewById(R.id.activity_toolbar_search_back);
        backButton.setOnClickListener(this);

        searchBox = (EditText) findViewById(R.id.activity_toolbar_search_box);
        searchBox.setVisibility(View.GONE);

        titleText = (TextView) findViewById(R.id.activity_toolbar_search_title);
        titleText.setText(Heading.SEND_MESSAGE_HEADING);
        titleText.setVisibility(View.VISIBLE);

        sendButton = (ImageView) findViewById(R.id.create_message_send);
        sendButton.setOnClickListener(this);
        smileyButton = (ImageView) findViewById(R.id.create_message_smiley);
        smileyButton.setOnClickListener(this);

        warningText = (TextView) findViewById(R.id.create_message_list_warning);

        messageBox = (EditText) findViewById(R.id.create_message_box);
        listView = (ListView) findViewById(R.id.create_message_list_view);
        listView.setOnItemClickListener(onItemClickListener);

        footerLayout = (LinearLayout) findViewById(R.id.create_message_footer);
        footerLayout.setVisibility(View.GONE);

        nameText = (TextView) findViewById(R.id.create_message_name);
        nameText.setText("Unknown");

        setWarning(Warnings.LOADING, R.drawable.ic_data);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                String text = searchBox.getText().toString().toLowerCase(Locale.getDefault());
                if (usersListAdapter != null) {
                    usersListAdapter.filter(text);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        smiliKeyBoard = new SmileyKeyBoard();
        smiliKeyBoard.enable(this, this, R.id.message_footer_for_emoticons, messageBox);
        chatFooter = (RelativeLayout) findViewById(R.id.create_message_bottom_layout);
        smiliKeyBoard.checkKeyboardHeight(chatFooter);
        smiliKeyBoard.enableFooterView(messageBox);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (usersListAdapter.getItem(position).getUser_id() != null) {
                openFooter = true;
                footerLayout.setVisibility(View.VISIBLE);
                nameText.setText(friendList.get(position).getName());
                friend_id = friendList.get(position).getUser_id();
            }
        }
    };

    @Override
    public void onBackPressed() {
        hideKeyboard();
        if (openFooter == true) {
            openFooter = false;
            footerLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        }
    }

    private void toggleSearch() {
        if (openSearch == false) {
            openSearch = true;
            searchBox.setVisibility(View.VISIBLE);
            titleText.setVisibility(View.GONE);
        } else {
            openSearch = false;
            searchBox.setText("");
            hideKeyboard();
            searchBox.setVisibility(View.GONE);
            titleText.setVisibility(View.VISIBLE);
        }
    }

    protected void setWarning(String message, int image) {
        listView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetFriends().execute();
    }

    @Override
    public void getClickedEmoji(int gridviewItemPosition) {
        smiliKeyBoard.getClickedEmoji(gridviewItemPosition);
    }

    private class GetFriends extends AsyncTask<Void, Void, Integer> {
        String response;

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
                    .add(Constants.ACTION, Actions_.GET_USERS)
                    .build();
            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.MESSAGE_OPERATIONS, requestBody);
                result = Users_.usersResult(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer!=1){
                showLoader.dismissLoadingDialog();
            }
            switch (integer) {
                case 0:
                    setWarning(Warnings.FRIEND_UNAVAILABLE, R.drawable.ic_no_data);
                    break;
                case 1:
                    if (response != null)
                        new LoadList().execute(response);
                    break;
                case 2:
                    setWarning(Warnings.FRIEND_UNAVAILABLE, R.drawable.ic_no_data);
                    break;
                case 11:
                    setWarning(Warnings.NETWORK_ERROR_WARNING, R.drawable.ic_network_problem);
                    break;
                case 12:
                    setWarning(Warnings.INTERNAL_ERROR_WARNING, R.drawable.ic_network_problem);
                    break;
            }
        }
    }

    private class LoadList extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            friendList = Users_.parseUsers(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (friendList == null || friendList.size() <= 0) {
                setWarning(Warnings.FRIEND_UNAVAILABLE, R.drawable.ic_no_data);
            } else {
                listView.setVisibility(View.VISIBLE);
                warningText.setVisibility(View.GONE);
                usersListAdapter = new UsersListAdapter(CreateMessageActivity.this, friendList);
                listView.setAdapter(usersListAdapter);
            }
            showLoader.dismissLoadingDialog();
        }
    }

    private class SendMessage extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.sendSendingDialog();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TO_USER_ID, friend_id)
                    .add(Constants.MSG, params[0])
                    .add(Constants.ACTION, Actions_.SEND_MESSAGE)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.MESSAGE_OPERATIONS, requestBody);
                if (response == null) {
                    result = 12;
                } else {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.SEND_MESSAGE)) {
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.SEND_MESSAGE);
                        return responseObject.getInt(Constants.STATUS);
                    } else {
                        return 11;
                    }
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
            switch (integer) {
                case 0:
                    ShowToast.actionFailed(getApplicationContext());
                    break;
                case 1:
                    messageBox.setText("");
                    friend_id = null;
                    nameText.setText("");
                    footerLayout.setVisibility(View.GONE);
                    openFooter = false;
                    ShowToast.toast(getApplicationContext(), "Message Sent");
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
            showLoader.dismissSendingDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_toolbar_search_button:
                toggleSearch();
                break;
            case R.id.activity_toolbar_search_back:
                hideKeyboard();
                smiliKeyBoard.dismissKeyboard();
                if (openSearch == true) {
                    toggleSearch();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.create_message_send:
                hideKeyboard();
                smiliKeyBoard.dismissKeyboard();
                String message = messageBox.getText().toString().trim();
                if (friend_id != null) {
                    if (isValidMessage(message) == 1) {
                        new SendMessage().execute(message);
                    }
                }
                break;
            case R.id.create_message_smiley:
                smiliKeyBoard.showKeyboard(chatFooter);
                break;
        }
    }

    private int isValidMessage(String message) {
        if (message == null || message.length() <= 0) {
            messageBox.setError(Warnings.INVALID_DATA);
            return 0;
        }
        if (message.length() > 250) {
            messageBox.setError(Warnings.MESSAGE_MAXIMUM_CHAR);
            return 0;
        }
        return 1;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
