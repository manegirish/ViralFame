package technoindians.key.sticker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.technoindians.emojikeyboard.R;
import java.util.ArrayList;

public class StickerGridviewImageAdapter extends BaseAdapter {

	private ArrayList<Integer> mEmojiId;

	private Context mContext;

	private StickerClickInterface emojiClickInterface;

	public StickerGridviewImageAdapter(Context context, ArrayList<Integer> emojiId,
                                       StickerClickInterface emojiClickInterface) {
		this.mContext = context;
		this.mEmojiId = emojiId;
		this.emojiClickInterface = emojiClickInterface;

	}

	@Override
	public int getCount() {
		return mEmojiId.size();
	}

	@Override
	public Object getItem(int position) {
		return mEmojiId.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private static class Holder {
		ImageView imageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Holder holder = new Holder();
		if (null == view) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.sticker_gridview_customview, parent, false);
			holder.imageView = (ImageView) view.findViewById(R.id.ivCustoview_Gridview);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		holder.imageView.setImageResource(mEmojiId.get(position));
		final int pos = position;
		holder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emojiClickInterface.getClickedSticker(pos);
			}
		});

		return view;
	}

	public interface StickerClickInterface {
		public void getClickedSticker(int gridviewItemPosition);
	}

}