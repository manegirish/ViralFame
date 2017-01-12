package com.technoindians.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.peoples.Follow;
import com.technoindians.peoples.UserPortfolioActivity;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.CircleTransformMain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 04-03-2016
 *         Last Modified on 11-04-2016
 */

public class FollowListAdapter extends ArrayAdapter<Follow> {

    private static final String TAG = FollowListAdapter.class.getSimpleName();

    private ArrayList<Follow> likeList = null;
    private ArrayList<Follow> list;
    private Activity activity;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            if (v.getId() == R.id.liked_list_item_follow) {
                new Operations(likeList.get(position).getUserId(),
                        position).execute();
            } else {
                Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                        (activity.getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();

                Intent profileIntent = new Intent(activity.getApplicationContext(), UserPortfolioActivity.class);
                profileIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Log.e(TAG, "user_id: " + likeList.get(position).getUserId());
                profileIntent.putExtra(Constants.USER_ID, likeList.get(position).getUserId());
                profileIntent.putExtra(Constants.PROFILE_PIC, likeList.get(position).getPhoto());
                profileIntent.putExtra(Constants.NAME, likeList.get(position).getName());
                profileIntent.putExtra(Constants.SKILL, likeList.get(position).getSkill());
                profileIntent.putExtra(Constants.IS_FOLLOW, likeList.get(position).getIsFollow());
                activity.startActivity(profileIntent, nextAnimation);
            }
        }
    };

    public FollowListAdapter(Activity activity, ArrayList<Follow> likeList) {
        super(activity, 0, likeList);
        this.likeList = likeList;
        this.activity = activity;
        this.list = new ArrayList<>();
        this.list.addAll(likeList);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        Follow follow;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.liked_list_item, parent, false);
            holder = new ViewHolder();

            holder.skillText = (TextView) view.findViewById(R.id.liked_list_item_skill);
            holder.icon = (ImageView) view.findViewById(R.id.liked_list_item_icon);
            holder.nameText = (TextView) view.findViewById(R.id.liked_list_item_name);
            holder.follow = (TextView) view.findViewById(R.id.liked_list_item_follow);
            holder.infoLayout = (LinearLayout) view.findViewById(R.id.liked_list_item_info_layout);
            holder.warningText = (TextView) view.findViewById(R.id.liked_list_item_warning);
            holder.mainLayout = (LinearLayout) view.findViewById(R.id.liked_list_item_main);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        follow = likeList.get(position);
        if (follow.getStatus() != 1) {
            holder.mainLayout.setVisibility(View.GONE);
            holder.warningText.setVisibility(View.VISIBLE);
            holder.warningText.setText(Warnings.NO_RESULT_FOUND);
            return view;
        }

        holder.mainLayout.setVisibility(View.VISIBLE);
        holder.warningText.setVisibility(View.GONE);

        holder.follow.setTag(position);
        holder.infoLayout.setTag(position);
        holder.icon.setTag(position);

        if (Integer.parseInt(follow.getIsFollow()) == 1) {
            holder.follow.setText(activity.getApplicationContext().getResources().getString(R.string.unfollow));
        } else {
            holder.follow.setText(activity.getApplicationContext().getResources().getString(R.string.follow));
        }
        if (Integer.parseInt(follow.getType()) == 2) {
            holder.nameText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verify_user, 0, 0, 0);
        } else {
            holder.nameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        holder.skillText.setText(follow.getSkill());
        holder.nameText.setText(follow.getName());
        if (follow.getUserId().equalsIgnoreCase(Preferences.get(Constants.USER_ID))) {
            holder.nameText.setText("Me");
            holder.follow.setEnabled(false);
            holder.follow.setVisibility(View.GONE);
        } else {
            holder.follow.setEnabled(true);
            holder.follow.setVisibility(View.VISIBLE);
        }

        Picasso.with(activity.getApplicationContext())
                .load(Urls.DOMAIN + follow.getPhoto())
                .transform(new CircleTransformMain())
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(holder.icon);

        holder.follow.setOnClickListener(onClickListener);
        holder.infoLayout.setOnClickListener(onClickListener);
        holder.icon.setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public Follow getItem(int position) {
        return likeList.get(position);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        likeList.clear();
        if (charText.length() == 0) {
            likeList.addAll(list);
        } else {
            for (Follow wp : list) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    likeList.add(wp);
                }
            }
        }
        if (likeList == null || likeList.size() <= 0) {
            Follow follow = new Follow();
            follow.setStatus(2);
            likeList.add(follow);
        }
        notifyDataSetChanged();
    }

    private class Operations extends AsyncTask<Void, Void, Integer> {

        String follow_id;
        int position;

        public Operations(String follow_id, int position) {
            this.follow_id = follow_id;
            this.position = position;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 11;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.FOLLOW_ID, follow_id)
                    .add(Constants.ACTION, Actions_.FOLLOW_UNFOLLOW)
                    .build();

            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.FOLLOWER_OPERATIONS, requestBody, TAG);
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
                    ShowToast.actionFailed(activity.getApplicationContext());
                    break;
                case 1:
                    int is_follow = Integer.parseInt(likeList.get(position).getIsFollow());
                    Follow follow = likeList.get(position);
                    likeList.remove(position);
                    if (is_follow == 1) {
                        follow.setIsFollow("0");
                    } else {
                        follow.setIsFollow("1");
                    }
                    likeList.add(position, follow);
                    notifyDataSetChanged();
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
        TextView skillText, nameText, follow, warningText;
        ImageView icon;
        LinearLayout infoLayout, mainLayout;
    }
}