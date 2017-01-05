package com.technoindians.wall;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.library.CheckFileType;
import com.technoindians.library.FileCheck;
import com.technoindians.library.ShowLoader;
import com.technoindians.library.UploadFeedFile;
import com.technoindians.library.Utils;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.SoftKeyboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import technoindians.key.emoji.SmileyKeyBoard;
import technoindians.key.emoji.adapter.EmojiGridviewImageAdapter;

/**
 * Created by girish on 18/7/16.
 */

public class TimelineMessagePostActivity extends Activity implements View.OnClickListener,
        EmojiGridviewImageAdapter.EmojiClickInterface {

    private static final String TAG = TimelineMessagePostActivity.class.getSimpleName();

    private String fileUrl = null;
    private String uploadUrl = "";
    private int postType = 0;
    private int messageType = 0;
    private TextView postButton, audioName, audioSize, audioTime, sendButton;
    private ImageView backButton, smileyButton, thumbnail, removeButton;
    private EditText messageBox;
    private RelativeLayout mediaLayout;
    private Spinner spinner;
    private LinearLayout galleryButton, cameraButton, videoButton, audioButton, audioLayout, iconFooter, footerLayout;
    private boolean isKeyboard = false;
    private RelativeLayout chatFooter;
    private ShowLoader showLoader;
    private SmileyKeyBoard smileyKeyBoard;
    private ValueAnimator valueAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.create_wall_post);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        showLoader = new ShowLoader(TimelineMessagePostActivity.this);

        postButton = (TextView) findViewById(R.id.create_wall_post_button);
        postButton.setOnClickListener(this);

        backButton = (ImageView) findViewById(R.id.create_wall_post_back);
        backButton.setOnClickListener(this);

        messageBox = (EditText) findViewById(R.id.create_wall_post_box);

        iconFooter = (LinearLayout) findViewById(R.id.create_wall_icon_footer);
        iconFooter.setVisibility(View.GONE);
        iconFooter.setOnClickListener(this);


        footerLayout = (LinearLayout) findViewById(R.id.create_wall_list_footer);
        footerLayout.setVisibility(View.VISIBLE);
        footerLayout.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        footerLayout.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        footerLayout.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(
                                0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec
                                .makeMeasureSpec(0,
                                        View.MeasureSpec.UNSPECIFIED);
                        footerLayout.measure(widthSpec, heightSpec);

                        valueAnimator = slideAnimator(0,
                                footerLayout.getMeasuredHeight());
                        return true;
                    }
                });

        galleryButton = (LinearLayout) findViewById(R.id.create_wall_footer_gallery);
        galleryButton.setOnClickListener(this);
        cameraButton = (LinearLayout) findViewById(R.id.create_wall_footer_camera);
        cameraButton.setOnClickListener(this);
        videoButton = (LinearLayout) findViewById(R.id.create_wall_footer_video);
        videoButton.setOnClickListener(this);
        audioButton = (LinearLayout) findViewById(R.id.create_wall_footer_audio);
        audioButton.setOnClickListener(this);
        audioLayout = (LinearLayout) findViewById(R.id.create_wall_post_media_audio);
        audioLayout.setVisibility(View.GONE);
        mediaLayout = (RelativeLayout) findViewById(R.id.create_wall_post_media_layout);
        mediaLayout.setVisibility(View.GONE);

        thumbnail = (ImageView) findViewById(R.id.create_wall_post_media_audio_icon);
        audioName = (TextView) findViewById(R.id.create_wall_post_media_audio_name);
        audioSize = (TextView) findViewById(R.id.create_wall_post_media_audio_size);
        audioTime = (TextView) findViewById(R.id.create_wall_post_media_audio_duration);

        removeButton = (ImageView) findViewById(R.id.create_wall_post_media_remove);
        removeButton.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.wall_create_privacy_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.privacy_spinner_item
                , getResources().getStringArray(R.array.privacy_arrays));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                postType = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                postType = 0;
            }
        });

        smileyButton = (ImageView) findViewById(R.id.create_wall_post_smiley);
        smileyButton.setOnClickListener(this);

        smileyKeyBoard = new SmileyKeyBoard();
        smileyKeyBoard.enable(this, this, R.id.footer_for_emoticons, messageBox);
        chatFooter = (RelativeLayout) findViewById(R.id.relativeBottomArea);
        smileyKeyBoard.checkKeyboardHeight(chatFooter);
        smileyKeyBoard.enableFooterView(messageBox);

        softKey();
        setFooter();
    }

    private void expand() {
        footerLayout.setVisibility(View.VISIBLE);
        valueAnimator.start();
    }

    private void collapse() {
        int finalHeight = footerLayout.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                // Height=0, but it set visibility to GONE
                footerLayout.setVisibility(View.GONE);
                iconFooter.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = footerLayout
                        .getLayoutParams();
                layoutParams.height = value;
                footerLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void setFooter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isKeyboard) {
                    iconFooter.setVisibility(View.VISIBLE);
                    footerLayout.setVisibility(View.GONE);
                } else {
                    iconFooter.setVisibility(View.GONE);
                    footerLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void softKey() {
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.create_wall_post_layout); // You must use your root layout
        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard;
        softKeyboard = new SoftKeyboard(mainLayout, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                Log.e(TAG, "onSoftKeyboardHide()");
                isKeyboard = false;
                setFooter();
            }

            @Override
            public void onSoftKeyboardShow() {
                Log.e(TAG, "onSoftKeyboardShow()");
                isKeyboard = true;
                setFooter();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_wall_footer:

                break;
            case R.id.create_wall_icon_footer:
                smileyKeyBoard.dismissKeyboard();
                if (isKeyboard) {
                    hideKeyboard();
                }
                if (footerLayout.getVisibility() == View.GONE) {
                    expand();
                } else {
                    collapse();
                }
                break;
            case R.id.create_wall_post_button:
                String message = messageBox.getText().toString().trim();
                if (message == null || message.length() <= 3) {
                    messageBox.setError("Invalid Message \n min char 3");
                } else {
                    new MakePost(message, uploadUrl, "" + messageType, "", "" + postType).execute();
                }
                break;
            case R.id.create_wall_post_back:
                onBackPressed();
                break;
            case R.id.create_wall_post_media_remove:
                mediaLayout.setVisibility(View.GONE);
                audioLayout.setVisibility(View.GONE);
                messageType = Constants.INTENT_MESSAGE;
                fileUrl = null;
                uploadUrl = "";
                postType = 0;
                messageType = 0;

                break;
            case R.id.create_wall_footer_gallery:
                if (Build.VERSION.SDK_INT < 19) {
                    Intent intent = new Intent();
                    intent.setType("image/jpeg");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constants.INTENT_IMAGE);
                } else {
                    Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imageIntent.setType("image/*");
                    startActivityForResult(imageIntent, Constants.INTENT_IMAGE);
                }
                break;
            case R.id.create_wall_footer_camera:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUrl = Utils.getOutputMediaFile(1);
                Uri uriSavedImage = Uri.fromFile(new File(fileUrl));
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                cameraIntent.putExtra(Constants.NAME, fileUrl);
                startActivityForResult(cameraIntent, Constants.INTENT_CAMERA);
                break;
            case R.id.create_wall_footer_video:
                Intent videoIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                videoIntent.setType("*/*");
                startActivityForResult(videoIntent, Constants.INTENT_VIDEO);
                break;
            case R.id.create_wall_footer_audio:
                Intent musicIntent = new Intent(Intent.ACTION_GET_CONTENT);
                musicIntent.setType("*/*");
                startActivityForResult(musicIntent, Constants.INTENT_AUDIO);
                break;
            case R.id.create_wall_post_smiley:
                smileyKeyBoard.showKeyboard(chatFooter);
                break;
        }
    }

    private class Upload extends AsyncTask<Void, Void, Integer> {
        String filePath;
        int intentType;
        int file_size;

        public Upload(String filePath, int intentType, int file_size) {
            this.filePath = filePath;
            this.intentType = intentType;
            this.file_size = file_size;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.showUploadingDialog();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            HashMap<String, String> responseMap = UploadFeedFile.uploadFile(filePath, Preferences.get(Constants.USER_ID));
            if (responseMap != null) {
                //{"profile_pic":[{"path":"1471093109.VID-20160813-WA0003.mp4","success":"File uploaded successfully.","status":1}]}
                try {
                    JSONObject jsonObject = new JSONObject(responseMap.get(Constants.RESPONSE));
                    if (jsonObject.has(JsonArrays_.PROFILE_PIC)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.PROFILE_PIC);
                        JSONObject urlObject = jsonArray.getJSONObject(0);
                        uploadUrl = urlObject.getString(Constants.PATH);
                    } else {
                        return 11;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return 11;
                }
                return Integer.parseInt(responseMap.get(Constants.STATUS));
            } else {
                return 12;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            showLoader.dismissUploadingDialog();
            if (integer == 1) {
                mediaLayout.setVisibility(View.VISIBLE);
                audioLayout.setVisibility(View.VISIBLE);

                audioName.setText(FileCheck.getFileName(filePath));
                audioSize.setText(FileCheck.size(file_size));

                switch (intentType) {
                    case Constants.INTENT_IMAGE:
                        messageType = Constants.INTENT_IMAGE;
                        audioTime.setVisibility(View.GONE);
                        Picasso.with(getApplicationContext())
                                .load(new File(filePath))
                                .resize(180, 150)
                                .centerCrop()
                                .placeholder(R.drawable.ic_dummy_wall)
                                .error(R.drawable.ic_dummy_wall)
                                .into(thumbnail);
                        break;

                    case Constants.INTENT_VIDEO:
                        messageType = Constants.INTENT_VIDEO;
                        audioTime.setVisibility(View.VISIBLE);
                        audioTime.setText(FileCheck.duration(filePath));
                        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmThumbnail, 180, 150, false);
                        thumbnail.setImageBitmap(resizedBitmap);
                        break;

                    case Constants.INTENT_AUDIO:
                        messageType = Constants.INTENT_AUDIO;
                        audioTime.setVisibility(View.VISIBLE);
                        audioTime.setText(FileCheck.duration(filePath));
                        thumbnail.setImageResource(R.drawable.ic_audio_y);
                        break;

                    case Constants.INTENT_CAMERA:
                        messageType = Constants.INTENT_IMAGE;

                        audioTime.setVisibility(View.GONE);
                        Picasso.with(getApplicationContext())
                                .load(new File(filePath))
                                .placeholder(R.drawable.ic_dummy_wall)
                                .error(R.drawable.ic_dummy_wall)
                                .resize(100, 100)
                                .centerCrop()
                                .into(thumbnail);
                        break;
                }
            } else {
                ShowToast.toast(getApplicationContext(), "Upload Failed");
            }
        }
    }

    private void uploadFile(String filePath, int intentType) {
        File file = new File(filePath);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));

        if (FileCheck.size(file_size) == null) {
            ShowToast.toast(getApplicationContext(), "Max file size allowed 25 MB");
        } else {
            new Upload(filePath, intentType, file_size).execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.INTENT_IMAGE:
                    String imagePath = Utils.getPath(getApplicationContext(), data.getData());
                    if (CheckFileType.imageFile(imagePath) == 1) {
                        uploadFile(imagePath, Constants.INTENT_IMAGE);
                    } else {
                        ShowToast.toast(getApplicationContext(), "Please Select Valid Image File");
                    }
                    break;
                case Constants.INTENT_VIDEO:
                    String videoPath = Utils.getPath(getApplicationContext(), data.getData());
                    if (CheckFileType.videoFile(videoPath) == 1) {
                        uploadFile(videoPath, Constants.INTENT_VIDEO);
                    } else {
                        ShowToast.toast(getApplicationContext(), "Please Select Valid Video File");
                    }
                    break;

                case Constants.INTENT_AUDIO:
                    String audioPath = Utils.getPath(getApplicationContext(), data.getData());

                    if (CheckFileType.audioFile(audioPath) == 1) {
                        uploadFile(audioPath, Constants.INTENT_AUDIO);
                    } else {
                        ShowToast.toast(getApplicationContext(), "Please Select Valid Audio File");
                    }
                    break;
                case Constants.INTENT_CAMERA:
                    if (fileUrl != null) {
                        uploadFile(fileUrl, Constants.INTENT_CAMERA);
                    }
                    break;
            }
        }
    }


    @Override
    public void getClickedEmoji(int gridviewItemPosition) {
        smileyKeyBoard.getClickedEmoji(gridviewItemPosition);
    }

    private class MakePost extends AsyncTask<Void, Void, Integer> {

        String post_text, media_file, media_type, shared_to, post_type;

        public MakePost(String post_text, String media_file, String media_type,
                        String shared_to, String post_type) {
            this.post_text = post_text;
            this.media_file = media_file;
            this.media_type = media_type;
            this.shared_to = shared_to;
            this.post_type = post_type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.showPostingDialog();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ACTION, Actions_.ADD_POST)
                    .add(Constants.POST_TEXT, post_text)
                    .add(Constants.MEDIA_FILE, media_file)
                    .add(Constants.MEDIA_TYPE, media_type)
                    .add(Constants.SHARED_TO, shared_to)
                    .add(Constants.POST_TYPE, post_type)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL, requestBody);
                //{"add_post":{"msg":"Posted Successfully","status":1}}
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.ADD_POST);
                    return responseObject.getInt(Constants.STATUS);
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
                    ShowToast.actionFailed(getApplicationContext());
                    break;
                case 1:
                    messageBox.setText("");
                    mediaLayout.setVisibility(View.GONE);
                    audioLayout.setVisibility(View.GONE);
                    messageType = Constants.INTENT_MESSAGE;
                    fileUrl = null;
                    uploadUrl = "";
                    postType = 0;
                    messageType = 0;

                    ShowToast.successful(getApplicationContext());
                    Intent i = new Intent();
                    i.putExtra(Constants.IS_REFRESH, "123456");
                    setResult(RESULT_OK, i);
                    finish();
                    //onBackPressed();
                    break;
                case 2:
                    ShowToast.actionFailed(getApplicationContext());
                    break;
                case 11:
                    ShowToast.internalErrorToast(getApplicationContext());
                    break;
                case 12:
                    ShowToast.networkProblemToast(getApplicationContext());
                    break;
            }
            showLoader.dismissPostingDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (smileyKeyBoard.isKeyboardVisibile()) {
            smileyKeyBoard.dismissKeyboard();
            return;
        }
        if (isKeyboard) {
            hideKeyboard();
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
    }

    private void hideKeyboard() {
        isKeyboard = false;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
