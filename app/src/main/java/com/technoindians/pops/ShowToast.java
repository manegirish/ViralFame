package com.technoindians.pops;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.technoindians.constants.Warnings;

/**
 * Created by girish on 1/7/16.
 */
public class ShowToast {
    public static void toast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,80);
        toast.show();
    }

    public static void slowNetworkToast(Context context) {
        Toast toast = Toast.makeText(context, Warnings.SLOW_NETWORK_WARNING,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }

    public static void actionFailed(Context context) {
        Toast toast = Toast.makeText(context, "Action Failed",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }

    public static void noData(Context context) {
        Toast toast = Toast.makeText(context, "No data",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }

    public static void successful(Context context) {
        Toast toast = Toast.makeText(context, "Successful",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }

    public static void follow(Context context) {
        Toast toast = Toast.makeText(context, "Follow",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }
    public static void unfollow(Context context) {
        Toast toast = Toast.makeText(context, "Unfollow",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }
    public static void networkProblemToast(Context context) {
        Toast toast = Toast.makeText(context, Warnings.NETWORK_ERROR_WARNING,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }
    public static void internalErrorToast(Context context) {
        Toast toast = Toast.makeText(context, Warnings.INTERNAL_ERROR_WARNING,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }
    public static void fileNotFound(Context context) {
        Toast toast = Toast.makeText(context, "File Not Found",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 90);
        toast.show();
    }
}
