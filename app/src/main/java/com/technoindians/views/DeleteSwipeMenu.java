/*
 * Copyright (c) 2016
 * Girish D Mane (girishmane8692@gmail.com)
 * Gurujot Singh Pandher (gsp11111992@gmail.com)
 * All rights reserved.
 * This application code can not be used directly without prior permission of owners.
 *
 */

package com.technoindians.views;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.dataappsinfo.viralfame.R;


/**
 * Created by girish on 8/7/16.
 */
public class DeleteSwipeMenu implements SwipeMenuCreator {

    private Activity activity;

    public DeleteSwipeMenu(Activity activity){
        this.activity=activity;
    }

    @Override
    public void create(com.baoyz.swipemenulistview.SwipeMenu menu) {
        SwipeMenuItem deleteItem = new SwipeMenuItem(activity.getApplicationContext());
        // set item background
        deleteItem.setBackground(new ColorDrawable(activity.getApplicationContext()
                .getResources().getColor(R.color.colorPrimaryDark)));
        // set item width
        deleteItem.setWidth(dp2px(80));
        // set a icon
        deleteItem.setIcon(R.drawable.ic_delete);
        // add to menu
        menu.addMenuItem(deleteItem);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                activity.getResources().getDisplayMetrics());
    }
}
