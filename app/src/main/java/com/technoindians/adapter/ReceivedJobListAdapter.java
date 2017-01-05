/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.technoindians.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.opportunities.Jobs_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

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

public class ReceivedJobListAdapter extends ArrayAdapter<Jobs_> {

    private ArrayList<Jobs_> jobList = null;
    private ArrayList<Jobs_> list;
    private Activity activity;

    public ReceivedJobListAdapter(Activity activity, ArrayList<Jobs_> jobList) {
        super(activity, 0, jobList);
        this.jobList = jobList;
        this.activity = activity;
        this.list = new ArrayList<>();
        this.list.addAll(jobList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parrent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.received_opportunity_list_item, parrent, false);
            holder = new ViewHolder();

            holder.titleText = (TextView) view.findViewById(R.id.opportunity_list_item_title);
            holder.timeText = (RelativeTimeTextView) view.findViewById(R.id.opportunity_list_item_time);
            holder.nameText = (TextView) view.findViewById(R.id.opportunity_list_item_name);
            holder.companyText = (TextView) view.findViewById(R.id.opportunity_list_item_firm);

            holder.skillText = (TextView) view.findViewById(R.id.opportunity_list_item_skill);
            holder.descriptionText = (TextView) view.findViewById(R.id.opportunity_list_item_description);
            holder.applyText = (TextView) view.findViewById(R.id.opportunity_list_apply);
            holder.warningText = (TextView) view.findViewById(R.id.opportunity_list_item_warning);
            holder.mainLayout = (LinearLayout) view.findViewById(R.id.opportunity_list_item_layout);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Jobs_ jobs_ = jobList.get(position);

        if (jobs_.getStatus() == 2) {
            holder.warningText.setText(Warnings.NO_MATCHING_JOB);
            holder.warningText.setVisibility(View.VISIBLE);
            holder.mainLayout.setVisibility(View.GONE);
            return view;
        }
        holder.mainLayout.setVisibility(View.VISIBLE);
        holder.warningText.setVisibility(View.GONE);
        long time = Long.parseLong(jobs_.getDate());

        if (time < 1000000000000L) {
            time *= 1000;
        }

        holder.applyText.setTag(position);
        holder.applyText.setText(getText(Integer.parseInt(jobs_.getApply())));
        holder.applyText.setBackgroundResource(getBackground(Integer.parseInt(jobs_.getApply())));
        holder.applyText.setOnClickListener(onClickListener);
        holder.applyText.setBackgroundResource(getBackground(Integer.parseInt(jobs_.getApply())));

        holder.titleText.setText(jobs_.getTitle());
        holder.descriptionText.setText(jobs_.getDescription());
        holder.nameText.setText(jobs_.getName());
        holder.companyText.setText(jobs_.getOrganisation());
        holder.skillText.setText(jobs_.getSkillPrimary());
        holder.timeText.setReferenceTime(time);

        return view;
    }

    private int getBackground(int status) {
        switch (status) {
            case 0:
                return R.drawable.ic_grey_rounded;
            case 1:
                return R.drawable.ic_amber_rounded;
            case 2:
                return R.drawable.ic_green_rounded;
            case 3:
                return R.drawable.ic_red_rounded;
            default:
                return R.drawable.ic_grey_rounded;
        }
    }

    private String getText(int status) {
        //1: apply job, 2- Accept job,3-reject job,4-unsubscribe job
        switch (status) {
            case 0:
                return "Apply";
            case 1:
                return "Applied";
            case 2:
                return "Accepted";
            case 3:
                return "Rejected";
            default:
                return "Apply";
        }
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.opportunity_list_item_menu:
                    showStatusPopup(position, v);
                    break;
                case R.id.opportunity_list_apply:

                    break;
            }
        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            //ShowToast.toast(activity.getApplicationContext(), "position : " + position);
            int apply_status = Integer.parseInt(jobList.get(position).getApply());
            if (apply_status == 0) {
                new Operation(position, Actions_.APPLY_JOB).execute();
            }
            if (apply_status == 1) {
                confirmationDialog(position, 1,
                        activity.getApplicationContext().getResources().getString(R.string.warning_job_apply_remove));
            }

            if (apply_status == 2) {

            }
        }
    };

    private void confirmationDialog(final int position, final int type, final String titleText) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView title = (TextView) dialog.findViewById(R.id.confirmation_dialog_description);
        title.setText(titleText);
        //title.setText(activity.getApplicationContext().getResources().getString(R.string.warning_job_remove));

        Button cancel = (Button) dialog.findViewById(R.id.confirmation_dialog_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        Button ok = (Button) dialog.findViewById(R.id.confirmation_dialog_ok_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (type) {
                    case 1:
                        new Operation(position, Actions_.UNDO_APPLICATION).execute();
                        break;
                    case 2:
                        jobList.remove(position);
                        notifyDataSetChanged();
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class Operation extends AsyncTask<Void, Void, Integer> {
        int position;
        String action;

        public Operation(int position, String action) {
            this.action = action;
            this.position = position;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.ID, jobList.get(position).getId())
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ACTION, action)
                    .build();

            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.JOB_OPERATIONS, requestBody);
                //{"success":"Job applied successfully","status":1}
                //http://dataappsinfo.com/viral/job_operations.php?id=3&user_id=5&action=apply_job
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Constants.STATUS)) {
                        result = jsonObject.getInt(Constants.STATUS);
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
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.e("onPost", "action -> " + action);
            switch (result) {
                case 0:
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;
                case 1:
                    ShowToast.successful(activity.getApplicationContext());
                    if (jobList.get(position).getApply().equalsIgnoreCase("0") &&
                            action.equalsIgnoreCase(Actions_.APPLY_JOB)) {
                        Jobs_ jobs_ = jobList.get(position);
                        jobList.remove(position);
                        jobs_.setApply("1");
                        jobList.add(position, jobs_);
                        notifyDataSetChanged();
                    }
                    if (action.equalsIgnoreCase(Actions_.UNDO_APPLICATION)) {
                        Jobs_ jobs_ = jobList.get(position);
                        jobList.remove(position);
                        jobs_.setApply("0");
                        jobList.add(position, jobs_);
                        notifyDataSetChanged();
                    }
                    break;
                case 2:
                    if (jobList.get(position).getApply().equalsIgnoreCase("0") &&
                            action.equalsIgnoreCase(Actions_.APPLY_JOB)) {
                        ShowToast.toast(activity.getApplicationContext(), "Already Applied");
                    }
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

    public void filterJobs(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        jobList.clear();
        if (charText.length() == 0) {
            jobList.addAll(list);
        } else {
            for (Jobs_ wp : list) {
                if (wp.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    jobList.add(wp);
                }
            }
        }
        if (jobList == null || jobList.size() <= 0) {
            Jobs_ jobs_ = new Jobs_();
            jobs_.setStatus(2);
            jobList.add(jobs_);
        }
        notifyDataSetChanged();
    }

    private void showStatusPopup(final int position, View view) {
        PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.job_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_job:
                        return true;
                    case R.id.remove_job:
                        confirmationDialog(position, 2,
                                activity.getApplicationContext().getResources().getString(R.string.warning_job_delete));
                        break;
                }
                return true;
            }
        });
        popup.show();
    }


    static class ViewHolder {
        TextView titleText, nameText, skillText, descriptionText, companyText, applyText, warningText;
        RelativeTimeTextView timeText;
        LinearLayout mainLayout;
    }
}