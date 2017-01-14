package com.technoindians.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.dataappsinfo.viralfame.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.technoindians.message.Details_;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 04-03-2016
 * Last Modified on 11-04-2016
 */

public class ReplyListAdapter extends ArrayAdapter<Details_> {

    private ArrayList<Details_> replyList = new ArrayList<>();
    private Activity activity;
    private ViewHolder holder;

    public ReplyListAdapter(Activity activity, ArrayList<Details_> replyList) {
        super(activity, 0, replyList);
        this.replyList = replyList;
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parrent) {

        View view = convertView;
        Details_ details_;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.message_details_list_item, parrent, false);
            holder = new ViewHolder();

            holder.fromMessage = (EmojiconTextView) view.findViewById(R.id.message_details_from_message);
            holder.fromTime = (RelativeTimeTextView) view.findViewById(R.id.message_details_from_time);
            holder.myMessage = (EmojiconTextView) view.findViewById(R.id.message_details_my_message);
            holder.myTime = (RelativeTimeTextView) view.findViewById(R.id.message_details_my_time);

            holder.myLayout = (LinearLayout) view.findViewById(R.id.message_details_my_layout);
            holder.fromLayout = (LinearLayout) view.findViewById(R.id.message_details_from_layout);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        details_ = replyList.get(position);
        //Log.e("ReplyListAdapter","getId -> "+replyList.get(position).getId());
        int self = Integer.parseInt(details_.getSelf());
        if (self == 1) {
            setMy(details_);
        } else {
            setFrom(details_);
        }
        return view;
    }


    private void setMy(Details_ myDetails) {
        holder.myLayout.setVisibility(View.VISIBLE);
        holder.fromLayout.setVisibility(View.GONE);

        holder.myMessage.setText(myDetails.getDescription());
        long time = Long.parseLong(myDetails.getDate());
        //Log.e("setMy()","time -> "+time);
        if (time < 1000000000000L) {
            time *= 1000;
        }
        holder.myTime.setReferenceTime(time);
    }

    private void setFrom(Details_ fromDetails) {
        holder.myLayout.setVisibility(View.GONE);
        holder.fromLayout.setVisibility(View.VISIBLE);

        holder.fromMessage.setText(fromDetails.getDescription());
        long time = Long.parseLong(fromDetails.getDate());
        //Log.e("setFrom()","time -> "+time);
        if (time < 1000000000000L) {
            time *= 1000;
        }
        holder.fromTime.setReferenceTime(time);
    }

    private class ViewHolder {
        EmojiconTextView myMessage, fromMessage;
        RelativeTimeTextView myTime, fromTime;
        LinearLayout myLayout, fromLayout;
    }
}