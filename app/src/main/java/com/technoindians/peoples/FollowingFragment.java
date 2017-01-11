package com.technoindians.peoples;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.MainActivity_new;
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
import com.technoindians.preferences.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by girish on 12/8/16.
 */

public class FollowingFragment extends Fragment {

    private static final String TAG = FollowingFragment.class.getSimpleName();
    ListView listView;
    TextView warningText;
    private ArrayList<Follow> followList;

    private FollowListAdapter followListAdapter;
    private Activity activity;
    private ShowLoader showLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_layout, null);

        activity = getActivity();
        Preferences.initialize(activity.getApplicationContext());
        MainActivity_new.mainActivity.setTitle(activity.getApplicationContext()
                .getResources().getString(R.string.my_people));

        showLoader = new ShowLoader(activity);
        followList = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.layout_list_view);
        warningText = (TextView) view.findViewById(R.id.list_view_layout_warning);

        ColorDrawable color = new ColorDrawable(this.getResources().getColor(R.color.black_45));
        listView.setDivider(color);
        listView.setDividerHeight(2);
        MainActivity_new.mainActivity.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = MainActivity_new.mainActivity.searchBox.getText().toString().toLowerCase(Locale.getDefault());
                if (text != null&&followListAdapter!=null) {
                    followListAdapter.filter(text);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    protected void setWarning(String message, int image) {
        listView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();

        new GetData().execute();
    }

    private class GetData extends AsyncTask<Void, Void, Integer> {
        String response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setWarning(Warnings.LOADING, R.drawable.ic_data);
            showLoader.sendLoadingDialog();
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
                response = MakeCall.post(Urls.DOMAIN + Urls.FOLLOWER_OPERATIONS, requestBody,TAG);
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
                    setWarning(Warnings.NO_FOLLOWING, R.drawable.ic_no_data);
                    break;
                case 1:
                    new LoadData().execute(response);
                    break;
                case 2:
                    showLoader.dismissSendingDialog();
                    setWarning(Warnings.NO_FOLLOWING, R.drawable.ic_no_data);
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
                setWarning(Warnings.NO_FOLLOWING,R.drawable.ic_no_data);
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
