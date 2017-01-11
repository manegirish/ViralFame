package com.technoindians.message;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.squareup.picasso.Picasso;
import com.technoindians.adapter.ReplyListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.library.ShowLoader;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Messages_;
import com.technoindians.peoples.UserPortfolioActivity;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.CircleTransform;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import technoindians.key.emoji.SmileyKeyBoard;
import technoindians.key.emoji.adapter.EmojiGridviewImageAdapter;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 20/07/2016
 *         Last modified 15/08/2016
 */

public class MessageDetailsActivity extends AppCompatActivity implements View.OnClickListener,
        EmojiGridviewImageAdapter.EmojiClickInterface {

    private static final String TAG = MessageDetailsActivity.class.getSimpleName();

    private ImageView backButton, profilePhoto;
    private TextView nameText, skillText, warningText;
    private EditText messageBox;
    private ImageView sendButton, smileyButton;
    private ListView listView;
    private String name, skill, profile_pic,friend_id;

    private ShowLoader showLoader;
    private ArrayList<Details_> replyList;
    private RelativeLayout chatFooter;

    private ReplyListAdapter replyListAdapter;
    private SmileyKeyBoard smiliKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.message_details_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        showLoader = new ShowLoader(MessageDetailsActivity.this);
        replyList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Constants.USER_ID)) {

                friend_id = intent.getStringExtra(Constants.USER_ID);
                name = intent.getStringExtra(Constants.RESPOND_BY);
                skill = intent.getStringExtra(Constants.SKILL);
                profile_pic = intent.getStringExtra(Constants.PROFILE_PIC);

            } else {
                onBackPressed();
            }
        }

        backButton = (ImageView) findViewById(R.id.activity_toolbar_photo_back);
        backButton.setOnClickListener(this);

        nameText = (TextView) findViewById(R.id.activity_toolbar_photo_name);
        skillText = (TextView) findViewById(R.id.activity_toolbar_photo_skill);
        profilePhoto = (ImageView) findViewById(R.id.activity_toolbar_photo);
        profilePhoto.setOnClickListener(this);

        sendButton = (ImageView) findViewById(R.id.message_details_send);
        sendButton.setOnClickListener(this);

        smileyButton = (ImageView) findViewById(R.id.message_details_smiley);
        smileyButton.setOnClickListener(this);

        warningText = (TextView) findViewById(R.id.message_details_list_warning);

        messageBox = (EditText) findViewById(R.id.message_details_message_box);
        listView = (ListView) findViewById(R.id.message_details_list_view);
        listView.setOnItemClickListener(onItemClickListener);

        replyListAdapter = new ReplyListAdapter(MessageDetailsActivity.this, replyList);
        listView.setAdapter(replyListAdapter);

        smiliKeyBoard = new SmileyKeyBoard();
        smiliKeyBoard.enable(this, this, R.id.reply_message_footer_for_emoticons, messageBox);
        chatFooter = (RelativeLayout) findViewById(R.id.message_details_bottom_layout);
        smiliKeyBoard.checkKeyboardHeight(chatFooter);
        smiliKeyBoard.enableFooterView(messageBox);

        setData();
    }

    private void setData() {
        nameText.setText(name);
        skillText.setText(skill);
        Picasso.with(getApplicationContext())
                .load(Urls.DOMAIN + profile_pic)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_avtar)
                .error(R.drawable.ic_avtar)
                .into(profilePhoto);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    @Override
    public void getClickedEmoji(int gridviewItemPosition) {
        smiliKeyBoard.getClickedEmoji(gridviewItemPosition);
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
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
        Log.e("onStart() ", "replyList" + replyList + " message_id -> " + friend_id);
        if (replyList.size() <= 0) {
            if (friend_id != null && !friend_id.equalsIgnoreCase("0")) {
                new GetDetails().execute();
            } else {
                onBackPressed();
            }
        }
    }

/*
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ID,message_id);
        outState.putSerializable(JsonArrays_.MESSAGE_DETAILS,replyList);
    }
*/

    private class GetDetails extends AsyncTask<Void, Void, Integer> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.sendLoadingDialog();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.TO_ID, friend_id)
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.ACTION, Actions_.GET_MESSAGE_DETAILS)
                    .build();
            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.MESSAGE_OPERATIONS, requestBody, TAG);
                result = Messages_.detailsResult(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.e("onPostExecute", "res -> " + integer);
            showLoader.dismissLoadingDialog();
            switch (integer) {
                case 0:
                    setWarning(Warnings.NO_DATA, R.drawable.ic_no_data);
                    break;
                case 1:
                    if (response != null)
                        new LoadDetails().execute(response);
                    break;
                case 2:
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

    private class LoadDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (replyListAdapter!=null){
                replyListAdapter.clear();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<Details_> list = Messages_.parseDetails(params[0]);
            list.addAll(replyList);
            replyList.clear();
            replyList.addAll(list);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (replyList != null && replyList.size() > 0) {
                listView.setVisibility(View.VISIBLE);
                warningText.setVisibility(View.GONE);

                replyListAdapter.notifyDataSetChanged();
                listView.setSelection(replyListAdapter.getCount());
            }
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
                String response = MakeCall.post(Urls.DOMAIN + Urls.MESSAGE_OPERATIONS, requestBody, TAG);
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
                    ShowToast.toast(getApplicationContext(), "Message Sent");
                    new GetDetails().execute();
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
            case R.id.activity_toolbar_photo_back:
                SmileyKeyBoard.dismissKeyboard();
                onBackPressed();
                break;
            case R.id.message_details_send:
                hideKeyboard();
                SmileyKeyBoard.dismissKeyboard();
                String message = messageBox.getText().toString().trim();
                if (isValidMessage(message) == 1) {
                    new SendMessage().execute(message);
                }
                break;
            case R.id.message_details_smiley:
                smiliKeyBoard.showKeyboard(chatFooter);
                break;
            case R.id.activity_toolbar_photo:
                Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                        (getApplicationContext(), R.anim.animation_one,R.anim.animation_two).toBundle();
                Intent profileIntent = new Intent(getApplicationContext(), UserPortfolioActivity.class);
                profileIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                profileIntent.putExtra(Constants.USER_ID, friend_id);
                profileIntent.putExtra(Constants.PROFILE_PIC, profile_pic);
                profileIntent.putExtra(Constants.NAME, name);
                profileIntent.putExtra(Constants.SKILL, skill);
                startActivity(profileIntent,nextAnimation);
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

    public void addMessage(Details_ newMessage) {
        boolean duplicate = false;
        for (Details_ message : replyList) {
            if (message.getId().equals(newMessage.getId())) {
                duplicate = true;
                break;
            }
        }
        if (!duplicate) {
            replyList.add(newMessage);
            replyListAdapter.notifyDataSetChanged();
            listView.setSelection(replyListAdapter.getCount() - 1);
        }

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
