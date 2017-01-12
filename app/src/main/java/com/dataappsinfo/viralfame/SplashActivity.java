
/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.dataappsinfo.viralfame;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.technoindians.constants.Constants;
import com.technoindians.firebase.Config;
import com.technoindians.firebase.NotificationUtils;
import com.technoindians.library.RuntimePermissionsActivity;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author Girish M
 *         Created on 21/6/16.
 *         Last modified 01/08/2016
 */

public class SplashActivity extends RuntimePermissionsActivity {

    private static final int REQUEST_PERMISSIONS = 20;
    int login = 0;
    Animation startAnimation;
    ImageView icon;
    TextView appName;
    private String TAG = SplashActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Preferences.initialize(getApplicationContext());

        if (Preferences.contains(Constants.LOGIN)) {
            login = Integer.parseInt(Preferences.get(Constants.LOGIN));
        }

        appName = (TextView)findViewById(R.id.splash_app_name);
        icon = (ImageView) findViewById(R.id.ic_logo);
        startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        try {
            @SuppressLint
                    ("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.dataappsinfo.viralfame",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //   Log.d("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.e(TAG,"YourKeyHash: "+Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }

        displayFirebaseRegId();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (requestCode == 20) {
            intent();
        } else {
            ShowToast.toast(getApplicationContext(), "Please Enable all permissions");
        }
    }

    private void intent() {
        Intent homeIntent;
        if (login == 1) {
            homeIntent = new Intent(getApplicationContext(), MainActivity_new.class);
        } else {
            homeIntent = new Intent(getApplicationContext(), LoginActivity.class);
        }
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);


        Log.e(TAG, "Firebase reg id: " + regId);

        //    if (!TextUtils.isEmpty(regId))
        //   txtRegId.setText("Firebase Reg Id: " + regId);
        //   else
        //   txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        icon.startAnimation(startAnimation);
        startAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                SplashActivity.super.requestAppPermissions(new
                                String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}, R.string.runtime_permissions_txt
                        , REQUEST_PERMISSIONS);
                /*} else {
                    intent();
                }*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}