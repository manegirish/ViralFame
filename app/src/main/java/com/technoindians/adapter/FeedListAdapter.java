package com.technoindians.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.technoindians.wall.Feed_;
import com.technoindians.wall.WallCommentDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import technoindians.key.emoji.custom.EmojiTextView;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 04-03-2016
 *         Last Modified on 24-08-2016
 */

public class FeedListAdapter extends ArrayAdapter<Feed_> {

    private ArrayList<Feed_> feedList = new ArrayList<>();
    private Activity activity;
    private ImageLoader imageLoader = ViralFame.getInstance().getImageLoader();
    private ViewHolder holder;
    private RetrieveOperation retrieveOperation;

    public FeedListAdapter(Activity activity, ArrayList<Feed_> feedList) {
        super(activity, 0, feedList);
        this.feedList = feedList;
        this.activity = activity;
        this.retrieveOperation = new RetrieveOperation(activity.getApplicationContext());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        Feed_ feed_;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.wall_feed_item_layout, parent, false);

            holder = new ViewHolder();

            holder.likeText = (TextView) view.findViewById(R.id.wall_feed_item_like);
            holder.commentText = (TextView) view.findViewById(R.id.wall_feed_item_comment);
            holder.postText = (EmojiTextView) view.findViewById(R.id.wall_feed_item_post_text);
            holder.nameText = (TextView) view.findViewById(R.id.wall_feed_item_name);
            holder.postImage = (NetworkImageView) view.findViewById(R.id.wall_feed_item_image);
            holder.timeText = (TextView) view.findViewById(R.id.wall_feed_item_time);
            holder.feedText = (EmojiTextView) view.findViewById(R.id.wall_feed_item_feed_text);
            holder.skillText = (TextView) view.findViewById(R.id.wall_feed_item_role);

            holder.audioName = (TextView) view.findViewById(R.id.wall_post_media_audio_name);
            holder.audioSize = (TextView) view.findViewById(R.id.wall_post_media_audio_size);
            holder.audioTime = (TextView) view.findViewById(R.id.wall_post_media_audio_duration);
            holder.audioText = (EmojiTextView) view.findViewById(R.id.wall_post_media_post_text);

            holder.profilePhoto = (ImageView) view.findViewById(R.id.wall_feed_item_photo);
            holder.mediaIcon = (ImageView) view.findViewById(R.id.wall_post_media_audio_icon);
            holder.audioLayout = (LinearLayout) view.findViewById(R.id.wall_feed_item_audio_layout);
            holder.audioIconLayout = (LinearLayout) view.findViewById(R.id.wall_post_media_audio);
            holder.imageLayout = (RelativeLayout) view.findViewById(R.id.wall_feed_item_image_layout);

            holder.topView = (View) view.findViewById(R.id.wall_feed_item_top_view);
            holder.bottomView = (View) view.findViewById(R.id.wall_feed_item_bottom_view);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        feed_ = feedList.get(position);

        holder.skillText.setText(feed_.getSkill());
        holder.likeText.setText(feed_.getTotal_likes());
        holder.commentText.setText(feed_.getTotal_comments());
        holder.nameText.setText(feed_.getName());

        holder.postImage.setTag(position);
        holder.postImage.setOnClickListener(onClickListener);

        String time = TimeConverter.getWallTime(Long.parseLong(feed_.getLast_updated()));
        if (time == null || time.length() <= 0) {
            time = "now";
        } else {
            if (time.contains(",")) {
                time = time.substring(0, time.lastIndexOf(","));
            }
        }

        holder.timeText.setText(time);

        holder.profilePhoto.setTag(position);
        holder.profilePhoto.setOnClickListener(onClickListener);

        holder.commentText.setTag(position);
        holder.commentText.setOnClickListener(onClickListener);

        holder.likeText.setTag(position);
        holder.likeText.setOnClickListener(onClickListener);

        holder.audioIconLayout.setTag(position);
        holder.audioIconLayout.setOnClickListener(onClickListener);

