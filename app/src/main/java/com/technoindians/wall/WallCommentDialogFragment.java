package com.technoindians.wall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.CommentListAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.database.UpdateOperations;
import com.technoindians.library.Validator_;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Wall_;
import com.technoindians.pops.ShowToast;
import com.technoindians.preferences.Preferences;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Girish Mane (girishmane8692@gmail.com)
 * created on 9/7/16
 * last modified 11/01/17
 */

public class WallCommentDialogFragment extends DialogFragment implements View.OnClickListener{

    private static final String TAG = WallCommentDialogFragment.class.getSimpleName();

    ImageView backButton, sendButton;
    RelativeLayout likeListButton;
    private String wall_id;
    private Activity activity;
    private EditText messageBox;
    private ListView listView;
    private TextView warning;

    private ArrayList<Comment_> commentList;

    private CommentListAdapter commentListAdapter;
    private UpdateOperations updateOperations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.commet_dialog_layout,null);

        activity = getActivity();
        updateOperations = new UpdateOperations(activity.getApplicationContext());
        Preferences.initialize(activity.getApplicationContext());

        commentList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle!=null){
            wall_id =  bundle.getString(Constants.ID);
        }

        listView = (ListView)view.findViewById(R.id.comment_dialog_list_view);
        warning = (TextView)view.findViewById(R.id.comment_dialog_warning);

        backButton = (ImageView)view.findViewById(R.id.comment_dialog_back);
        backButton.setOnClickListener(this);

        sendButton = (ImageView)view.findViewById(R.id.comment_dialog_send);
        sendButton.setOnClickListener(this);

        likeListButton = (RelativeLayout)view.findViewById(R.id.comment_dialog_like_layout);
        likeListButton.setOnClickListener(this);

        messageBox = (EditText)view.findViewById(R.id.comment_dialog_box);

        commentListAdapter = new CommentListAdapter(activity,commentList);
        listView.setAdapter(commentListAdapter);

        setWarning(Warnings.NO_DATA,R.drawable.ic_no_data);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_up;
        return dialog;
    }

    private void setWarning(String message,int image){
        listView.setVisibility(View.GONE);
        warning.setVisibility(View.VISIBLE);
        warning.setText(message);
        warning.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        d.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        event.getAction() == KeyEvent.ACTION_UP &&
                        !event.isCanceled()) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        d.setCancelable(false);
        new FetchComments().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.My_Dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.comment_dialog_back:
                dismiss();
                break;
            case R.id.comment_dialog_send:
                String message = messageBox.getText().toString().trim();
                if (Validator_.comment(message)==1){
                    new Comment().execute(message);
                }else {
                    messageBox.setError(Warnings.INVALID_COMMENT);
                }
                break;
            case R.id.comment_dialog_like_layout:
                Intent listIntent = new Intent(activity.getApplicationContext(),LikedListActivity.class);
                Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                        (activity.getApplicationContext(), R.anim.animation_one,R.anim.animation_two).toBundle();
                listIntent.putExtra(Constants.WALL_ID,wall_id);
                startActivity(listIntent,nextAnimation);
                break;
        }
    }

    private class Comment extends AsyncTask<String,Void,Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 11;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.ID, wall_id)
                    .add(Constants.MSG, params[0])
                    .add(Constants.ACTION, Actions_.COMMENT)
                    .build();
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL,requestBody,TAG);
                if (response!=null){
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(JsonArrays_.COMMENT)){
                        JSONObject responseObject = jsonObject.getJSONObject(JsonArrays_.COMMENT);
                        result = responseObject.getInt(Constants.STATUS);
                    }else {
                        result = 12;
                    }
                }else {
                    result = 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = 11;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer){
                    case 0:
                        ShowToast.actionFailed(activity.getApplicationContext());
                        break;
                    case 1:
                        messageBox.setText("");
                        ShowToast.successful(activity.getApplicationContext());
                        new FetchComments().execute();
                        break;
                    case 2:
                        ShowToast.actionFailed(activity.getApplicationContext());
                        break;
                    case 11:
                        ShowToast.networkProblemToast(activity.getApplicationContext());
                        break;
                    case 12:
                        ShowToast.internalErrorToast(activity.getApplicationContext());
                        break;
            }
        }
    }

    private class FetchComments extends AsyncTask<Void,Void,Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setWarning("Loading..... ",R.drawable.ic_no_data);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 11;
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, Preferences.get(Constants.USER_ID))
                    .add(Constants.TIMEZONE, Preferences.get(Constants.TIMEZONE))
                    .add(Constants.ID, wall_id)
                    .add(Constants.ACTION, Actions_.GET_COMMENT)
                    .build();
           // Log.e(TAG,"------>");
            try {
                String response = MakeCall.post(Urls.DOMAIN + Urls.POST_OPERATIONS_URL,requestBody,TAG);
                if (response!=null){
                    result = Wall_.commentResult(response);
                    if (result==1){
                        commentList = Wall_.parseComment(response);
                    }
                }else {
                    result = 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = 11;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer){
                case 0:
                    ShowToast.noData(activity.getApplicationContext());
                    setWarning(Warnings.NO_DATA,R.drawable.ic_no_data);
                    break;
                case 1:
                    commentListAdapter.clear();
                    listView.setVisibility(View.VISIBLE);
                    warning.setVisibility(View.GONE);
                    commentListAdapter = new CommentListAdapter(activity,commentList);
                    listView.setAdapter(commentListAdapter);
                    updateOperations.updateFeed(wall_id, Constants.TOTAL_COMMENTS, "" + commentList.size());
                    break;
                case 2:
                    ShowToast.noData(activity.getApplicationContext());
                    setWarning(Warnings.NO_DATA,R.drawable.ic_no_data);
                    break;
                case 11:
                    ShowToast.networkProblemToast(activity.getApplicationContext());
                    setWarning(Warnings.NETWORK_ERROR_WARNING,R.drawable.ic_network_problem);
                    break;
                case 12:
                    setWarning(Warnings.INTERNAL_ERROR_WARNING,R.drawable.ic_network_problem);
                    ShowToast.internalErrorToast(activity.getApplicationContext());
                    break;
            }
        }
    }
}
