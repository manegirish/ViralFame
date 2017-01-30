package com.technoindians.portfolio;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.dataappsinfo.viralfame.R;
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.database.TableList;
import com.technoindians.database.UpdateOperations;
import com.technoindians.directory.DirectoryList;
import com.technoindians.library.CheckUserType;
import com.technoindians.library.FileCheck;
import com.technoindians.library.ShowLoader;
import com.technoindians.library.UrlShorten;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.wall.WallCommentDialogFragment;
import com.technoindians.wall.WallOperations_;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by girish on 12/8/16.
 */

public class FeedDetailsActivity extends Activity implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener {

    private String TAG = FeedDetailsActivity.class.getSimpleName();
    private String media_path = null;
    private int media_type;
    private String wall_id;
    private TextView like, comment;
    private EmojiconTextView tagText;
    ImageView backButton, menuButton;
    RelativeLayout audioLayout, videoLayout;

    private ImageView feedImage, playButton, stopButton;
    private int is_like = 0, likeCount = 0, commentCount = 0;
    private ShowLoader showLoader;
    private boolean isPlaying = false, isDelete = false;
    private int duration;
    private SeekBar seekBarProgress;
    private FragmentManager fragmentManager;

    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    static MediaPlayer mPlayer;
    private final Handler handler = new Handler();
    VideoView vidView;
    PopupWindow changeStatusPopUp;
    Point p;

    DownloadStatusListenerV1 downloadStatusListenerV1 = new DownloadStatusListenerV1() {
        @Override
        public void onDownloadComplete(DownloadRequest downloadRequest) {
            ShowToast.toast(getApplicationContext(), "Download finished");
        }

        @Override
        public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {

        }

        @Override
        public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
            Log.e("onProgress", "downloadedBytes -> " + downloadedBytes + " totalBytes -> " + totalBytes + " progress -> " + progress);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_details_layout);

        showLoader = new ShowLoader(FeedDetailsActivity.this);
        fragmentManager = getFragmentManager();

        Preferences.initialize(getApplicationContext());

        Intent intent = getIntent();
        if (intent != null) {
            media_path = intent.getStringExtra(Constants.MEDIA_FILE);
            media_type = Integer.parseInt(intent.getStringExtra(Constants.MEDIA_TYPE));
            wall_id = intent.getStringExtra(Constants.WALL_ID);
            if (intent.hasExtra(Constants.USER_ID)) {
                if (intent.getStringExtra(Constants.USER_ID).equalsIgnoreCase(Preferences.get(Constants.USER_ID))) {
                    isDelete = true;
                }
            }
        }

        feedImage = (ImageView) findViewById(R.id.feed_details_image);
        tagText = (EmojiconTextView) findViewById(R.id.feed_details_tag);
        vidView = (VideoView) findViewById(R.id.myVideo);
        like = (TextView) findViewById(R.id.feed_details_like);
        like.setOnClickListener(this);
        comment = (TextView) findViewById(R.id.feed_details_comment);
        comment.setOnClickListener(this);
        backButton = (ImageView) findViewById(R.id.feed_details_back);
        backButton.setOnClickListener(this);
        menuButton = (ImageView) findViewById(R.id.feed_details_menu);
        menuButton.setOnClickListener(this);
        playButton = (ImageView) findViewById(R.id.feed_details_audio_play);
        playButton.setOnClickListener(this);
        stopButton = (ImageView) findViewById(R.id.feed_details_audio_stop);
        stopButton.setOnClickListener(this);

        seekBarProgress = (SeekBar) findViewById(R.id.feed_details_audio_seek);

        audioLayout = (RelativeLayout) findViewById(R.id.feed_details_audio_layout);
        videoLayout = (RelativeLayout) findViewById(R.id.feed_details_video_layout);

        if (media_type == Constants.INTENT_AUDIO) {
            feedImage.setVisibility(View.GONE);
            audioLayout.setVisibility(View.VISIBLE);
            videoLayout.setVisibility(View.GONE);
        }
        if (media_type == Constants.INTENT_VIDEO) {
            feedImage.setVisibility(View.GONE);
            audioLayout.setVisibility(View.GONE);
            videoLayout.setVisibility(View.VISIBLE);
        }
        if (media_type == Constants.INTENT_IMAGE) {
            feedImage.setVisibility(View.VISIBLE);
            audioLayout.setVisibility(View.GONE);
            videoLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (media_type == Constants.INTENT_AUDIO) {
            preparePlayer();
        }
        if (media_type == Constants.INTENT_VIDEO) {
            prepareVideoPlayer();
        }
        if (media_type == Constants.INTENT_IMAGE) {
            loadImage();
        }
        new GetFeed().execute();
    }

