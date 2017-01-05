package com.technoindians.message;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.MainActivity_new;
import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.MessageListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.database.RetrieveOperation;
import com.technoindians.library.CheckUserType;
import com.technoindians.library.ShowLoader;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Messages_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 01-07-2016
 *         Last Modified on 23-08-2016
 */

public class MessageListFragment extends Fragment implements View.OnClickListener {

    ArrayList<Message_> messageList;
    ListView listView;
    TextView warningText;
    FloatingActionButton floatingActionButton;

    private Activity activity;
    private MessageListAdapter messageListAdapter;
    private ShowLoader showLoader;
    private RetrieveOperation retrieveOperation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_list_layout, null);

        activity = getActivity();

        showLoader = new ShowLoader(activity);
        retrieveOperation = new RetrieveOperation(activity.getApplicationContext());

        MainActivity_new.mainActivity.setTitle(activity.getApplicationContext()
                .getResources().getString(R.string.messages));
        Preferences.initialize(activity.getApplicationContext());

        messageList = new ArrayList<>();

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.message_list_create_button);
        floatingActionButton.setOnClickListener(this);

        warningText = (TextView) view.findViewById(R.id.message_list_warning);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(onItemClickListener);

        MainActivity_new.mainActivity.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = MainActivity_new.mainActivity.searchBox.getText().toString().toLowerCase(Locale.getDefault());
                if (messageListAdapter != null) {
                    messageListAdapter.filterMessage(text);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        messageList = retrieveOperation.getMessage("10");
        if (messageList != null && messageList.size() > 0) {
            listView.setVisibility(View.VISIBLE);
            warningText.setVisibility(View.GONE);
        } else {
            setWarning(Warnings.LOADING, R.drawable.ic_data);
        }
        messageListAdapter = new MessageListAdapter(activity, messageList);
        listView.setAdapter(messageListAdapter);

        return view;
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                    (activity.getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();

            if (messageListAdapter.getItem(position).getId() != null) {
                MainActivity_new.mainActivity.closeSearch();

                Intent detailsIntent = new Intent(activity.getApplicationContext(), MessageDetailsActivity.class);
                detailsIntent.setFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
                detailsIntent.putExtra(Constants.USER_ID, messageList.get(position).getUserId());
                detailsIntent.putExtra(Constants.RESPOND_BY, messageList.get(position).getName());
                detailsIntent.putExtra(Constants.PROFILE_PIC, messageList.get(position).getProfile_pic());
                detailsIntent.putExtra(Constants.SKILL, messageList.get(position).getSkill());
                startActivity(detailsIntent, nextAnimation);
            }
        }
    };

    protected void setWarning(String message, int image) {
        listView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity_new.mainActivity.closeSearch();
        new FetchMessage().execute();
    }

    private void refreshList() {
        //Log.e("refreshList()", " messageList => " + messageList);
        listView.setVisibility(View.VISIBLE);
        messageListAdapter.clear();
        messageListAdapter.addAll(messageList);
        messageListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_list_create_button:
                if (!CheckUserType.isGuest()) {
                    Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                            (activity.getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();
                    Intent createIntent = new Intent(activity.getApplicationContext(), CreateMessageActivity.class);
                    startActivity(createIntent, nextAnimation);
                } else {
                    ShowToast.toast(activity.getApplicationContext(), Warnings.GUEST_LOGIN);
                }
                break;
        }
    }

    private class FetchMessage extends AsyncTask<Void, Void, Integer> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (messageList == null && messageList.size() <= 0) {
                setWarning(Warnings.LOADING, R.drawable.ic_data);
            }
            showLoader.sendLoadingDialog();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.ACTION, Actions_.GET_MESSAGES)
                    .build();
            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.MESSAGE_OPERATIONS, requestBody);
                if (response != null) {
                    result = Messages_.messageResult(response);
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
            Log.e("onPost", "integer -> " + integer);
            switch (integer) {
                case 0:
                    showLoader.dismissLoadingDialog();
                    if (messageList == null && messageList.size() <= 0)
                        setWarning(Warnings.NO_MESSAGE_RECEIVED, R.drawable.ic_no_data);
                    break;
                case 1:
                    new LoadMessage().execute(response);
                    break;
                case 2:
                    showLoader.dismissLoadingDialog();
                    setWarning(Warnings.NO_MESSAGE_RECEIVED, R.drawable.ic_no_data);
                    break;
                case 11:
                    showLoader.dismissLoadingDialog();
                    if (messageList == null && messageList.size() <= 0)
                        setWarning(Warnings.INTERNAL_ERROR_WARNING, R.drawable.ic_network_problem);
                    break;
                case 12:
                    showLoader.dismissLoadingDialog();
                    if (messageList == null && messageList.size() <= 0)
                        setWarning(Warnings.NETWORK_ERROR_WARNING, R.drawable.ic_network_problem);
                    break;
            }
        }
    }


    private class LoadMessage extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Messages_.parseMessage(params[0], activity.getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            messageList.clear();
            messageList = retrieveOperation.getMessage("10");
            if (messageList == null || messageList.size() <= 0) {
                setWarning(Warnings.NO_MESSAGE_RECEIVED, R.drawable.ic_no_data);
            } else {
                listView.setVisibility(View.VISIBLE);
                warningText.setVisibility(View.GONE);

                refreshList();
            }
            showLoader.dismissLoadingDialog();
        }
    }
}