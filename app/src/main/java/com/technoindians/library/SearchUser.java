package com.technoindians.library;

import android.os.AsyncTask;

import com.technoindians.constants.Constants;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.preferences.Preferences;

import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by root on 9/12/16.
 */

public class SearchUser {

    private static final String TAG = SearchUser.class.getSimpleName();

    public static String search(String keyword) {
        RequestBody requestBody = new FormBody.Builder()
                .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                .add(Constants.KEYWORD, keyword)
                .build();
        try {
            return new SearchUsers().execute(requestBody).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class SearchUsers extends AsyncTask<RequestBody, Void, String> {

        @Override
        protected String doInBackground(RequestBody... params) {
            try {
                return MakeCall.post(Urls.DOMAIN + Urls.SEARCH_RESULT, params[0], TAG);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
