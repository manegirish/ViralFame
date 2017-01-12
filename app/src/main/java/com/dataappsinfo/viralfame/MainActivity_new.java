
/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.dataappsinfo.viralfame;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.technoindians.adapter.DrawerListAdapter;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Fragment_;
import com.technoindians.constants.Warnings;
import com.technoindians.firebase.Config;
import com.technoindians.firebase.MyFirebaseInstanceIDService;
import com.technoindians.firebase.NotificationUtils;
import com.technoindians.library.CheckUserType;
import com.technoindians.library.FileCheck;
import com.technoindians.library.ImageDownloader;
import com.technoindians.library.SearchUser;
import com.technoindians.library.ShowLoader;
import com.technoindians.message.MessageListFragment;
import com.technoindians.network.CheckInternet;
import com.technoindians.network.ConnectivityReceiver;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.opportunities.OpportunitiesMainFragment;
import com.technoindians.opportunities.ReceivedListFragment;
import com.technoindians.peoples.PeopleMainFragment;
import com.technoindians.pops.ShowSnack;
import com.technoindians.pops.ShowToast;
import com.technoindians.portfolio.PortfolioMainActivity;
import com.technoindians.preferences.Preferences;
import com.technoindians.settings.PreferencesFragment;
import com.technoindians.views.CircleTransform;
import com.technoindians.wall.WallFeedFragment;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MainActivity_new extends AppCompatActivity
        implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = MainActivity_new.class.getSimpleName();
    public static MainActivity_new mainActivity;
    private static boolean isOpen = false;
    public EditText searchBox;
    TextView titleText, nameText, skillText;
    ImageView createPost, profilePhoto, backgroundPhoto;
    ListView listView;
    RelativeLayout drawerLayout;
    DrawerListAdapter drawerListAdapter;
    private ArrayList<String> menuList;
    private ArrayList<Integer> counterList;
    private DrawerLayout drawer;
    private ShowLoader showLoader;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private GoogleApiClient client;
    private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerStateChanged(int status) {
        }

        @Override
        public void onDrawerSlide(View view, float slideArg) {
        }

        @Override
        public void onDrawerOpened(View view) {
            invalidateOptionsMenu();
            if (Preferences.contains(Constants.CHANGE_IN_PHOTO))
                if (Preferences.get(Constants.CHANGE_IN_PHOTO).equalsIgnoreCase("1"))
                    setHeader();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Preferences.initialize(getApplicationContext());

        mainActivity = this;

        showLoader = new ShowLoader(mainActivity);

        menuList = new ArrayList<>();
        counterList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleText = (TextView) toolbar.findViewById(R.id.app_bar_main_title);
        titleText.setVisibility(View.VISIBLE);

        searchBox = (EditText) toolbar.findViewById(R.id.app_bar_main_search);
        searchBox.setVisibility(View.GONE);

        createPost = (ImageView) toolbar.findViewById(R.id.app_bar_main_search_button);
        createPost.setOnClickListener(this);

        nameText = (TextView) findViewById(R.id.nav_header_name);
        skillText = (TextView) findViewById(R.id.nav_header_skill);
        profilePhoto = (ImageView) findViewById(R.id.nav_header_photo);
        profilePhoto.setOnClickListener(this);
        backgroundPhoto = (ImageView) findViewById(R.id.nav_header_background_photo);
        backgroundPhoto.setOnClickListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout = (RelativeLayout) findViewById(R.id.drawer_layout_relative);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setDrawerListener(mDrawerListener);
        drawer.closeDrawer(GravityCompat.START);
        toggle.syncState();

        replaceFragment(new WallFeedFragment());
        setTitle(getApplicationContext().getResources().getString(R.string.app_name));
        setUpDrawer();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    Log.e(TAG, "USER_ID: " + Preferences.get(Constants.USER_ID));
                    if (Preferences.contains(Constants.USER_ID) && Preferences.get(Constants.USER_ID) != null) {
                        if (!Preferences.get(Constants.USER_ID).equalsIgnoreCase("0"))
                            displayFirebaseRegId();
                    }

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

    }

    /* private void toggleSearch() {
         if (!isOpen) {
             searchBox.setText("");
             isOpen = true;
             searchBox.setVisibility(View.VISIBLE);
             titleText.setVisibility(View.GONE);
             createPost.setImageResource(R.drawable.ic_cancel);
         } else {
             closeSearch();
         }
     }
 */
    public void closeSearch() {
        isOpen = false;
        searchBox.setText("");
        searchBox.setVisibility(View.GONE);
        titleText.setVisibility(View.VISIBLE);
        createPost.setImageResource(R.drawable.ic_search_white);
    }

    public void setTitle(String title) {
        titleText.setText(title);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logout() {
        Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logoutIntent);
        finish();
        Preferences.clear();
        Preferences.save(Constants.LOGIN, "0");
    }

    public void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment oldFragment = fm.findFragmentByTag(Fragment_.FOLLOWERS);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }

        oldFragment = fm.findFragmentByTag(Fragment_.OPPORTUNITIES);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fm.findFragmentByTag(Fragment_.OPPORTUNITIES_SENT);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }

        oldFragment = fm.findFragmentByTag(Fragment_.OPPORTUNITIES_RECEIVED);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }

        oldFragment = fm.findFragmentByTag(Fragment_.MAIN_FRAGMENT);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.setCustomAnimations(R.animator.slide_in_left, 0, 0, R.animator.slide_out_right);
        ft.replace(R.id.container_main, fragment, Fragment_.MAIN_FRAGMENT);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_bar_main_search_button:
                if (!isOpen) {
                    isOpen = true;
                    searchBox.setVisibility(View.VISIBLE);
                    titleText.setVisibility(View.GONE);
                } else {
                    SearchUser.search(searchBox.getText().toString().trim());
                    hideKeyboard();
                }
                break;
            case R.id.nav_header_background_photo:
                break;
            case R.id.nav_header_photo:
                drawer.closeDrawer(GravityCompat.START);
                getFragment(1);
                break;
        }
    }

    private Fragment getFragment(int position) {
        Fragment fragment = new WallFeedFragment();
        createPost.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                fragment = new WallFeedFragment();
                break;
            case 1:
                Intent next = new Intent(getApplicationContext(), PortfolioMainActivity.class);
                Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                        (getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();
                next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(next, nextAnimation);
                break;
            case 2:
                if (Integer.parseInt(Preferences.get(Constants.USER_TYPE)) == 2) {
                    fragment = new OpportunitiesMainFragment();
                } else {
                    fragment = new ReceivedListFragment();
                }
                break;
            case 3:
                fragment = new MessageListFragment();
                break;
            case 4:
                fragment = new PeopleMainFragment();
                break;
            case 5:
                createPost.setVisibility(View.GONE);
                fragment = new PreferencesFragment();
                break;
            case 6:
                shareApplication();
                break;
            case 7:
                if (CheckInternet.check()) {
                    new Logout().execute();
                } else {
                    ShowSnack.noInternet(drawerLayout);
                }
                break;
        }
        if (position != 6 && position != 7) {
            return fragment;
        } else {
            return null;
        }
    }

    private void shareApplication() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Are you artist? Grab this opportunity!");
            String sAux = "\nLet me recommend you this application for all type of artist\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.dataappsinfo.viralfame\n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Choose one"));
        } catch (Exception e) {
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected)
            ShowToast.noNetwork(getApplicationContext());
    }

    private void setUpDrawer() {
        setHeader();
        int[] iconSet = prepareListData();
        listView = (ListView) findViewById(R.id.drawer_list_view);
        drawerListAdapter = new DrawerListAdapter(MainActivity_new.this, menuList, iconSet, counterList);
        listView.setAdapter(drawerListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2 && CheckUserType.isGuest()) {
                    ShowToast.toast(getApplicationContext(), Warnings.GUEST_LOGIN);
                    return;
                }
                replaceFragment(getFragment(position));
                drawer.closeDrawer(drawerLayout);
            }
        });
    }

    private int[] prepareListData() {
        menuList.add(getApplicationContext().getResources().getString(R.string.home));
        menuList.add(getApplicationContext().getResources().getString(R.string.portfolio));
        menuList.add(getApplicationContext().getResources().getString(R.string.opportunities));
        menuList.add(getApplicationContext().getResources().getString(R.string.messages));
        menuList.add(getApplicationContext().getResources().getString(R.string.my_people));
        menuList.add(getApplicationContext().getResources().getString(R.string.preferences));
        menuList.add(getApplicationContext().getResources().getString(R.string.share));
        menuList.add(getApplicationContext().getResources().getString(R.string.logout));

        int[] iconSet = {
                R.drawable.ic_home_d,
                R.drawable.ic_portfolio,
                R.drawable.ic_opportunity,
                R.drawable.ic_message,
                R.drawable.ic_people_d,
                R.drawable.ic_settings,
                R.drawable.ic_share_n,
                R.drawable.ic_logout
        };

        counterList.add(0);
        counterList.add(0);
        counterList.add(0);
        counterList.add(0);
        counterList.add(0);
        counterList.add(0);
        counterList.add(0);
        counterList.add(0);

        return iconSet;
    }

    public void setHeader() {
        Preferences.save(Constants.CHANGE_IN_PHOTO, "0");
        nameText.setText(Preferences.get(Constants.NAME));
        skillText.setText(Preferences.get(Constants.PRIMARY_SKILL));
        if (Preferences.get(Constants.LOCAL_PATH) != null &&
                new File(Preferences.get(Constants.LOCAL_PATH)).exists()) {
            Picasso.with(getApplicationContext())
                    .load(new File(Preferences.get(Constants.LOCAL_PATH)))
                    .transform(new CircleTransform())
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .into(profilePhoto);
            setBackground(true);
        } else {

            if (Preferences.contains(Constants.PROFILE_PIC)) {
                try {
                    ImageDownloader.downloadImage(Urls.DOMAIN + Preferences.get(Constants.PROFILE_PIC),
                            FileCheck.getFileName(Preferences.get(Constants.PROFILE_PIC)), true, getApplicationContext(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setBackground(false);
        }
    }

    private void setBackground(boolean exists) {
        backgroundPhoto.invalidate();
        backgroundPhoto.setImageDrawable(null);
        if (exists) {
            GlideDrawableImageViewTarget ivTarget = new GlideDrawableImageViewTarget(backgroundPhoto);
            Glide.with(this)
                    .load(new File(Preferences.get(Constants.LOCAL_PATH)))
                    .bitmapTransform(new BlurTransformation(getApplicationContext()))
                    .crossFade()
                    .placeholder(R.drawable.ic_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivTarget);
        } else {
            Glide.with(this).load(R.drawable.ic_avatar)
                    .bitmapTransform(new BlurTransformation(getApplicationContext()))
                    .placeholder(R.drawable.ic_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(backgroundPhoto);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayFirebaseRegId();
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

        ViralFame.getInstance().setConnectivityListener(this);
        if (!CheckInternet.check())
            ShowToast.noNetwork(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void displayFirebaseRegId() {
        Preferences.initialize(getApplicationContext());
        String regId = Preferences.get("regId");
        Log.e(TAG, "Firebase reg id: " + regId);
        if (regId != null) {
            MyFirebaseInstanceIDService.getToken(getApplicationContext());
        }
    }

    private class Logout extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.sendLoadingDialog();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.LOGOUT_URL, requestBody, TAG);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.LOG_OUT)) {
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.LOG_OUT);
                        return responseObject.getInt(Constants.STATUS);
                    } else {
                        return 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 12;
            }
            return 12;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 0:
                    ShowToast.networkProblemToast(getApplicationContext());
                    break;
                case 1:
                    ShowToast.toast(getApplicationContext(), Warnings.SUCCESSFUL);
                    logout();
                    break;
                case 2:
                    ShowToast.actionFailed(getApplicationContext());
                    break;
                case 11:
                    ShowToast.networkProblemToast(getApplicationContext());
                    break;
                case 12:
                    ShowToast.internalErrorToast(getApplicationContext());
                    break;
                default:
                    ShowToast.actionFailed(getApplicationContext());
                    break;
            }
            showLoader.dismissSendingDialog();
        }
    }
}