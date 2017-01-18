package com.technoindians.wall;

import android.os.AsyncTask;

import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Wall_;
import com.technoindians.preferences.Preferences;

import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Girish on 18-01-2017.
 */

public class WallOperations_ {

    private static final String TAG = WallOperations_.class.getSimpleName();

    public static int delete(String _id) {
        RequestBody deleteBody = new FormBody.Builder()
                .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                .add(Constants.ID, _id)
                .add(Constants.ACTION, Actions_.DELETE_POST)
                .build();
        String url = Urls.DOMAIN + Urls.POST_OPERATIONS_URL;
        try {
            String response = new Operation(deleteBody, url).execute().get();
            return Wall_.operationParser(response, JsonArrays_.DELETE_POST);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 12;
    }

    public static int remove(String _id) {

        return 12;
    }

    public static int spam(String _id) {
        RequestBody deleteBody = new FormBody.Builder()
                .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                .add(Constants.ID, _id)
                .add(Constants.ACTION, Actions_.SPAM_POST)
                .build();
        String url = Urls.DOMAIN + Urls.POST_OPERATIONS_URL;
        try {
            String response = new Operation(deleteBody, url).execute().get();
            return Wall_.operationParser(response, JsonArrays_.SPAM_POST);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 12;

    }

    private static class Operation extends AsyncTask<Void, Void, String> {
        RequestBody requestBody;
        String url;

        protected Operation(RequestBody requestBody, String url) {
            this.requestBody = requestBody;
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL, requestBody, TAG);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
