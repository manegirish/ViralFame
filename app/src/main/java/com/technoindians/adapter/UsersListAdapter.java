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
import com.squareup.picasso.Picasso;
import com.technoindians.constants.Warnings;
import com.technoindians.message.Friends_;
import com.technoindians.network.Urls;
import com.technoindians.views.CircleTransformMain;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Girish Mane <girishmane8692@gmail.com>
 *         Created on 04-03-2016
 *         Last Modified on 11-04-2016
 */

public class UsersListAdapter extends ArrayAdapter<Friends_> {
    private ArrayList<Friends_> friendList = null;
    private ArrayList<Friends_> list;
    private Activity activity;

    public UsersListAdapter(Activity activity, ArrayList<Friends_> friendList) {
        super(activity, 0, friendList);
        this.friendList = friendList;
        this.activity = activity;
        this.list = new ArrayList<>();
        this.list.addAll(friendList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        Friends_ friends_;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.users_list_item, parent, false);
            holder = new ViewHolder();

            holder.skillText = (TextView) view.findViewById(R.id.users_list_item_skill);
            holder.icon = (ImageView) view.findViewById(R.id.users_list_item_icon);
            holder.nameText = (TextView) view.findViewById(R.id.users_list_item_name);
            holder.warningText = (TextView) view.findViewById(R.id.users_list_item_warning);
            holder.mainLayout = (LinearLayout) view.findViewById(R.id.users_list_item_main);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        friends_ = friendList.get(position);
        if (friends_.getStatus() != 1) {
            holder.mainLayout.setVisibility(View.GONE);
            holder.warningText.setVisibility(View.VISIBLE);
            holder.warningText.setText(Warnings.NO_RESULT_FOUND);
            return view;
        }
        holder.mainLayout.setVisibility(View.VISIBLE);
        holder.warningText.setVisibility(View.GONE);

        holder.nameText.setText(friends_.getName());
        if (Integer.parseInt(friends_.getType()) == 2) {
            holder.nameText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verify_user, 0, 0, 0);
        } else {
            holder.nameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        holder.skillText.setText(friends_.getSkill());

        Picasso.with(activity.getApplicationContext())
                .load(Urls.DOMAIN + friends_.getImages())
                .transform(new CircleTransformMain())
                .placeholder(R.drawable.ic_avtar)
                .error(R.drawable.ic_avtar)
                .into(holder.icon);

        return view;
    }

    @Override
    public Friends_ getItem(int position) {
        return friendList.get(position);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        friendList.clear();
        if (charText.length() == 0) {
            friendList.addAll(list);
        } else {
            for (Friends_ wp : list) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    friendList.add(wp);
                }
            }
        }
        if (friendList == null || friendList.size() <= 0) {
            Friends_ friends_ = new Friends_();
            friends_.setStatus(2);
            friendList.add(friends_);
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView skillText, nameText, warningText;
        ImageView icon;
        LinearLayout mainLayout;
    }
}