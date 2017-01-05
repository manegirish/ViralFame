package com.technoindians.library;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 25/06/2016
 * Last modified 15/07/2016
 *
 */

public class ShowLoader {

    ProgressDialog progress;
    private Activity activity;
    public ShowLoader(Activity activity){
        this.activity=activity;
    }

    public void sendSendingDialog() {
        if (progress == null) {
            progress = new ProgressDialog(activity);
            progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progress.setMessage("Sending....");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.setIndeterminate(false);
            progress.show();
        }else {
            progress.show();
        }
    }

    public void dismissSendingDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void sendLoadingDialog() {
        if (progress == null) {
            progress = new ProgressDialog(activity);
            progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progress.setMessage("Loading....");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.setIndeterminate(false);
            progress.show();
        }else {
            progress.show();
        }
    }

    public void dismissLoadingDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void showPostingDialog() {
        if (progress == null) {
            progress = new ProgressDialog(activity);
            progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progress.setMessage("Posting....");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.setIndeterminate(false);
            progress.show();
        }else {
            progress.show();
        }
    }

    public void dismissPostingDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void showUploadingDialog() {
        if (progress == null) {
            progress = new ProgressDialog(activity);
            progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progress.setMessage("Uploading....");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.setIndeterminate(false);
            progress.show();
        }else {
            progress.show();
        }
    }

    public void dismissUploadingDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
}
