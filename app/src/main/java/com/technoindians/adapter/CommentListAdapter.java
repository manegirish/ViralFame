package com.technoindians.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.technoindians.library.TimeConverter;
import com.technoindians.wall.Comment_;

import java.util.ArrayList;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 04-03-2016
 * Last Modified on 11-04-2016
 */

public class CommentListAdapter extends ArrayAdapter<Comment_>
{
	private ArrayList<Comment_> feedList = new ArrayList<>();
	private Activity activity;

	public CommentListAdapter(Activity activity, ArrayList<Comment_> feedList) {
		super(activity, 0,feedList);
		this.feedList=feedList;
		this.activity=activity;
	}

   @Override
   public View getView(final int position, View convertView, ViewGroup parrent)
   	{
		final ViewHolder holder;
		View view=convertView;
		Comment_ comment_;
       if (view==null)
       	{
    	   LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view=layoutInflater.inflate(R.layout.comment_item_layout, parrent, false);
           holder=new ViewHolder();

			holder.commentText = (TextView)view.findViewById(R.id.comment_item_text);
            holder.postImage = (ImageView)view.findViewById(R.id.comment_item_icon);
            holder.timeText = (TextView)view.findViewById(R.id.comment_item_time);

           view.setTag(holder);
       }else{
    	   holder=(ViewHolder)view.getTag();
       }
        comment_ = feedList.get(position);

        String time = TimeConverter.getWallTime(Long.parseLong(comment_.getLast_updated()));
        holder.timeText.setText(time);

        holder.commentText.setTag(position);
        holder.commentText.setText(comment_.getComment());

        return view;
    }


	private class ViewHolder {
		TextView commentText,nameText,timeText;
        ImageView postImage;
	}
}