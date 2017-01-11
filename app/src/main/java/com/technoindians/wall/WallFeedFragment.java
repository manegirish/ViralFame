
/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.technoindians.wall;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.MainActivity_new;
import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.WallFeedAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.database.RetrieveOperation;
import com.technoindians.database.TableList;
import com.technoindians.library.CheckUserType;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Wall_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by girish on 27/6/16.
 */
public class WallFeedFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private String TAG = WallFeedFragment.class.getSimpleName();

    private ListView feedListView;
    private TextView warningText, postText;

    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Cursor feedCursor;

    private RetrieveOperation retrieveOperation;
    WallFeedAdapter wallFeedAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wall_feed_list_view_layout, null);

        activity = getActivity();

        retrieveOperation = new RetrieveOperation(activity.getApplicationContext());

        MainActivity_new.mainActivity.setTitle(activity.getApplicationContext()
                .getResources().getString(R.string.home));
        Preferences.initialize(activity.getApplicationContext());

        feedListView = (ListView) view.findViewById(R.id.wall_feed_list_view);
        warningText = (TextView) view.findViewById(R.id.wall_feed_list_view_warning);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.wall_feed_list_view_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        postText = (TextView) view.findViewById(R.id.wall_feed_message_box);
        postText.setOnClickListener(this);

        //feedListView.setOnScrollListener(this);

        return view;
    }


    public void pullFromCache() {
        new Load().execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                pullFromCache();
            }
        });
    }

/*    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int last_position = feedListView.getLastVisiblePosition();
        int size = feedsList.size();
        if ((last_position + 2) == size) {
            Log.e(TAG, "onScroll() => " + feedsList.get(size - 1).getId() + "\nsize -> " + size);
            //pullFromCache(feedsList.get(size - 1).getLast_updated());
        }
    }*/

    private class Load extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setWarning(Warnings.LOADING, R.drawable.ic_data);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            feedCursor = retrieveOperation.fetchFeed();
            if (feedCursor != null) {
                return 1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1) {
                warningText.setVisibility(View.GONE);
                feedListView.setVisibility(View.VISIBLE);

                wallFeedAdapter = new WallFeedAdapter(activity, feedCursor);
                feedListView.setAdapter(wallFeedAdapter);
            } else {
                setWarning(Warnings.NO_DATA, R.drawable.ic_no_data);
            }
        }
    }

    protected void setWarning(String message, int image) {
        feedListView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wall_feed_message_box:
                if (!CheckUserType.isGuest()) {
                    Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                            (activity.getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();
                    Intent next = new Intent(activity.getApplicationContext(), TimelineMessagePostActivity.class);
                    startActivity(next, nextAnimation);
                } else {
                    ShowToast.toast(activity.getApplicationContext(), Warnings.GUEST_LOGIN);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the activity result was received from the "Get Car" request
        if (Constants.INTENT_ACTIVITY == requestCode) {
            // If the activity confirmed a selection
            if (Activity.RESULT_OK == resultCode) {
                String carId = data.getStringExtra(Constants.IS_REFRESH);
                Log.e("onActivityResult", "carId " + carId);
            } else {
                // You can handle a case where no selection was made if you want
                Log.e("onActivityResult", "failed");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRefresh() {
        new GetFeed().execute(true);
    }

    private class GetFeed extends AsyncTask<Boolean, Void, Integer> {
        String response = null;
        String date = "0";
        boolean isRefresh = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            date = retrieveOperation.getUtc(TableList.TABLE_WALL_FEED, Preferences.get(Constants.USER_ID));
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Integer doInBackground(Boolean... params) {
            isRefresh = params[0];
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.LAST_DATE, date)
                    .add(Constants.ACTION, Actions_.GET_POST)
                    .build();

            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL, requestBody);
                result = Wall_.feedResult(response);
            } catch (Exception e) {
                result = 11;
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 0:
                    swipeRefreshLayout.setRefreshing(false);
                    if (isRefresh) {
                        ShowToast.toast(activity.getApplicationContext(), Warnings.NO_MORE_UPDATE);
                    } else {
                        setWarning(Warnings.NO_DATA, R.drawable.ic_no_data);
                    }
                    break;
                case 1:
                    if (response != null) {
                        Wall_.parseFeed(response, activity.getApplicationContext());
                        feedCursor = retrieveOperation.fetchFeed();
                        wallFeedAdapter.changeCursor(feedCursor);
                        wallFeedAdapter.notifyDataSetChanged();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case 2:
                    swipeRefreshLayout.setRefreshing(false);
                    if (isRefresh) {
                        ShowToast.toast(activity.getApplicationContext(), Warnings.NO_MORE_UPDATE);
                    } else {
                        setWarning(Warnings.NO_DATA, R.drawable.ic_no_data);
                    }
                    break;
                case 11:
                    swipeRefreshLayout.setRefreshing(false);
                    if (isRefresh) {
                        ShowToast.toast(activity.getApplicationContext(), Warnings.INTERNAL_ERROR_WARNING);
                    } else {
                        setWarning(Warnings.INTERNAL_ERROR_WARNING, R.drawable.ic_network_problem);
                    }
                    break;
                case 12:
                    swipeRefreshLayout.setRefreshing(false);
                    if (isRefresh) {
                        ShowToast.toast(activity.getApplicationContext(), Warnings.NETWORK_ERROR_WARNING);
                    } else {
                        setWarning(Warnings.NETWORK_ERROR_WARNING, R.drawable.ic_network_problem);
                    }
                    break;
            }
        }
    }

    /*private class RefreshList extends AsyncTask<String, Void, Integer> {
        ArrayList<Feed_> refreshList = null;

        @Override
        protected Integer doInBackground(String... params) {
            refreshList = Wall_.parseFeed(params[0], activity.getApplicationContext());
            Log.e(TAG, "refreshList size -> " + refreshList.size() + "list -> " + refreshList);
            for (int i = 0; i < refreshList.size(); i++) {
                if (isDuplicate(refreshList.get(i))) {
                    refreshList.remove(i);
                }
            }
            Log.e(TAG, "refreshList size -> " + refreshList.size() + "list -> " + refreshList);
            if (refreshList != null && refreshList.size() > 0) {
                refreshList.addAll(feedsList);
                return 1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 1) {
                feedListView.setVisibility(View.VISIBLE);
                warningText.setVisibility(View.GONE);

                wallFeedListAdapter.addAll(refreshList);
                wallFeedListAdapter.notifyDataSetInvalidated();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }*/
}
