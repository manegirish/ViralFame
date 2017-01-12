package com.technoindians.portfolio;

import android.app.ActivityOptions;
import android.app.DialogFragment;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dataappsinfo.viralfame.MainActivity_new;
import com.dataappsinfo.viralfame.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Fragment_;
import com.technoindians.library.BlurBuilder;
import com.technoindians.library.ShowLoader;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Portfolio_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.CircleTransformMain;
import com.technoindians.views.DepthPageTransformer;

import java.io.File;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class PortfolioMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = PortfolioMainActivity.class.getSimpleName();
    protected static PortfolioMainActivity portfolioMainActivity;
    ImageView backButton, profilePhoto;
    TextView nameText, skillText, aboutTag, followerCountText, followingCountText;
    TextView aboutText;
    RelativeLayout profilePicLayout;
    LinearLayout followerButton, followingButton;
    private Toolbar toolbar;
    private ImageView imageView;
    private HashMap<String, String> profileMap;
    private CollapsingToolbarLayout collapsingToolbar;
    private TabPagerAdapter tabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ShowLoader showLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.my_portfolio_fragment_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        portfolioMainActivity = this;

        Preferences.initialize(getApplicationContext());

        showLoader = new ShowLoader(PortfolioMainActivity.this);

        imageView = (ImageView) findViewById(R.id.backdrop);
        profilePhoto = (ImageView) findViewById(R.id.portfolio_profile_photo);
        nameText = (TextView) findViewById(R.id.portfolio_profile_name);
        skillText = (TextView) findViewById(R.id.portfolio_profile_skill);
        aboutTag = (TextView) findViewById(R.id.portfolio_profile_tag);
        aboutTag.setOnClickListener(this);
        aboutText = (TextView) findViewById(R.id.portfolio_profile_about_me);
        followerCountText = (TextView) findViewById(R.id.portfolio_follower_count);
        followingCountText = (TextView) findViewById(R.id.portfolio_following_count);

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
        profilePicLayout.setOnClickListener(this);

        followerButton = (LinearLayout) findViewById(R.id.portfolio_follower_layout);
        followerButton.setOnClickListener(this);
        followingButton = (LinearLayout) findViewById(R.id.portfolio_following_layout);
        followingButton.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(tabPagerAdapter);
        mTabLayout.setTabsFromPagerAdapter(tabPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(0);
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

        mViewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
        MainActivity_new.mainActivity.setHeader();
    }


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setImage() {
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_avatar_square);
        if (new File(Preferences.get(Constants.LOCAL_PATH)).exists()) {
            myBitmap = BitmapFactory.decodeFile(Preferences.get(Constants.LOCAL_PATH));
        }
        Bitmap blurredBitmap = BlurBuilder.blur(PortfolioMainActivity.this, myBitmap);
        imageView.setBackgroundDrawable(new BitmapDrawable(getResources(), blurredBitmap));

        Picasso.with(getApplicationContext())
                .load(new File(Preferences.get(Constants.LOCAL_PATH)))
                .transform(new CircleTransformMain())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(profilePhoto);
        nameText.setText(Preferences.get(Constants.NAME));
        skillText.setText(Preferences.get(Constants.PRIMARY_SKILL));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.portfolio_profile_tag:
                toggleTag();
                break;
            case R.id.portfolio_profile_pic:
                Intent cropIntent = new Intent(getApplicationContext(), CropImageActivity.class);
                Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                        (getApplicationContext(), R.anim.animation_one,R.anim.animation_two).toBundle();
                startActivity(cropIntent,nextAnimation);
                break;
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
        }
    }

    private void toggleTag() {
        openDialog(aboutText.getText().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetPortfolio().execute();
        setImage();
    }

    public void changeDp() {
/*
        profilePhoto.invalidate();
        imageView.invalidate();
        profilePhoto.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);*/

        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_avatar_square);
        if (new File(Preferences.get(Constants.LOCAL_PATH)).exists()) {
            myBitmap = BitmapFactory.decodeFile(Preferences.get(Constants.LOCAL_PATH));
        }
        Bitmap blurredBitmap = BlurBuilder.blur(PortfolioMainActivity.this, myBitmap);
        imageView.setBackgroundDrawable(new BitmapDrawable(getResources(), blurredBitmap));

        Picasso.with(getApplicationContext())
                .load(new File(Preferences.get(Constants.LOCAL_PATH)))
                .transform(new CircleTransformMain())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(profilePhoto);

    }

    private void openFragment(DialogFragment openFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(Fragment_.FOLLOWERS);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        openFragment.show(fragmentManager, Fragment_.FOLLOWERS);
    }

    private void openDialog(String about_me) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(Constants.ABOUT_ME);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        AboutMeFragment detailsFragment = new AboutMeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ABOUT_ME, about_me);
        detailsFragment.setArguments(bundle);
        detailsFragment.show(fragmentManager, Constants.ABOUT_ME);
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setAboutMe(String about) {
        aboutText.setText(about);
    }

    private class TabPagerAdapter extends FragmentStatePagerAdapter {
        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ImageGalleryFragment().newInstance(Preferences.get(Constants.USER_ID));
                case 1:
                    return new VideoGalleryFragment().newInstance(Preferences.get(Constants.USER_ID));
                case 2:
                    return new AudioGalleryFragment().newInstance(Preferences.get(Constants.USER_ID));
                default:
                    return new ImageGalleryFragment().newInstance(Preferences.get(Constants.USER_ID));
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
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ACTION, Actions_.GET_PORTFOLIO)
                    .build();
            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.PORTFOLIO_OPERATIONS, requestBody,TAG);
                if (response != null) {
                    result = Portfolio_.portfolioResult(response);
                } else {
                    result = 12;
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
            switch (integer) {
                case 0:
                    showLoader.dismissLoadingDialog();
                    ShowToast.noData(getApplicationContext());
                    onBackPressed();
                    break;
                case 1:
                    new LoadPortfolio().execute(response);
                    break;
                case 2:
                    showLoader.dismissLoadingDialog();
                    ShowToast.noData(getApplicationContext());
                    onBackPressed();
                    break;
                case 11:
                    showLoader.dismissLoadingDialog();
                    ShowToast.internalErrorToast(getApplicationContext());
                    onBackPressed();
                    break;
                case 12:
                    showLoader.dismissLoadingDialog();
                    ShowToast.networkProblemToast(getApplicationContext());
                    onBackPressed();
                    break;
            }
        }
    }

    private class LoadPortfolio extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            profileMap = Portfolio_.portfolioFriends(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showLoader.dismissLoadingDialog();
            aboutText.setText(profileMap.get(Constants.ABOUT_ME));
            followerCountText.setText(profileMap.get(Constants.FOLLOWER_COUNT));
            followingCountText.setText(profileMap.get(Constants.FOLLOWING_COUNT));
        }
    }
}
