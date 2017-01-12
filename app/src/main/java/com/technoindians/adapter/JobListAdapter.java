package com.technoindians.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.technoindians.constants.Warnings;
import com.technoindians.network.CheckInternet;
import com.technoindians.opportunities.Jobs_;
import com.technoindians.opportunities.SentListFragment;
import com.technoindians.pops.ShowSnack;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 04-03-2016
 *         Last Modified on 11-04-2016
 */

public class JobListAdapter extends ArrayAdapter<Jobs_> {

    private ArrayList<Jobs_> jobList = null;
    private ArrayList<Jobs_> list;
    private Activity activity;
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            if (CheckInternet.check()) {
                switch (v.getId()) {
                    case R.id.opportunity_list_item_menu:
                        showStatusPopup(position, v);
                        break;
                }
            }else {
                ShowSnack.noInternet(v);
            }
        }
    };

    public JobListAdapter(Activity activity, ArrayList<Jobs_> jobList) {
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
            view = layoutInflater.inflate(R.layout.opportunity_list_item, parrent, false);
            holder = new ViewHolder();

            holder.titleText = (TextView) view.findViewById(R.id.opportunity_list_item_title);
            holder.timeText = (RelativeTimeTextView) view.findViewById(R.id.opportunity_list_item_time);
            holder.nameText = (TextView) view.findViewById(R.id.opportunity_list_item_name);
            holder.companyText = (TextView) view.findViewById(R.id.opportunity_list_item_firm);
            holder.menuButton = (ImageView) view.findViewById(R.id.opportunity_list_item_menu);
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
        holder.menuButton.setTag(position);

        int count = 0;
        if (jobs_.getApply() != null || jobs_.getApply().length() > 0) {
            count = Integer.parseInt(jobs_.getApply());
        }
        String message = count + "";
        holder.applyText.setText(message);
        if (count > 0) {
            if (count > 99) {
                message = "99+";
            }
            holder.applyText.setBackgroundResource(R.drawable.green_circle);
            holder.applyText.setPadding(0, 0, 0, 0);
            holder.applyText.setText(message);
            holder.applyText.setVisibility(View.VISIBLE);
        } else {
            holder.applyText.setVisibility(View.GONE);
        }
        holder.menuButton.setVisibility(View.VISIBLE);
        holder.menuButton.setEnabled(true);
        holder.menuButton.setOnClickListener(onClick);


        holder.titleText.setText(jobs_.getTitle());
        holder.descriptionText.setText(jobs_.getDescription());
        holder.nameText.setText(jobs_.getName());
        holder.companyText.setText(jobs_.getOrganisation());
        holder.skillText.setText(jobs_.getSkillPrimary());
        holder.timeText.setReferenceTime(time);


        return view;
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
                        SentListFragment.sentListFragment.removeJob(position);
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
        ImageView menuButton;
    }
}