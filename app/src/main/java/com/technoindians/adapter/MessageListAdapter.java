package com.technoindians.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Warnings;
import com.technoindians.message.Message_;
import com.technoindians.network.Urls;
import com.technoindians.views.CircleTransformMain;

import java.util.ArrayList;
import java.util.Locale;

import technoindians.key.emoji.custom.EmojiTextView;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 04-03-2016
 * Last Modified on 11-04-2016
 */

public class MessageListAdapter extends ArrayAdapter<Message_>
{
	private ArrayList<Message_> messageList = null;
	private ArrayList<Message_> list;
	private Activity activity;

	public MessageListAdapter(Activity activity, ArrayList<Message_> messageList) {
		super(activity, 0,messageList);
		this.messageList=messageList;
		this.activity=activity;
        this.list=new ArrayList<>();
        this.list.addAll(messageList);
	}

    @Override
    public Message_ getItem(int position) {
        return messageList.get(position);
    }

    @Override
   public View getView(final int position, View convertView, ViewGroup parrent)
   	{
		ViewHolder holder;
		View view=convertView;
       if (view==null)
       	{
    	   LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           	view=layoutInflater.inflate(R.layout.message_list_item, parrent, false);
           	holder=new ViewHolder();

            holder.messageText = (EmojiTextView) view.findViewById(R.id.message_list_item_message);
			holder.timeText = (RelativeTimeTextView)view.findViewById(R.id.message_list_item_time);
			holder.nameText = (TextView)view.findViewById(R.id.message_list_item_name);
			holder.countText = (TextView)view.findViewById(R.id.message_list_item_count);
			holder.icon = (ImageView)view.findViewById(R.id.message_list_item_icon);
			holder.warningText = (TextView)view.findViewById(R.id.message_list_item_warning);
            holder.mainLayout = (LinearLayout)view.findViewById(R.id.message_list_item_main_layout);

           	view.setTag(holder);
       }else{
    	   holder=(ViewHolder)view.getTag();
       }

		Message_ messages_ = messageList.get(position);
        if (messages_.getStatus()!=1){
            holder.warningText.setVisibility(View.VISIBLE);
            holder.warningText.setText(Warnings.NO_RESULT_FOUND);
            holder.mainLayout.setVisibility(View.GONE);
            return view;
        }
        holder.warningText.setVisibility(View.GONE);
        holder.mainLayout.setVisibility(View.VISIBLE);

		long time = Long.parseLong(messages_.getDate());
		if (time < 1000000000000L) {
			time *= 1000;
		}

        holder.messageText.setEmojiText(messages_.getMessage());
		holder.timeText.setReferenceTime(time);
        holder.nameText.setText(messages_.getName());
		int read = Integer.parseInt(messages_.getRead());
		if (read==0){
            holder.nameText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.black_95));
            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.white));
        	holder.messageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_msg,0,0,0);
			holder.countText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.colorPrimary));
		}else {
			holder.countText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.black_75));
            holder.nameText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.black_75));
		    view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.black_ee));
			holder.messageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_msg_read,0,0,0);
		}
		int reply_count = Integer.parseInt(messages_.getCount());
		if (reply_count<=0){
			holder.countText.setVisibility(View.GONE);
			holder.countText.setText(""+reply_count);
		}else {
			holder.countText.setVisibility(View.VISIBLE);
			holder.countText.setText(""+reply_count);
		}

		Picasso.with(activity.getApplicationContext())
				.load(Urls.DOMAIN+messages_.getProfile_pic())
				.transform(new CircleTransformMain())
				.placeholder(R.drawable.ic_avtar)
				.error(R.drawable.ic_avtar)
				.into(holder.icon);

        return view;
    }

    public void filterMessage(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        messageList.clear();
        if (charText.length() == 0) {
            messageList.addAll(list);
        }
        else
        {
            for (Message_ wp : list)
            {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    messageList.add(wp);
                }
            }
        }
		if (messageList==null||messageList.size()<=0){
			Message_ message_ = new Message_();
			message_.setStatus(2);
			messageList.add(message_);
		}
        notifyDataSetChanged();
    }

	static class ViewHolder {
		TextView nameText,countText,warningText;
		EmojiTextView messageText;
		RelativeTimeTextView timeText;
        LinearLayout mainLayout;
		ImageView icon;
	}
}