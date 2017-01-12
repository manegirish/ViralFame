package com.technoindians.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.technoindians.network.CheckInternet;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.opportunities.Applicant_;
import com.technoindians.peoples.UserPortfolioActivity;
import com.technoindians.pops.ShowSnack;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.CircleTransformMain;

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

public class ApplicantListAdapter extends ArrayAdapter<Applicant_> {

    private static final String TAG = ApplicantListAdapter.class.getSimpleName();
    private ArrayList<Applicant_> applicantList = null;
    private ArrayList<Applicant_> list;
    private Activity activity;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (CheckInternet.check()) {
                int position = (Integer) v.getTag();
                String id = applicantList.get(position).getApplicationId();
                switch (v.getId()) {
                    case R.id.applicant_list_item_action_accept:
                        new Operations(id, Actions_.ACCEPT_APPLICATION, position).execute();
                        break;

                    case R.id.applicant_list_item_action_reject:
                        new Operations(id, Actions_.REJECT_APPLICATION, position).execute();
                        break;

                    case R.id.applicant_list_item_info_layout:
                        intentNext(position);
                        break;
                    case R.id.applicant_list_item_icon:
                        intentNext(position);
                        break;
                }
            } else {
                ShowSnack.noInternet(v);
            }
        }
    };

    public ApplicantListAdapter(Activity activity, ArrayList<Applicant_> applicantList) {
        super(activity, 0, applicantList);
        this.applicantList = applicantList;
        this.activity = activity;
        this.list = new ArrayList<>();
        this.list.addAll(applicantList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View view = convertView;
        Applicant_ applicant_;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.applicant_list_item, parent, false);

            holder = new ViewHolder();

            holder.skillText = (TextView) view.findViewById(R.id.applicant_list_item_skill);
            holder.icon = (ImageView) view.findViewById(R.id.applicant_list_item_icon);
            holder.nameText = (TextView) view.findViewById(R.id.applicant_list_item_name);
            holder.accept = (ImageView) view.findViewById(R.id.applicant_list_item_action_accept);
            holder.reject = (ImageView) view.findViewById(R.id.applicant_list_item_action_reject);
            holder.infoLayout = (LinearLayout) view.findViewById(R.id.applicant_list_item_info_layout);
            holder.mainLayout = (LinearLayout) view.findViewById(R.id.applicant_list_item_main);
            holder.warningText = (TextView) view.findViewById(R.id.applicant_list_item_warning);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        applicant_ = applicantList.get(position);
        if (applicant_.getStatus() == 2) {
            holder.mainLayout.setVisibility(View.GONE);
            holder.warningText.setVisibility(View.VISIBLE);
            holder.warningText.setText(Warnings.NO_RESULT_FOUND);
            return view;
        }
        holder.mainLayout.setVisibility(View.VISIBLE);
        holder.warningText.setVisibility(View.GONE);

        holder.accept.setTag(position);
        holder.reject.setTag(position);
        holder.infoLayout.setTag(position);
        holder.icon.setTag(position);

        holder.skillText.setText(applicant_.getSkillPrimary());
        holder.nameText.setText(applicant_.getName());
        holder.nameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        Picasso.with(activity.getApplicationContext())
                .load(Urls.DOMAIN + applicant_.getPic())
                .transform(new CircleTransformMain())
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(holder.icon);
        holder.accept.setOnClickListener(onClickListener);
        holder.reject.setOnClickListener(onClickListener);
        holder.infoLayout.setOnClickListener(onClickListener);
        holder.icon.setOnClickListener(onClickListener);

        return view;
    }

    private void intentNext(int position) {
        Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                (activity.getApplicationContext(), R.anim.animation_bottom_to_up, R.anim.animation_bottom_to_up).toBundle();

        Intent profileIntent = new Intent(activity.getApplicationContext(), UserPortfolioActivity.class);
        profileIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        profileIntent.putExtra(Constants.USER_ID, applicantList.get(position).getApplicantId());
        profileIntent.putExtra(Constants.PROFILE_PIC, applicantList.get(position).getPic());
        profileIntent.putExtra(Constants.NAME, applicantList.get(position).getName());
        profileIntent.putExtra(Constants.SKILL, applicantList.get(position).getSkillPrimary());
        activity.startActivity(profileIntent, nextAnimation);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        applicantList.clear();
        if (charText.length() == 0) {
            applicantList.addAll(list);
        } else {
            for (Applicant_ wp : list) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    applicantList.add(wp);
                }
            }
        }
        if (applicantList == null || applicantList.size() <= 0) {
            Applicant_ applicant_ = new Applicant_();
            applicant_.setStatus(2);
            applicantList.add(applicant_);
        }
        notifyDataSetChanged();
    }

    private class Operations extends AsyncTask<Void, Void, Integer> {

        String id, action;
        int position;

        public Operations(String id, String action, int position) {
            this.id = id;
            this.action = action;
            this.position = position;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ACTION, action)
                    .add(Constants.ID, id)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.JOB_OPERATIONS, requestBody,TAG);

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null) {
                    if (jsonObject.has(Constants.STATUS)) {
                        return jsonObject.getInt(Constants.STATUS);
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
            switch (integer) {
                case 0:
                    ShowToast.actionFailed(activity.getApplicationContext());
                    break;
                case 1:
                    ShowToast.successful(activity.getApplicationContext());
                    applicantList.remove(position);
                    notifyDataSetChanged();
                    break;
                case 2:
                    ShowToast.actionFailed(activity.getApplicationContext());
                    break;
                case 11:
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;
                case 12:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    break;
            }
        }
    }

    private class ViewHolder {
        TextView skillText, nameText, warningText;
        ImageView icon, accept, reject;
        LinearLayout infoLayout, mainLayout;
    }

}