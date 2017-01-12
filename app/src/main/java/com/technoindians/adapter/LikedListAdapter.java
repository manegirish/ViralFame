package com.technoindians.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.CircleTransformMain;
import com.technoindians.wall.Liked_;

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

public class LikedListAdapter extends ArrayAdapter<Liked_> {

    private static final String TAG = LikedListAdapter.class.getSimpleName();

    private ArrayList<Liked_> likeList = null;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.liked_list_item_follow:
                    new FollowUnfollow(likeList.get(position).getUserId(), position).execute();
                    break;
            }
        }
    };
    private ArrayList<Liked_> list;
    private Activity activity;

    public LikedListAdapter(Activity activity, ArrayList<Liked_> likeList) {
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
        Liked_ liked_;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.liked_list_item, parent, false);
            holder = new ViewHolder();

            holder.skillText = (TextView) view.findViewById(R.id.liked_list_item_skill);
            holder.icon = (ImageView) view.findViewById(R.id.liked_list_item_icon);
            holder.nameText = (TextView) view.findViewById(R.id.liked_list_item_name);
            holder.follow = (TextView) view.findViewById(R.id.liked_list_item_follow);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        liked_ = likeList.get(position);

        holder.follow.setTag(position);
        if (Integer.parseInt(liked_.getIsFollow()) == 1) {
            holder.follow.setText(activity.getApplicationContext().getResources().getString(R.string.unfollow));
        } else {
            holder.follow.setText(activity.getApplicationContext().getResources().getString(R.string.follow));
        }
        if (Integer.parseInt(liked_.getUserType()) == 2) {
            holder.nameText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verify_user, 0, 0, 0);
        } else {
            holder.nameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        holder.skillText.setText(liked_.getSkill());
        holder.nameText.setText(liked_.getName());
        holder.follow.setOnClickListener(onClickListener);
        if (liked_.getUserId().equalsIgnoreCase(Preferences.get(Constants.USER_ID))) {
            holder.nameText.setText("Me");
            holder.follow.setEnabled(false);
            holder.follow.setVisibility(View.GONE);
        } else {
            holder.follow.setEnabled(true);
            holder.follow.setVisibility(View.VISIBLE);
        }

        Picasso.with(activity.getApplicationContext())
                .load(Urls.DOMAIN + liked_.getProfile_pic())
                .transform(new CircleTransformMain())
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(holder.icon);

        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        likeList.clear();
        if (charText.length() == 0) {
            likeList.addAll(list);
        } else {
            for (Liked_ wp : list) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    likeList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void updateList(int position, int type) {
        Liked_ liked_ = likeList.get(position);
        likeList.remove(position);
        liked_.setIsFollow("" + type);
        likeList.add(position, liked_);
        notifyDataSetChanged();
    }

    private class FollowUnfollow extends AsyncTask<Void, Void, Integer> {

        String follow_id;
        int type, position;

        public FollowUnfollow(String follow_id, int position) {
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
                        type = responseObject.getInt(Constants.TYPE);
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
                    if (type == 1) {
                        updateList(position, type);
                        ShowToast.follow(activity.getApplicationContext());
                    } else {
                        updateList(position, type);
                        ShowToast.unfollow(activity.getApplicationContext());
                    }
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
        TextView skillText, nameText, follow;
        ImageView icon, badge;
    }
}