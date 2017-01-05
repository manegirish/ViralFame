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
import java.util.ArrayList;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 18/7/16.
 * Last modified 01/08/2016
 *
 */

public class DrawerListAdapter extends ArrayAdapter<String> {
    private Activity activity;
    private ArrayList<String> menuList = new ArrayList<>();
    private int [] iconSet;
    private ArrayList<Integer> counterList = new ArrayList<>();

    public DrawerListAdapter(Activity activity, ArrayList<String> menuList, int [] iconSet, ArrayList<Integer> counterList) {
        super(activity, 0, menuList);
        this.activity = activity;
        this.menuList = menuList;
        this.iconSet = iconSet;
        this.counterList = counterList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parrent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(R.layout.drawer_list_item, parrent, false);

            holder = new ViewHolder();

            holder.titleText = (TextView) view.findViewById(R.id.drawer_list_item_title);
            holder.counterText = (TextView) view.findViewById(R.id.drawer_list_item_counter);
            holder.icon = (ImageView) view.findViewById(R.id.drawer_list_item_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.titleText.setText(menuList.get(position));
        holder.icon.setImageResource(iconSet[position]);

        if (counterList.get(position) <= 0) {
            holder.counterText.setText("");
            holder.counterText.setVisibility(View.GONE);
        } else {
            holder.counterText.setText("" + counterList.get(position));
            holder.counterText.setVisibility(View.VISIBLE);
        }

        return view;
    }

    static class ViewHolder {
        TextView titleText, counterText;
        ImageView icon;
    }
}

