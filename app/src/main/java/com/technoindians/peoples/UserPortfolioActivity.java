package com.technoindians.peoples;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.dataappsinfo.viralfame.R;
import com.dataappsinfo.viralfame.ViralFame;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Fragment_;
import com.technoindians.directory.DirectoryList;
import com.technoindians.library.BlurBuilder;
import com.technoindians.library.FileCheck;
import com.technoindians.library.ImageDownloader;
import com.technoindians.library.ShowLoader;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Portfolio_;
import com.technoindians.pops.ShowToast;
import com.technoindians.portfolio.AudioGalleryFragment;
import com.technoindians.portfolio.FollowersDialogFragment;
import com.technoindians.portfolio.FollowingDialogFragment;
import com.technoindians.portfolio.ImageGalleryFragment;
import com.technoindians.portfolio.VideoGalleryFragment;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.CircleTransformMain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class UserPortfolioActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView imageView;
    private String friend_id, friend_name, friend_photo, friend_skill;
    private HashMap<String, String> profileMap;
    private String is_follow = "3";
    ImageView backButton, profilePhoto;
    TextView nameText, followButton, skillText, aboutTag, followerCountText, followingCountText;
    TextView aboutText;
    RelativeLayout profilePicLayout,editLayout;
    LinearLayout followerButton, followingButton;

    private CollapsingToolbarLayout collapsingToolbar;
    private TabPagerAdapter tabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ShowLoader showLoader;
    public static UserPortfolioActivity userPortfolioActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.portfolio_fragment_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        userPortfolioActivity = this;

        Intent data = getIntent();
        if (data != null && data.hasExtra(Constants.USER_ID)) {
            friend_id = data.getStringExtra(Constants.USER_ID);
            friend_name = data.getStringExtra(Constants.NAME);
            friend_photo = data.getStringExtra(Constants.PROFILE_PIC);
            friend_skill = data.getStringExtra(Constants.SKILL);
            if (data.hasExtra(Constants.IS_FOLLOW)) {
                is_follow = data.getStringExtra(Constants.IS_FOLLOW);
            }
        } else {
            onBackPressed();
        }
        showLoader = new ShowLoader(UserPortfolioActivity.this);

        imageView = (ImageView) findViewById(R.id.backdrop);
        profilePhoto = (ImageView) findViewById(R.id.portfolio_profile_photo);
        followerCountText = (TextView) findViewById(R.id.portfolio_follower_count);
        followingCountText = (TextView) findViewById(R.id.portfolio_following_count);
        nameText = (TextView) findViewById(R.id.portfolio_profile_name);
        skillText = (TextView) findViewById(R.id.portfolio_profile_skill);
        aboutTag = (TextView) findViewById(R.id.portfolio_profile_tag);
        aboutTag.setOnClickListener(this);
        aboutTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        aboutText = (TextView) findViewById(R.id.portfolio_profile_about_me);
        followButton = (TextView) findViewById(R.id.portfolio_profile_follow);
        if (is_follow.equalsIgnoreCase("3")) {
            followButton.setVisibility(View.GONE);
        } else {
            toggleFollow();
        }
        followButton.setOnClickListener(this);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        setToolbar();

        backButton = (ImageView) toolbar.findViewById(R.id.portfolio_main_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profilePicLayout = (RelativeLayout) findViewById(R.id.portfolio_profile_pic);

        followerButton = (LinearLayout) findViewById(R.id.portfolio_follower_layout);
        followerButton.setOnClickListener(this);
        followingButton = (LinearLayout) findViewById(R.id.portfolio_following_layout);
        followingButton.setOnClickListener(this);
        editLayout = (RelativeLayout)findViewById(R.id.portfolio_profile_photo_edit);
        editLayout.setVisibility(View.GONE);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(tabPagerAdapter);
        mTabLayout.setTabsFromPagerAdapter(tabPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void toggleFollow(){
        if (Integer.parseInt(is_follow) == 1) {
            followButton.setText("Unfollow");
        } else {
            followButton.setText("Follow");
        }
        followButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
    }


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private class DownPhoto extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                   }
        @Override
        protected Void doInBackground(String... params) {
            String file_name = FileCheck.getFileName(params[0]);
            try {
                ImageDownloader.downloadImage(Urls.DOMAIN + params[0], file_name, false, getApplicationContext(), 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void updateImage(){
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_avtar_square);
        String local_image = DirectoryList.PROFILE + FileCheck.getFileName(friend_photo);
        if (new File(local_image).exists()) {
            myBitmap = BitmapFactory.decodeFile(local_image);
        }
        Bitmap blurredBitmap = BlurBuilder.blur(UserPortfolioActivity.this, myBitmap);
        imageView.setBackgroundDrawable(new BitmapDrawable(getResources(), blurredBitmap));

        Picasso.with(getApplicationContext())
                .load(new File(local_image))
                .transform(new CircleTransformMain())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.ic_avtar)
                .error(R.drawable.ic_avtar)
                .into(profilePhoto);

        profilePhoto.invalidate();
        imageView.invalidate();
    }

    private void setData() {
        String local_image = DirectoryList.PROFILE + FileCheck.getFileName(friend_photo);
        if (new File(local_image).exists()) {
            new File(local_image).delete();
        }
        nameText.setText(friend_name);
        skillText.setText(friend_skill);
        ImageLoader imageLoader = ViralFame.getInstance().getImageLoader();
        imageLoader.get(Urls.DOMAIN + friend_photo,imageListener);
        new DownPhoto().execute(friend_photo);
        new GetPortfolio().execute();
    }

    ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            response.getBitmap();
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.portfolio_follower_layout:
                if (profileMap != null && profileMap.containsKey(Constants.FOLLOWER_COUNT))
                    if (Integer.parseInt(profileMap.get(Constants.FOLLOWER_COUNT)) > 0) {
                        openFragment(new FollowersDialogFragment());
                    }
                break;
            case R.id.portfolio_following_layout:
                if (profileMap != null && profileMap.containsKey(Constants.FOLLOWING_COUNT))
                    if (Integer.parseInt(profileMap.get(Constants.FOLLOWING_COUNT)) > 0) {
                        openFragment(new FollowingDialogFragment());
                    }
                break;
            case R.id.portfolio_profile_follow:
/*                if (is_follow.equalsIgnoreCase("1")){
                    is_follow = "0";
                }else {
                    is_follow = "1";
                }
                toggleFollow();*/
                new Operations().execute();
                break;
        }
    }

    private class TabPagerAdapter extends FragmentStatePagerAdapter {
        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ImageGalleryFragment().newInstance(friend_id);
                case 1:
                    return new VideoGalleryFragment().newInstance(friend_id);
                case 2:
                    return new AudioGalleryFragment().newInstance(friend_id);
                default:
                    return new ImageGalleryFragment().newInstance(friend_id);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Images";
                case 1:
                    return "Video";
                case 2:
                    return "Audio";
                default:
                    return "Images";
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }

    private class GetPortfolio extends AsyncTask<Void, Void, Integer> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.sendLoadingDialog();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, friend_id)
                    .add(Constants.ACTION, Actions_.GET_PORTFOLIO)
                    .build();
            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.PORTFOLIO_OPERATIONS, requestBody);
                if (response != null) {
                    profileMap = Portfolio_.portfolioFriends(response);
                    return Integer.parseInt(profileMap.get(Constants.STATUS));
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
            showLoader.dismissLoadingDialog();
            if (integer == 1) {
                aboutText.setText(profileMap.get(Constants.ABOUT_ME));
                followerCountText.setText(profileMap.get(Constants.FOLLOWER_COUNT));
                followingCountText.setText(profileMap.get(Constants.FOLLOWING_COUNT));
            } else {
                ShowToast.internalErrorToast(getApplicationContext());
                onBackPressed();
            }
        }
    }

    private class Operations extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 11;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.FOLLOW_ID, friend_id)
                    .add(Constants.ACTION, Actions_.FOLLOW_UNFOLLOW)
                    .build();

            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.FOLLOWER_OPERATIONS, requestBody);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.FOLLOW_UNFOLLOW)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.FOLLOW_UNFOLLOW);
                        JSONObject responseObject = jsonArray.getJSONObject(0);
                        result = responseObject.getInt(Constants.STATUS);
                    }
                } else {
                    result = 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = 12;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 0:
                    ShowToast.actionFailed(getApplicationContext());
                    break;
                case 1:
                    if (is_follow.equalsIgnoreCase("1")){
                        is_follow = "0";
                    }else {
                        is_follow = "1";
                    }
                    toggleFollow();
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
            }
        }
    }

    private void openFragment(DialogFragment openFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(Fragment_.FOLLOWERS);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID, friend_id);
        openFragment.setArguments(bundle);
        openFragment.show(fragmentManager, Fragment_.FOLLOWERS);
    }
}
