package com.technoindians.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dataappsinfo.viralfame.R;
import com.squareup.picasso.Picasso;
import com.technoindians.network.Urls;
import com.technoindians.portfolio.Images_;
import com.technoindians.portfolio.RecyclerViewHolders;

import java.util.ArrayList;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 04-03-2016
 * Last Modified on 24-08-2016
 */


public class ImageGalleryAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private ArrayList<Images_> imagesList;
    private Context context;
    private int type;

    public ImageGalleryAdapter(Context context, ArrayList<Images_> imagesList, int type) {
        this.imagesList = imagesList;
        this.context = context;
        this.type=type;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {

        String like_count = imagesList.get(position).getLikeCount();
        if (Integer.parseInt(like_count)>0){
            holder.likeIcon.setImageResource(R.drawable.ic_like_p);
            holder.likeCount.setText(like_count);
            holder.likeCount.setVisibility(View.VISIBLE);
        }else {
            holder.likeIcon.setImageResource(R.drawable.ic_like_g);
            holder.likeCount.setVisibility(View.GONE);
        }


        if (type == 1){
            holder.playIcon.setVisibility(View.GONE);
            Picasso.with(context)
                    .load(Urls.DOMAIN+imagesList.get(position).getPath())
                    .fit()
                    .placeholder(R.drawable.ic_dummy_wall)
                    .error(R.drawable.ic_dummy_wall)
                    .into(holder.countryPhoto);
        }

        if (type==2){
            holder.playIcon.setVisibility(View.GONE);
            holder.countryPhoto.setBackgroundResource(R.drawable.ic_video_play);
        }

        if (type == 3){
            holder.playIcon.setVisibility(View.GONE);
            holder.countryPhoto.setBackgroundResource(R.drawable.ic_audio_play_1);
        }
       // Log.e("ImageGalleryAdapter","type -> "+type);
    }

    @Override
    public int getItemCount() {
        return this.imagesList.size();
    }
}
