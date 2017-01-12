/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.technoindians.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.technoindians.constants.Constants;
import com.technoindians.preferences.Preferences;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    public static String getToken(Context context) {
        Preferences.initialize(context);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);
        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);
        Log.e(TAG, "refreshedToken: " + refreshedToken);
        return refreshedToken;
    }

    private static void sendRegistrationToServer(final String token) {
        if (token != null)
            new RegisterCall().execute(token);
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private static void storeRegIdInPref(String token) {
        Preferences.save("regId", token);
        Log.e(TAG, "storeRegIdInPref: " + token);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Preferences.initialize(getApplicationContext());
        if (Preferences.contains(Constants.USER_ID) && Preferences.get(Constants.USER_ID) != null) {
            if (!Preferences.get(Constants.USER_ID).equalsIgnoreCase("0")) {
                Log.e(TAG, "onTokenRefresh: ");
                String refreshedToken = getToken(getApplicationContext());
                Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("token", refreshedToken);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
            }
        }
    }
}
