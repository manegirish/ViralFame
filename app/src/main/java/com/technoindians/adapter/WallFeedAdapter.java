package com.technoindians.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.dataappsinfo.viralfame.R;
import com.dataappsinfo.viralfame.ViralFame;
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.database.RetrieveOperation;
import com.technoindians.database.UpdateOperations;
import com.technoindians.library.FileCheck;
import com.technoindians.library.TimeConverter;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.peoples.UserPortfolioActivity;
import com.technoindians.pops.ShowToast;
import com.technoindians.portfolio.FeedDetailsActivity;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.CircleTransformMain;
import com.technoindians.views.NetworkImageView;
import com.technoindians.wall.WallCommentDialogFragment;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import technoindians.key.emoji.custom.EmojiTextView;

/**
 * Created by Girish on 11-01-2017.
 */

public class WallFeedAdapter extends CursorAdapter {

    private static final String TAG = WallFeedAdapter.class.getSimpleName();
    private Context context;
    private Activity activity;
    private ImageLoader imageLoader = ViralFame.getInstance().getImageLoader();
    private RetrieveOperation retrieveOperation;
    private UpdateOperations updateOperations;

    private Cursor originalCursor;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                    (context, R.anim.animation_one, R.anim.animation_two).toBundle();
            String id = (String) v.getTag();
            Cursor selectedCursor = retrieveOperation.fetchSingleFeed("*", id);
            if (selectedCursor == null) {
                return;
            }
            selectedCursor.moveToFirst();
            switch (v.getId()) {
                case R.id.wall_feed_item_comment:
                    openCommentDialog(id);
                    break;
                case R.id.wall_feed_item_like:
                    new Operations().execute(id);
                    break;
                case R.id.wall_feed_item_image:
                    Intent imageIntent = new Intent(context, FeedDetailsActivity.class);
                    imageIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    imageIntent.putExtra(Constants.MEDIA_FILE, selectedCursor.getString(selectedCursor.getColumnIndex(Constants.MEDIA_FILE)));
                    imageIntent.putExtra(Constants.WALL_ID, id);
                    imageIntent.putExtra(Constants.MEDIA_TYPE, Constants.TYPE_IMAGE);
                    activity.startActivity(imageIntent, nextAnimation);
                    break;
                case R.id.wall_post_media_audio:
                    int media_type = Integer.parseInt(selectedCursor.getString(selectedCursor.getColumnIndex(Constants.MEDIA_TYPE)));
                    if (media_type == Constants.INTENT_AUDIO) {
                        Intent audioIntent = new Intent(context, FeedDetailsActivity.class);
                        audioIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        audioIntent.putExtra(Constants.MEDIA_FILE, selectedCursor.getString(selectedCursor.getColumnIndex(Constants.MEDIA_FILE)));
                        audioIntent.putExtra(Constants.WALL_ID, id);
                        audioIntent.putExtra(Constants.MEDIA_TYPE, Constants.TYPE_AUDIO);
                        activity.startActivity(audioIntent, nextAnimation);
                    } else if (media_type == Constants.INTENT_VIDEO) {
                        Intent videoIntent = new Intent(context, FeedDetailsActivity.class);
                        videoIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        videoIntent.putExtra(Constants.MEDIA_FILE, selectedCursor.getString(selectedCursor.getColumnIndex(Constants.MEDIA_FILE)));
                        videoIntent.putExtra(Constants.WALL_ID, id);
                        videoIntent.putExtra(Constants.MEDIA_TYPE, Constants.TYPE_VIDEO);
                        activity.startActivity(videoIntent, nextAnimation);
                    }
                    break;
                case R.id.wall_feed_item_photo:

                    Intent profileIntent = new Intent(context, UserPortfolioActivity.class);
                    profileIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profileIntent.putExtra(Constants.USER_ID, selectedCursor.getString(selectedCursor.getColumnIndex(Constants.USER_ID)));
                    profileIntent.putExtra(Constants.PROFILE_PIC, selectedCursor.getString(selectedCursor.getColumnIndex(Constants.PROFILE_PIC)));
                    profileIntent.putExtra(Constants.NAME, selectedCursor.getString(selectedCursor.getColumnIndex(Constants.NAME)));
                    profileIntent.putExtra(Constants.SKILL, selectedCursor.getString(selectedCursor.getColumnIndex(Constants.SKILL)));
                    profileIntent.putExtra(Constants.IS_FOLLOW, selectedCursor.getString(selectedCursor.getColumnIndex(Constants.IS_FOLLOW)));
                    activity.startActivity(profileIntent, nextAnimation);
                    break;
            }
        }
    };

    public WallFeedAdapter(Activity activity, Cursor cursor) {
        super(activity, cursor);
        this.originalCursor = cursor;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.retrieveOperation = new RetrieveOperation(context);
        updateOperations = new UpdateOperations(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.wall_feed_item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView likeText = (TextView) view.findViewById(R.id.wall_feed_item_like);
        TextView commentText = (TextView) view.findViewById(R.id.wall_feed_item_comment);
        TextView timeText = (TextView) view.findViewById(R.id.wall_feed_item_time);
        TextView nameText = (TextView) view.findViewById(R.id.wall_feed_item_name);
        TextView audioName = (TextView) view.findViewById(R.id.wall_post_media_audio_name);
        TextView audioSize = (TextView) view.findViewById(R.id.wall_post_media_audio_size);
        TextView audioTime = (TextView) view.findViewById(R.id.wall_post_media_audio_duration);
        TextView skillText = (TextView) view.findViewById(R.id.wall_feed_item_role);

        EmojiTextView postText = (EmojiTextView) view.findViewById(R.id.wall_feed_item_post_text);
        EmojiTextView audioText = (EmojiTextView) view.findViewById(R.id.wall_post_media_post_text);
        EmojiTextView feedText = (EmojiTextView) view.findViewById(R.id.wall_feed_item_feed_text);

        NetworkImageView postImage = (NetworkImageView) view.findViewById(R.id.wall_feed_item_image);

        ImageView profilePhoto = (ImageView) view.findViewById(R.id.wall_feed_item_photo);
        ImageView mediaIcon = (ImageView) view.findViewById(R.id.wall_post_media_audio_icon);

        LinearLayout audioLayout = (LinearLayout) view.findViewById(R.id.wall_feed_item_audio_layout);
        LinearLayout audioIconLayout = (LinearLayout) view.findViewById(R.id.wall_post_media_audio);
        RelativeLayout imageLayout = (RelativeLayout) view.findViewById(R.id.wall_feed_item_image_layout);

        View topView = view.findViewById(R.id.wall_feed_item_top_view);
        View bottomView = view.findViewById(R.id.wall_feed_item_bottom_view);

        if (cursor.isClosed()) {
            // Log.e(TAG, "cursor:closed ");
            return;
        }
        String name = cursor.getString(cursor.getColumnIndex(Constants.NAME));
        String id = cursor.getString(cursor.getColumnIndex(Constants._ID));
        String media_type = cursor.getString(cursor.getColumnIndex(Constants.MEDIA_TYPE));
        String media_file = cursor.getString(cursor.getColumnIndex(Constants.MEDIA_FILE));
        String total_comments = cursor.getString(cursor.getColumnIndex(Constants.TOTAL_COMMENTS));
        String total_likes = cursor.getString(cursor.getColumnIndex(Constants.TOTAL_LIKES));
        String is_like = cursor.getString(cursor.getColumnIndex(Constants.IS_LIKE));
        String last_updated = cursor.getString(cursor.getColumnIndex(Constants.LAST_UPDATED));
        String profile_pic = cursor.getString(cursor.getColumnIndex(Constants.PROFILE_PIC));
        String skills = cursor.getString(cursor.getColumnIndex(Constants.SKILL));

        skillText.setText(skills);
        likeText.setText(total_likes);
        commentText.setText(total_comments);
        nameText.setText(name);

        postImage.setTag(id);
        postImage.setOnClickListener(onClickListener);

        String time = TimeConverter.getWallTime(Long.parseLong(last_updated));
        if (time == null || time.length() <= 0) {
            time = "now";
        } else {
            if (time.contains(",")) {
                time = time.substring(0, time.lastIndexOf(","));
            }
        }

        timeText.setText(time);

        profilePhoto.setTag(id);
        profilePhoto.setOnClickListener(onClickListener);

        commentText.setTag(id);
        commentText.setOnClickListener(onClickListener);

        likeText.setTag(id);
        likeText.setOnClickListener(onClickListener);

        audioIconLayout.setTag(id);
        audioIconLayout.setOnClickListener(onClickListener);

        if (Integer.parseInt(is_like) == 1) {
            likeText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            likeText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_p, 0, 0, 0);
        } else {
            likeText.setTextColor(context.getResources().getColor(R.color.black_65));
            likeText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_g, 0, 0, 0);
        }

        if (Integer.parseInt(total_comments) > 0) {
            commentText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            commentText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_p, 0, 0, 0);
        } else {
            commentText.setTextColor(context.getResources()
                    .getColor(R.color.black_65));
            commentText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_g, 0, 0, 0);
        }
        //Log.e("FLA", "Profile_pic => " + Urls.DOMAIN + feed_.getProfile_pic());
        Picasso.with(context)
                .load(Urls.DOMAIN + profile_pic)
                .resize(100, 100)
                .onlyScaleDown()
                .transform(new CircleTransformMain())
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(profilePhoto);

        switch (Integer.parseInt(media_type)) {
            case 0:
                feedText.setVisibility(View.VISIBLE);
                audioLayout.setVisibility(View.GONE);
                imageLayout.setVisibility(View.GONE);
                setMessage(feedText, topView, bottomView, cursor);
                break;
            case 1:
                feedText.setVisibility(View.GONE);
                audioLayout.setVisibility(View.GONE);
                imageLayout.setVisibility(View.VISIBLE);
                setImage(postText, topView, bottomView, cursor);

                postImage.setDefaultImageResId(R.drawable.ic_image_place_holder);
                postImage.setImageUrl(Urls.DOMAIN + media_file, imageLoader);
                break;
            case 2:
                feedText.setVisibility(View.GONE);
                audioLayout.setVisibility(View.VISIBLE);
                imageLayout.setVisibility(View.GONE);
                setVideo(audioText, mediaIcon, audioSize, audioName, audioTime, topView, bottomView, cursor);
                break;
            case 3:
                feedText.setVisibility(View.GONE);
                audioLayout.setVisibility(View.VISIBLE);
                imageLayout.setVisibility(View.GONE);
                setAudio(audioText, mediaIcon, audioSize, audioName, audioTime, topView, bottomView, cursor);
                break;
        }
    }

    private void setMessage(EmojiTextView feedText, View topView, View bottomView, Cursor cursor) {
        feedText.setEmojiText(cursor.getString(cursor.getColumnIndex(Constants.POST_TEXT)));
        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.VISIBLE);
    }

    private void setImage(EmojiTextView postText, View topView, View bottomView, Cursor cursor) {
        postText.setEmojiText(cursor.getString(cursor.getColumnIndex(Constants.POST_TEXT)));
        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);
    }

    private void setAudio(EmojiTextView audioText, ImageView mediaIcon, TextView audioSize,
                          TextView audioName, TextView audioTime, View topView, View bottomView, Cursor cursor) {
        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);

        mediaIcon.setImageResource(R.drawable.ic_audio_y);
        audioText.setEmojiText(cursor.getString(cursor.getColumnIndex(Constants.POST_TEXT)));
        audioSize.setText(FileCheck.getFileSize(Long.parseLong(cursor.getString(cursor.getColumnIndex(Constants.MEDIA_SIZE)))));
        audioName.setText(FileCheck.getFileName(cursor.getString(cursor.getColumnIndex(Constants.MEDIA_FILE))));
        String time = retrieveOperation.getFeedItem(Constants.MEDIA_DURATION, cursor.getString(cursor.getColumnIndex(Constants._ID)));
        if (time.equalsIgnoreCase("0") || time.length() <= 0) {
            time = "";
        }
        audioTime.setText(time);
    }

    private void setVideo(EmojiTextView audioText, ImageView mediaIcon, TextView audioSize,
                          TextView audioName, TextView audioTime, View topView, View bottomView, Cursor cursor) {
        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);

        mediaIcon.setImageResource(R.drawable.ic_video_play);
        audioText.setEmojiText(cursor.getString(cursor.getColumnIndex(Constants.POST_TEXT)));
        audioSize.setText(FileCheck.getFileSize(Long.parseLong(cursor.getString(cursor.getColumnIndex(Constants.MEDIA_SIZE)))));
        audioName.setText(FileCheck.getFileName(cursor.getString(cursor.getColumnIndex(Constants.MEDIA_FILE))));
        String time = retrieveOperation.getFeedItem(Constants.MEDIA_DURATION, cursor.getString(cursor.getColumnIndex(Constants._ID)));
        if (time.equalsIgnoreCase("0")) {
            time = "";
        }
        audioTime.setText(time);
    }

    private boolean removeCommentDialog() {
        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.COMMENT);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
            return true;
        }
        return false;
    }

    private void openCommentDialog(String _id) {
        removeCommentDialog();
        FragmentManager fragmentManager = activity.getFragmentManager();
        WallCommentDialogFragment detailsFragment = new WallCommentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ID, _id);
        detailsFragment.setArguments(bundle);
        detailsFragment.show(fragmentManager, Constants.COMMENT);
    }

    private void toggleLike(String _id) {
        Cursor likeCursor = retrieveOperation.fetchSingleFeed("*", _id);
        if (likeCursor == null) {
            return;
        }
        likeCursor.moveToFirst();
        String is_like = likeCursor.getString(likeCursor.getColumnIndex(Constants.IS_LIKE));
        int total_like = Integer.parseInt(likeCursor.getString(likeCursor.getColumnIndex(Constants.TOTAL_LIKES)));
        if (Integer.parseInt(is_like) == 1) {
            is_like = "0";
            if (total_like > 0) {
                total_like = total_like - 1;
            }
        } else {
            is_like = "1";
            total_like = total_like + 1;
        }
        updateOperations.updateFeed(_id, Constants.IS_LIKE, is_like);
        updateOperations.updateFeed(_id, Constants.TOTAL_LIKES, "" + total_like);
        getCursor().requery();
        notifyDataSetChanged();
    }

    private class Operations extends AsyncTask<String, Void, Integer> {
        int result = 11;
        String _id;

        @Override
        protected Integer doInBackground(String... params) {
            _id = params[0];
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ID, _id)
                    .add(Constants.ACTION, Actions_.LIKE_UNLIKE)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL, requestBody, TAG);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.COMMENT)) {
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.COMMENT);
                        result = responseObject.getInt(Constants.STATUS);
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
                    ShowToast.actionFailed(context);
                    break;
                case 1:
                    toggleLike(_id);
                    break;
                case 2:
                    ShowToast.actionFailed(context);
                    break;
                case 11:
                    ShowToast.networkProblemToast(context);
                    break;
                case 12:
                    ShowToast.internalErrorToast(context);
                    break;
            }
        }
    }
}
