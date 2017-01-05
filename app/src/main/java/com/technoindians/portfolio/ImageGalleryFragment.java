package com.technoindians.portfolio;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dataappsinfo.viralfame.R;
import com.technoindians.adapter.ImageGalleryAdapter;
import com.technoindians.constants.Actions_;
import com.technoindians.constants.Constants;
import com.technoindians.constants.Warnings;
import com.technoindians.network.JsonArrays_;
import com.technoindians.network.MakeCall;
import com.technoindians.network.Urls;
import com.technoindians.parser.Portfolio_;
import com.technoindians.preferences.Preferences;
import com.technoindians.views.RecyclerItemClickListener;
import java.util.ArrayList;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Girish M
 * Created on 03/08/16.
 * Last modified 01/08/2016
 *
 */

public class ImageGalleryFragment extends Fragment {

    public static final String TAG = ImageGalleryFragment.class.getSimpleName();

    private TextView warningText;
    private ArrayList<Images_> imagesList;
    private String user_id;

    private Activity activity;
    private GridLayoutManager lLayout;
    RecyclerView rView;
    ImageGalleryAdapter imageGalleryAdapter;
    // private ShowLoader showLoader;

    public static ImageGalleryFragment newInstance(String user_id) {
        ImageGalleryFragment fragment = new ImageGalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID,user_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collapsing_tab_fragment,null);

        activity = getActivity();
        Preferences.initialize(activity.getApplicationContext());

        Bundle data = getArguments();
        if (data!=null){
            user_id = data.getString(Constants.USER_ID);
        }else {
            user_id = Preferences.get(Constants.USER_ID);
        }

        lLayout = new GridLayoutManager(getActivity(), 3);
        imagesList = new ArrayList<>();

        warningText = (TextView)view.findViewById(R.id.gallery_warning_text);
        rView = (RecyclerView)view.findViewById(R.id.recycler_view);

        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        rView.addOnItemTouchListener(
                new RecyclerItemClickListener(activity.getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                                (activity.getApplicationContext(), R.anim.animation_bottom_to_up,R.anim.animation_top_to_bottom).toBundle();

                        Intent detailsIntent = new Intent(activity.getApplicationContext(),FeedDetailsActivity.class);
                        detailsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        detailsIntent.putExtra(Constants.MEDIA_FILE,imagesList.get(position).getPath());
                        detailsIntent.putExtra(Constants.WALL_ID,imagesList.get(position).getWallId());
                        detailsIntent.putExtra(Constants.MEDIA_TYPE,Constants.TYPE_IMAGE);
                        activity.startActivity(detailsIntent,nextAnimation);
                    }
                })
        );
        setWarning("Loading.....",R.drawable.ic_data);

        return view;
    }

    protected void setWarning(String message,int image){

        rView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetImages().execute();
    }

    private class GetImages extends AsyncTask<Void,Void,Integer>{
        String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setWarning("Loading.....",R.drawable.ic_data);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            RequestBody requestBody = new FormBody.Builder()
                    .add(Constants.USER_ID, user_id)
                    .add(Constants.ACTION, Actions_.GET_IMAGES)
                    .build();
            try {
                response = MakeCall.post(Urls.DOMAIN + Urls.PORTFOLIO_OPERATIONS, requestBody);
                return Portfolio_.galleryResult(response, JsonArrays_.GET_IMAGES);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 12;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer){
                case 0:
                    setWarning(Warnings.NO_IMAGE_SHARED,R.drawable.ic_no_data);
                    break;
                case 1:
                    warningText.setVisibility(View.GONE);
                    rView.setVisibility(View.VISIBLE);
                    new LoadData().execute(response);
                    break;
                case 2:
                    setWarning(Warnings.NO_IMAGE_SHARED,R.drawable.ic_no_data);
                    break;
                case 11:
                    setWarning(Warnings.INTERNAL_ERROR_WARNING,R.drawable.ic_network_problem);
                    break;
                case 12:
                    setWarning(Warnings.NETWORK_ERROR_WARNING,R.drawable.ic_network_problem);
                    break;
            }
        }
    }
    private class LoadData extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
                imagesList = Portfolio_.galleryImage(params[0],JsonArrays_.GET_IMAGES);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("onPost","size -> "+imagesList.size());
            if (imagesList!=null&&imagesList.size()>0){
                warningText.setVisibility(View.GONE);
                imageGalleryAdapter = new ImageGalleryAdapter(activity.getApplicationContext(),
                        imagesList,1);
                rView.setAdapter(imageGalleryAdapter);
            }
        }
    }
}
