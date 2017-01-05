package com.technoindians.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;

import java.util.ArrayList;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 04-08-2016
 * Last Modified on 04-08-2016
 */

public class DefaultMeListAdapter extends ArrayAdapter<String>
{
	private ArrayList<String> meList = null;
    private Activity activity;

	public DefaultMeListAdapter(Activity activity, ArrayList<String> meList) {
		super(activity, 0,meList);
		this.meList = meList;
		this.activity=activity;
	}

   @Override
   public View getView(final int position, View convertView, ViewGroup parent)
   	{
		final ViewHolder holder;
		View view=convertView;
       if (view==null)
       	{
    	   LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view=layoutInflater.inflate(R.layout.default_me_list_item, parent, false);
           holder=new ViewHolder();

            holder.titleText = (TextView)view.findViewById(R.id.default_me_item_text);
           view.setTag(holder);
       }else{
    	   holder=(ViewHolder)view.getTag();
       }
        holder.titleText.setText(meList.get(position));
        return view;
    }

	private class ViewHolder {
		TextView titleText;
	}
}