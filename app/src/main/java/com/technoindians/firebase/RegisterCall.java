package com.technoindians.firebase;

/**
 * Created by Girish on 12-01-2017.
 */

import android.os.AsyncTask;

import com.technoindians.constants.Constants;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.preferences.Preferences;

import okhttp3.FormBody;
import okhttp3.RequestBody;

class RegisterCall extends AsyncTask<String, Void, Void> {

    private static final String TAG = RegisterCall.class.getSimpleName();

    @Override
    protected Void doInBackground(String... params) {
        RequestBody requestBody = new FormBody.Builder()
                .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                .add("regId", params[0])
                .build();
        try {
            String response = MakeCall.post(Urls.DOMAIN + "fire.php", requestBody,TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