    private void loadImage() {
        Picasso.with(getApplicationContext())
                .load(Urls.DOMAIN + media_path)
                .placeholder(R.drawable.ic_image_place_holder)
                .error(R.drawable.ic_image_place_holder)
                .into(feedImage);
    }

    private void loadData(String postText, int commentCount) {
        tagText.setText(postText);
        like.setTextColor(getApplicationContext().getResources()
                .getColor(R.color.white));
        if (likeCount > 0) {
            like.setText("" + likeCount + "");
        }

        if (commentCount > 0) {
            comment.setText("" + commentCount + "");
            comment.setTextColor(getApplicationContext().getResources()
                    .getColor(R.color.white));
            comment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_p, 0, 0, 0);
        } else {
            comment.setText("");
            comment.setTextColor(getApplicationContext().getResources()
                    .getColor(R.color.black_65));
            comment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_g, 0, 0, 0);
        }

        if (is_like == 1) {
            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_p, 0, 0, 0);
        } else {
            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_g, 0, 0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!removeDialog()) {
            overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
            finish();
        }
    }

    private boolean removeDialog() {
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.COMMENT);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
            return true;
        }
        return false;
    }

    public void spamFeed(View view) {
        deleteConfirmation(3);
    }

    public void deleteFeed(View view) {
        if (isDelete) {
            deleteConfirmation(3);
        } else {

            deleteConfirmation(2);
        }
    }

    private void deleteConfirmation(final int type) {
        //1-delete;2-remove;3-spam
        final UpdateOperations updateOperations = new UpdateOperations(getApplicationContext());
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title, warning;
        Button fwdAccept, fwdDecline;
        title = (TextView) dialog.findViewById(R.id.confirmation_dialog_title);
        if (type == 1) {
            title.setText("Delete Timeline Post!");
        }
        if (type == 2) {
            title.setText("Remove Timeline Post!");
        }
        if (type == 3) {
            title.setText("Spam Timeline Post!");
        }
        warning = (TextView) dialog.findViewById(R.id.confirmation_dialog_description);
        if (type == 1) {
            warning.setText("Are you sure to delete timeline post?");
        }
        if (type == 2) {
            warning.setText("Are you sure to remove timeline post?");
        }
        if (type == 3) {
            warning.setText("Are you sure to spam timeline post?");
        }

        fwdAccept = (Button) dialog.findViewById(R.id.confirmation_dialog_ok_button);
        final EditText numberBox = (EditText) dialog.findViewById(R.id.confirmation_dialog_number_box);
        numberBox.setVisibility(View.GONE);

        fwdAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    int result = WallOperations_.delete(wall_id);
                    if (result == 1) {
                        updateOperations.deleteRecord(TableList.TABLE_WALL_FEED, Constants._ID, wall_id);
                        onBackPressed();
                    }
                }
                if (type == 2) {
                    WallOperations_.remove(wall_id);
                    updateOperations.deleteRecord(TableList.TABLE_WALL_FEED, Constants._ID, wall_id);
                    onBackPressed();
                }
                if (type == 3) {
                    int spam_result = WallOperations_.spam(wall_id);
                    if (spam_result == 1) {
                        updateOperations.deleteRecord(TableList.TABLE_WALL_FEED, Constants._ID, wall_id);
                        onBackPressed();
                    }
                }
                dialog.dismiss();

            }
        });

        fwdDecline = (Button) dialog.findViewById(R.id.confirmation_dialog_cancel_button);
        fwdDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void openCommentDialog() {
        removeDialog();
        WallCommentDialogFragment detailsFragment = new WallCommentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ID, wall_id);
        detailsFragment.setArguments(bundle);
        detailsFragment.show(fragmentManager, Constants.COMMENT);
    }

    private void toggleLike(int type) {
        like.invalidate();
        Log.e("toggleLike before", "is_like -> " + is_like + " likeCount -> " + likeCount + "type -> " + type);
        if (is_like == 1) {
            likeCount = (likeCount - 1);
            is_like = type;
            like.setText("");
            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_g, 0, 0, 0);
        } else {
            likeCount = (likeCount + 1);
            is_like = type;
            like.setTextColor(getApplicationContext().getResources()
                    .getColor(R.color.black_65));
            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_p, 0, 0, 0);
        }
        if (likeCount > 0) {
            like.setText("" + likeCount + "");
            like.setTextColor(getApplicationContext().getResources()
                    .getColor(R.color.white));
        }
    }

    private void showStatusPopup(final Activity context, Point p) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.feed_details_menu, null);
        if (isDelete) {
            TextView removeText = (TextView) layout.findViewById(R.id.feed_details_menu_remove_text);
            removeText.setText(getApplicationContext().getResources().getString(R.string.delete));
            ImageView removeIcon = (ImageView) layout.findViewById(R.id.feed_details_menu_remove_icon);
            removeIcon.setImageResource(R.drawable.ic_delete_ca_g);

            LinearLayout spamLayout = (LinearLayout) layout.findViewById(R.id.feed_details_menu_spam);
            spamLayout.setVisibility(View.GONE);
        }
        // Creating the PopupWindow
        changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);
        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 60;
        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());
        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

    }

    public void saveFile(View view) {
        if (CheckUserType.isGuest()) {
            ShowToast.toast(getApplicationContext(), Warnings.GUEST_LOGIN);
            return;
        }
        String url = Urls.DOMAIN + media_path;
        String file_name = FileCheck.getFileName(media_path);
        if (!new File(DirectoryList.DIR_MAIN + DirectoryList.DIR_SHARED_FILES).exists()) {
            new File(DirectoryList.DIR_MAIN + DirectoryList.DIR_SHARED_FILES).mkdir();
        }
        String destination = DirectoryList.DIR_MAIN + DirectoryList.DIR_SHARED_FILES + file_name;
        //Log.e("saveFile", " url -> " + url + " file_name -> " + file_name + " destination -> " + destination);
        changeStatusPopUp.dismiss();
        throwDownload(url, destination, "" + SystemClock.currentThreadTimeMillis());
        ShowToast.toast(getApplicationContext(), "Downloading....");
    }

    public void copyLink(View view) {
        if (CheckUserType.isGuest()) {
            ShowToast.toast(getApplicationContext(), Warnings.GUEST_LOGIN);
            return;
        }
        String url = Urls.DOMAIN + media_path;
        Log.e("copyLink", " url -> " + url + "\n UrlShorten -> " + UrlShorten.get(url));
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(Constants.MEDIA_FILE, UrlShorten.get(url));
        clipboard.setPrimaryClip(clip);
        changeStatusPopUp.dismiss();
        ShowToast.toast(getApplicationContext(), "Copied");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        int[] location = new int[2];
        ImageView button = (ImageView) findViewById(R.id.feed_details_menu);
        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        button.getLocationOnScreen(location);
        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    private void throwDownload(String path, String destination, String context) {

        RetryPolicy retryPolicy = new DefaultRetryPolicy();
        ThinDownloadManager downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
        DownloadRequest downloadRequest = new DownloadRequest(Uri.parse(path))
                .setRetryPolicy(retryPolicy)
                .setDestinationURI(Uri.parse(destination))
                .setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext(context)
                .setDeleteDestinationFileOnFailure(true)
                .setStatusListener(downloadStatusListenerV1);
        downloadManager.add(downloadRequest);
    }

    private void primarySeekBarProgressUpdater() {
        Log.e("Seek Progress Update", "duration => " + duration);
        if (mPlayer != null) {
            seekBarProgress.setProgress((int) (((float) mPlayer.getCurrentPosition() / duration) * 100)); // This math construction give a percentage of "was playing"/"song length"
            if (mPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                handler.postDelayed(notification, 100);
            }
        }
    }

    private void prepareVideoPlayer() {
        Log.e("prepareVideoPlayer()", "url => " + Urls.DOMAIN + media_path);
        Uri vidUri = Uri.parse(Urls.DOMAIN + media_path);
        vidView.setVideoURI(vidUri);
        MediaController vidControl = new MediaController(this);
        vidView.setMediaController(vidControl);
        vidControl.setAnchorView(vidView);
        vidControl.requestFocus();
        vidView.start();
    }

    private void preparePlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        seekBarProgress.setProgress(0);
        try {
            mPlayer.setDataSource(Urls.DOMAIN + media_path);
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            ShowToast.toast(getApplicationContext(), "You might not set the URI correctly!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mPlayer.prepare();
        } catch (IllegalStateException | IOException e) {
            ShowToast.toast(getApplicationContext(), "You might not set the URI correctly!");
        }
        duration = mPlayer.getDuration();
    }

    private void startPlayer() {
        isPlaying = true;
        stopButton.setVisibility(View.VISIBLE);
        mPlayer.start();
        primarySeekBarProgressUpdater();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void stopPlayer() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            seekBarProgress.setProgress(0);
            isPlaying = false;
            stopButton.setVisibility(View.GONE);
            playButton.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && mPlayer.isPlaying()) {
            pausePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null && mPlayer.isPlaying()) {
            startPlayer();
        }
    }

    private void pausePlayer() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            stopButton.setVisibility(View.VISIBLE);
            mPlayer.pause();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBarProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlayer();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPlaying = true;
        startPlayer();
        playButton.setImageResource(R.drawable.ic_pause);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_details_back:
                stopPlayer();
                onBackPressed();
                break;

            case R.id.feed_details_like:
                new Operations().execute();
                break;

            case R.id.feed_details_comment:
                stopPlayer();
                openCommentDialog();
                break;
            case R.id.feed_details_menu:
                if (p != null) {
                    showStatusPopup(FeedDetailsActivity.this, p);
                }
                break;
            case R.id.feed_details_audio_play:
                if (!isPlaying) {
                    isPlaying = true;
                    if (mPlayer == null) {
                        preparePlayer();
                    }
                    startPlayer();
                    playButton.setImageResource(R.drawable.ic_pause);
                } else {
                    isPlaying = false;
                    pausePlayer();
                    playButton.setImageResource(R.drawable.ic_play);
                }
                break;
            case R.id.feed_details_audio_stop:
                stopPlayer();
                break;
        }
    }

    private class GetFeed extends AsyncTask<Void, Void, Integer> {
        String post_text;

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
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.ACTION, Actions_.POST_DETAILS)
                    .add(Constants.ID, wall_id)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL, requestBody, TAG);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.POST_DETAILS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(JsonArrays_.POST_DETAILS);
                        JSONObject detailsObject = jsonArray.getJSONObject(0);
                        result = detailsObject.getInt(Constants.STATUS);
                        if (result == 1) {
                            likeCount = detailsObject.getInt(Constants.TOTAL_LIKES);
                            commentCount = detailsObject.getInt(Constants.TOTAL_COMMENTS);
                            is_like = detailsObject.getInt(Constants.IS_LIKE);
                            post_text = detailsObject.getString(Constants.POST_TEXT);
                        }
                    } else {
                        result = 11;
                    }
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
            switch (integer) {
                case 0:
                    ShowToast.internalErrorToast(getApplicationContext());
                    onBackPressed();
                    break;
                case 1:
                    loadData(post_text, commentCount);
                    break;
                case 2:
                    ShowToast.noData(getApplicationContext());
                    onBackPressed();
                    break;
                case 11:
                    ShowToast.internalErrorToast(getApplicationContext());
                    onBackPressed();
                    break;
                case 12:
                    ShowToast.networkProblemToast(getApplicationContext());
                    onBackPressed();
                    break;
            }
        }
    }

    private class Operations extends AsyncTask<Void, Void, Integer> {
        int result = 11;
        int type = 0;

        @Override
        protected Integer doInBackground(Void... params) {
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ID, wall_id)
                    .add(Constants.ACTION, Actions_.LIKE_UNLIKE)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL, requestBody, TAG);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.COMMENT)) {
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.COMMENT);
                        result = responseObject.getInt(Constants.STATUS);
                        type = responseObject.getInt(Constants.TYPE);
                    } else {
                        result = 12;
                    }
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
                    ShowToast.actionFailed(getApplicationContext());
                    break;
                case 1:
                    toggleLike(type);
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
}
