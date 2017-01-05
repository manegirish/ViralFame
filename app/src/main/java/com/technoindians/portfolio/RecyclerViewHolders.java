package com.technoindians.portfolio;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dataappsinfo.viralfame.R;

public class RecyclerViewHolders extends RecyclerView.ViewHolder {

    public ImageView countryPhoto,playIcon,likeIcon;
    public TextView likeCount;

    public RecyclerViewHolders(View itemView) {
        super(itemView);

        countryPhoto = (ImageView)itemView.findViewById(R.id.media_grid_item_icon);
        likeCount = (TextView)itemView.findViewById(R.id.media_grid_item_like);
        playIcon = (ImageView)itemView.findViewById(R.id.media_grid_play_icon);
        likeIcon = (ImageView)itemView.findViewById(R.id.media_grid_item_icon_like);
    }

/*    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Country Position = " + getPosition()+" type -> "+type, Toast.LENGTH_SHORT).show();
        if (type==2){
            Intent playIntent = new Intent(context,VideoPlayerActivity.class);
            playIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(playIntent);
        }
    }*/
}