package com.technoindians.opportunities;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.dataappsinfo.viralfame.MainActivity_new;
import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.ReceivedJobListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.library.ShowLoader;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.JobParser_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.DeleteSwipeMenu;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Girish Mane <girish@sagesurfer.com>
 *         Created on 01-07-2016
 *         Last Modified on 23-08-2016
 */

public class ReceivedListFragment extends Fragment {

    private static String TAG = ReceivedListFragment.class.getSimpleName();
    private SwipeMenuListView listView;
    private TextView warningText;
    private ArrayList<Jobs_> jobList;
    private Jobs_ jobs_;

    private ReceivedJobListAdapter jobListAdapter;
    private ShowLoader showLoader;
    private Activity activity;
    private DeleteSwipeMenu deleteSwipeMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_delete_list_view_layout, null);

        activity = getActivity();
        Preferences.initialize(activity.getApplicationContext());
        MainActivity_new.mainActivity.setTitle(activity.getApplicationContext()
                .getResources().getString(R.string.opportunities));

        jobList = new ArrayList<>();

        showLoader = new ShowLoader(activity);
        deleteSwipeMenu = new DeleteSwipeMenu(activity);

        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_list_view);
        listView.setMenuCreator(deleteSwipeMenu);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listView.setOnMenuItemClickListener(onMenuItemClick);

        warningText = (TextView) view.findViewById(R.id.swipe_list_view_layout_warning);

        ColorDrawable color = new ColorDrawable(this.getResources().getColor(R.color.black_45));
        listView.setDivider(color);
        listView.setDividerHeight(2);
        MainActivity_new.mainActivity.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = MainActivity_new.mainActivity.searchBox.getText().toString().toLowerCase(Locale.getDefault());
                if (jobListAdapter != null) {
                    jobListAdapter.filterJobs(text);
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

    SwipeMenuListView.OnMenuItemClickListener onMenuItemClick = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            listView.smoothOpenMenu(position);
            switch (index) {
                case 0:
                    operateListItem(position, true);
                    break;
            }
            return false;
        }
    };

    private void operateListItem(int position, boolean isDelete) {
        if (isDelete) {
            jobs_ = new Jobs_();
            jobs_ = jobList.get(position);
            jobList.remove(position);
            showSnack(position);
        } else {
            jobList.add(position, jobs_);
        }
        jobListAdapter.notifyDataSetInvalidated();
    }

    private void showSnack(final int position) {
        Snackbar mainBar = Snackbar
                .make(listView, "Job is deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        operateListItem(position, false);
                        Snackbar childBar = Snackbar.make(listView, "Job is restored!", Snackbar.LENGTH_SHORT);
                        childBar.setDuration(Snackbar.LENGTH_SHORT);
                        childBar.show();
                    }
                });
        mainBar.setActionTextColor(activity.getApplicationContext().getResources().getColor(R.color.colorPrimary));
        mainBar.setDuration(Snackbar.LENGTH_LONG);
        View sbView = mainBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        mainBar.show();
        mainBar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event == 0 || event == 2 || event == 4) {
                    new Operation().execute(position);
                }
                Log.e(TAG, "onDismissed \nevent -> " + event + "position -> " + position);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setWarning(Warnings.LOADING, R.drawable.ic_data);
        new GetData().execute();
    }

    protected void setWarning(String message, int image) {
        listView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    private class GetData extends AsyncTask<Void, Void, Integer> {
        String response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.sendLoadingDialog();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.ACTION, Actions_.GET_JOBS)
                    .build();

            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.JOB_OPERATIONS, requestBody);
                if (response != null) {
                    result = JobParser_.jobsResult(response);
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
                    showLoader.dismissSendingDialog();
                    setWarning(Warnings.NO_MATCHING_JOB, R.drawable.ic_no_data);
                    break;
                case 1:
                    new LoadData().execute(response);
                    listView.setVisibility(View.VISIBLE);
                    warningText.setVisibility(View.GONE);

                    break;
                case 2:
                    showLoader.dismissSendingDialog();
                    setWarning(Warnings.NO_MATCHING_JOB, R.drawable.ic_no_data);
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

    private class LoadData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            jobList = JobParser_.parseJobs(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (jobList == null || jobList.size() <= 0) {
                setWarning(Warnings.NO_MATCHING_JOB, R.drawable.ic_no_data);
            } else {
                jobListAdapter = new ReceivedJobListAdapter(activity, jobList);
                listView.setAdapter(jobListAdapter);
            }
            showLoader.dismissSendingDialog();
        }
    }

    private class Operation extends AsyncTask<Integer, Void, Integer> {
        int position;
        @Override
        protected Integer doInBackground(Integer... params) {
            int result = 12;
            position = params[0];
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.ID, jobList.get(position-1).getId())
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.ACTION, Actions_.HIDE_JOB)
                    .build();

            try {
                //{"hide_job":{"msg":"Job hide Successfully","status":1}}
                String response = MakeCall.post(Urls.DOMAIN + Urls.JOB_OPERATIONS, requestBody);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.HIDE_JOB)) {
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.HIDE_JOB);
                        return responseObject.getInt(Constants.STATUS);
                    } else {
                        result = 11;
                    }
                }
            } catch (Exception e) {
                result = 11;
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != 1) {
                operateListItem(position, false);
                ShowToast.toast(activity.getApplicationContext(),Warnings.ERROR_IN_DELETE);
            }
        }
    }
}