        if (Integer.parseInt(feed_.getIs_like()) == 1) {
            holder.likeText.setTextColor(activity.getApplicationContext().getResources()
                    .getColor(R.color.colorPrimary));
            holder.likeText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_p, 0, 0, 0);
        } else {
            holder.likeText.setTextColor(activity.getApplicationContext().getResources()
                    .getColor(R.color.black_65));
            holder.likeText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_g, 0, 0, 0);
        }

        if (Integer.parseInt(feed_.getTotal_comments()) > 0) {
            holder.commentText.setTextColor(activity.getApplicationContext().getResources()
                    .getColor(R.color.colorPrimary));
            holder.commentText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_p, 0, 0, 0);
        } else {
            holder.commentText.setTextColor(activity.getApplicationContext().getResources()
                    .getColor(R.color.black_65));
            holder.commentText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_g, 0, 0, 0);
        }
        Log.e("FLA","Profile_pic => "+Urls.DOMAIN + feed_.getProfile_pic());
        Picasso.with(activity.getApplicationContext())
                .load(Urls.DOMAIN + feed_.getProfile_pic())
                .resize(100, 100)
                .onlyScaleDown()
                .transform(new CircleTransformMain())
                .placeholder(R.drawable.ic_avtar)
                .error(R.drawable.ic_avtar)
                .into(holder.profilePhoto);

        switch (Integer.parseInt(feed_.getMedia_type())) {
            case 0:
                holder.feedText.setVisibility(View.VISIBLE);
                holder.audioLayout.setVisibility(View.GONE);
                holder.imageLayout.setVisibility(View.GONE);
                setMessage(holder, feed_);
                break;
            case 1:
                holder.feedText.setVisibility(View.GONE);
                holder.audioLayout.setVisibility(View.GONE);
                holder.imageLayout.setVisibility(View.VISIBLE);
                setImage(holder, feed_);
                /*Uri uri = Uri.parse(Urls.DOMAIN + feed_.getMedia_file());
                holder.postImage.setImageURI(uri);
*/
                holder.postImage.setDefaultImageResId(R.drawable.ic_image_place_holder);
                holder.postImage.setImageUrl(Urls.DOMAIN + feed_.getMedia_file(), imageLoader);
                break;
            case 2:
                holder.feedText.setVisibility(View.GONE);
                holder.audioLayout.setVisibility(View.VISIBLE);
                holder.imageLayout.setVisibility(View.GONE);
                setVideo(holder, feed_);
                break;
            case 3:
                holder.feedText.setVisibility(View.GONE);
                holder.audioLayout.setVisibility(View.VISIBLE);
                holder.imageLayout.setVisibility(View.GONE);
                setAudio(holder, feed_);
                break;
        }

        return view;
    }

    private void setMessage(ViewHolder holder, Feed_ feed_) {
        holder.feedText.setEmojiText(feed_.getPost_text());
        holder.topView.setVisibility(View.GONE);
        holder.bottomView.setVisibility(View.VISIBLE);
    }

    private void setImage(ViewHolder holder, Feed_ feed_) {
        holder.postText.setEmojiText(feed_.getPost_text());
        holder.topView.setVisibility(View.GONE);
        holder.bottomView.setVisibility(View.GONE);
    }

    private void setAudio(ViewHolder holder, Feed_ feed_) {
        holder.topView.setVisibility(View.GONE);
        holder.bottomView.setVisibility(View.GONE);

        holder.mediaIcon.setImageResource(R.drawable.ic_audio_y);
        holder.audioText.setEmojiText(feed_.getPost_text());
        holder.audioSize.setText(FileCheck.getFileSize(Long.parseLong(feed_.getSize())));
        holder.audioName.setText(FileCheck.getFileName(feed_.getMedia_file()));
        String time = retrieveOperation.getFeedItem(Constants.MEDIA_DURATION, feed_.getId());
        if (time.equalsIgnoreCase("0")) {
            //GetDuration.get(feed_.getId(), Urls.DOMAIN + feed_.getMedia_file(), activity.getApplicationContext());
            time = "";
        }
        holder.audioTime.setText(time);
    }

    private void setVideo(ViewHolder holder, Feed_ feed_) {
        holder.topView.setVisibility(View.GONE);
        holder.bottomView.setVisibility(View.GONE);

        holder.mediaIcon.setImageResource(R.drawable.ic_video_play);
        holder.audioText.setEmojiText(feed_.getPost_text());
        holder.audioSize.setText(FileCheck.getFileSize(Long.parseLong(feed_.getSize())));
        holder.audioName.setText(FileCheck.getFileName(feed_.getMedia_file()));
        String time = retrieveOperation.getFeedItem(Constants.MEDIA_DURATION, feed_.getId());
        if (time.equalsIgnoreCase("0")) {
            //GetDuration.get(feed_.getId(), Urls.DOMAIN + feed_.getMedia_file(), activity.getApplicationContext());
            time = "";
        }
        holder.audioTime.setText(time);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                    (activity.getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();

            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.wall_feed_item_comment:
                    openCommentDialog(position);
                    break;
                case R.id.wall_feed_item_like:
                    new Operations().execute(position);
                    break;
                case R.id.wall_feed_item_image:
                    Intent imageIntent = new Intent(activity.getApplicationContext(), FeedDetailsActivity.class);
                    imageIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    imageIntent.putExtra(Constants.MEDIA_FILE, feedList.get(position).getMedia_file());
                    imageIntent.putExtra(Constants.WALL_ID, feedList.get(position).getId());
                    imageIntent.putExtra(Constants.MEDIA_TYPE, Constants.TYPE_IMAGE);
                    activity.startActivity(imageIntent,nextAnimation);
                    break;
                case R.id.wall_post_media_audio:
                    int media_type = Integer.parseInt(feedList.get(position).getMedia_type());
                    if (media_type == Constants.INTENT_AUDIO) {
                        Intent audioIntent = new Intent(activity.getApplicationContext(), FeedDetailsActivity.class);
                        audioIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        audioIntent.putExtra(Constants.MEDIA_FILE, feedList.get(position).getMedia_file());
                        audioIntent.putExtra(Constants.WALL_ID, feedList.get(position).getId());
                        audioIntent.putExtra(Constants.MEDIA_TYPE, Constants.TYPE_AUDIO);
                        activity.startActivity(audioIntent,nextAnimation);
                    } else if (media_type == Constants.INTENT_VIDEO) {
                        Intent videoIntent = new Intent(activity.getApplicationContext(), FeedDetailsActivity.class);
                        videoIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        videoIntent.putExtra(Constants.MEDIA_FILE, feedList.get(position).getMedia_file());
                        videoIntent.putExtra(Constants.WALL_ID, feedList.get(position).getId());
                        videoIntent.putExtra(Constants.MEDIA_TYPE, Constants.TYPE_VIDEO);
                        activity.startActivity(videoIntent,nextAnimation);
                    }
                    break;
                case R.id.wall_feed_item_photo:

                    Intent profileIntent = new Intent(activity.getApplicationContext(), UserPortfolioActivity.class);
                    profileIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profileIntent.putExtra(Constants.USER_ID, feedList.get(position).getUser_id());
                    profileIntent.putExtra(Constants.PROFILE_PIC, feedList.get(position).getProfile_pic());
                    profileIntent.putExtra(Constants.NAME, feedList.get(position).getName());
                    profileIntent.putExtra(Constants.SKILL, feedList.get(position).getSkill());
                    profileIntent.putExtra(Constants.IS_FOLLOW, feedList.get(position).getFollow());

                    activity.startActivity(profileIntent, nextAnimation);
                    break;
            }
        }
    };

    public boolean removeCommentDialog(){
        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.COMMENT);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
            return true;
        }
        return false;
    }

    private void openCommentDialog(int position) {
        removeCommentDialog();
        FragmentManager fragmentManager = activity.getFragmentManager();
        WallCommentDialogFragment detailsFragment = new WallCommentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ID, feedList.get(position).getId());
        detailsFragment.setArguments(bundle);
        detailsFragment.show(fragmentManager, Constants.COMMENT);
    }

    private void toggleLike(int position) {
        // Log.e("position"," -> "+position);
        Feed_ tempFeed = feedList.get(position);
        if (Integer.parseInt(feedList.get(position).getIs_like()) == 1) {
            tempFeed.setIs_like("0");
            if (Integer.parseInt(feedList.get(position).getTotal_likes()) > 0) {
                tempFeed.setTotal_likes("" + (Integer.parseInt(feedList.get(position).getTotal_likes()) - 1));
            } else {
                tempFeed.setTotal_likes("" + Integer.parseInt(feedList.get(position).getTotal_likes()));
            }
            feedList.remove(position);
            feedList.add(position, tempFeed);
            notifyDataSetChanged();
        } else {
            tempFeed.setIs_like("1");
            tempFeed.setTotal_likes("" + (Integer.parseInt(feedList.get(position).getTotal_likes()) + 1));
            feedList.remove(position);
            feedList.add(position, tempFeed);
            notifyDataSetChanged();
        }
    }

    private class Operations extends AsyncTask<Integer, Void, Integer> {
        int result = 11;
        int position;

        @Override
        protected Integer doInBackground(Integer... params) {
            position = params[0];
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ID, feedList.get(position).getId())
                    .add(Constants.ACTION, Actions_.LIKE_UNLIKE)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL, requestBody);
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
                    ShowToast.actionFailed(activity.getApplicationContext());
                    break;
                case 1:
                    toggleLike(position);
                    break;
                case 2:
                    ShowToast.actionFailed(activity.getApplicationContext());
                    break;
                case 11:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    break;
                case 12:
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;
            }
        }
    }

    private class ViewHolder {
        TextView likeText, commentText, nameText, timeText,
                skillText, audioTime, audioSize, audioName;
        EmojiTextView feedText, postText, audioText;
        NetworkImageView postImage;
        LinearLayout audioLayout, audioIconLayout;
        RelativeLayout imageLayout;
        ImageView profilePhoto, mediaIcon;
        View topView, bottomView;
    }
}