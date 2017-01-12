
/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.dataappsinfo.viralfame;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.technoindians.constants.Constants;
import com.technoindians.network.CheckInternet;
import com.technoindians.network.ConnectivityReceiver;
import com.technoindians.pops.ShowSnack;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * @author Girish M
 *         Created on 21/6/16.
 *         Last modified 01/08/2016
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    TextView loginButton,registerButton;
    RelativeLayout facebookButton,googleButton;
    CallbackManager callbackManager;
    AccessToken accessToken;
    Profile profile;
    private GoogleApiClient mGoogleApiClient;
    private LoginManager loginManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView tagText = (TextView)findViewById(R.id.login_tag_line);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),
                "century_gothic.ttf");
        tagText.setTypeface(tf);
        loginButton = (TextView) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        registerButton = (TextView) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        facebookButton = (RelativeLayout) findViewById(R.id.login_facebook);
        facebookButton.setOnClickListener(this);

        googleButton = (RelativeLayout) findViewById(R.id.login_google);
        googleButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "onSuccess : " + loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    getFacebookProfile(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,photos,gender,location,verified");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel : ");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "onError : " + e.toString());
            }
        });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (oldToken != null) {
                    Log.e(TAG, "onCurrentAccessTokenChanged" + ", oldToken: " + oldToken.getToken() + ", newToken: " + newToken);
                }
                accessToken = newToken;
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                // displayMessage(newProfile);
                profile = newProfile;
                Log.e(TAG, "onCurrentProfileChanged");
            }
        };

        profile = Profile.getCurrentProfile();
        accessToken = AccessToken.getCurrentAccessToken();
    }

    private void getFacebookProfile(JSONObject object) throws JSONException {
        String first_name = profile.getFirstName();
        String last_name = profile.getLastName();
        String fb_id = profile.getId();

        String fb_pic = profile.getProfilePictureUri(256, 256).toString();
        String location = object.getString("location");
        String email = object.getString(Constants.EMAIL);
        String gender = object.getString(Constants.GENDER);
        JSONObject jsonObject = new JSONObject(location);
        Preferences.save(Constants.FIRST_NAME, first_name);
        Preferences.save(Constants.LAST_NAME, last_name);
        Preferences.save(Constants.EMAIL, email);
        Preferences.save(Constants.GENDER, gender);
        Preferences.save(Constants.PROFILE_PHOTO, fb_pic);
        Preferences.save(Constants.CITY, jsonObject.getString("name"));
        Preferences.save(Constants.FACEBOOK, fb_id);
        Log.e(TAG, "object: " + object);
        registerFragment("register");
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    public void registerFragment(String tag) {
        Fragment f = getFragmentManager().findFragmentByTag(tag);
        if (f != null) {
            getFragmentManager().popBackStack();
        } else {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.login_container, RegisterFragment.instantiate(this, RegisterFragment.class.getName()), tag).addToBackStack(null).commit();
        }
    }

    public void replaceFragment(String tag) {
        Fragment f = getFragmentManager().findFragmentByTag(tag);
        if (f != null) {
            getFragmentManager().popBackStack();
        } else {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.login_container, LoginFragment.instantiate(this,
                            LoginFragment.class.getName()), tag).addToBackStack(null).commit();
        }
    }


    @Override
    public void onClick(View v) {
        if (CheckInternet.check()) {
            switch (v.getId()) {
                case R.id.login_button:
                    replaceFragment("login");
                    break;
                case R.id.register_button:
                    registerFragment("register");
                    break;
                case R.id.login_google:
                    signIn();
                    break;
                case R.id.login_facebook:
                    facebookLogin();
                    break;
            }
        } else {
            ShowSnack.noInternet(v);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.e(TAG, "signOut()" + status);
                        //   updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String id = acct.getId();
            String getFamilyName = acct.getFamilyName();
            String getGivenName = acct.getGivenName();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl + ", id:" + id + ", getFamilyName: "
                    + getFamilyName + ", getGivenName:" + getGivenName + ", IdToken: " + acct.getIdToken());
/*

            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);
*/

            //    updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //   updateUI(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            if (!mGoogleApiClient.isConnecting()) {
                if (mGoogleApiClient.hasConnectedApi(Plus.API)) {
                    Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    Log.i(TAG, "--------------------------------" + person.getUrls());
                    Log.e(TAG, "Name: " + person.getName());
                    Log.e(TAG, "Display Name: " + person.getDisplayName());
                    Log.e(TAG, "Gender: " + person.getGender());
                    Log.e(TAG, "AboutMe: " + person.getAboutMe());
                    Log.e(TAG, "Birthday: " + person.getBirthday());
                    Log.e(TAG, "Places Lived Location: " + person.getPlacesLived());
                    Log.e(TAG, "Current Location: " + person.getCurrentLocation());
                    Log.e(TAG, "Language: " + person.getLanguage());
                } else {
                    ShowToast.toast(getApplicationContext(), "Failed to Connect");
                }
            } else {
                ShowToast.toast(getApplicationContext(), "Failed to Connect");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.e(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    // showLoader.dismissLoadingDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void facebookLogin() {
        loginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile", "user_birthday", "user_location"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViralFame.getInstance().setConnectivityListener(this);
        if (!CheckInternet.check())
            ShowToast.noNetwork(getApplicationContext());
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected)
            ShowToast.noNetwork(getApplicationContext());
    }
}