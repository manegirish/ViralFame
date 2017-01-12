package com.technoindians.pops;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.technoindians.constants.Warnings;

/**
 * Created by root on 12/12/16.
 */

public class ShowSnack {

    public static void snack(View view,String message){

    }

    public static void noInternet(View view){
        Snackbar snackbar = Snackbar.make(view, Warnings.NO_INTERNET, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.show();
    }
}
