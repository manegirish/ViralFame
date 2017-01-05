package technoindians.key.emoji.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.TextView;
import com.technoindians.emojikeyboard.R;

public class EmojiTextView extends TextView {

	private Context context;
	private int emojiSize;

	public EmojiTextView(Context context) {
		super(context);
		this.context = context;
		/*
		 * emojiSize = (int)
		 * context.getResources().getDimension(R.dimen.emoji_size) / (int)
		 * context.getResources().getDisplayMetrics().density + 7;
		 */
		emojiSize = Integer.parseInt(context.getResources().getString(R.string.emoji_size_actual)) + 7;

	}

	public EmojiTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		/*
		 * emojiSize = (int)
		 * context.getResources().getDimension(R.dimen.emoji_size) / (int)
		 * context.getResources().getDisplayMetrics().density + 7;
		 */
		emojiSize = Integer.parseInt(context.getResources().getString(R.string.emoji_size_actual)) + 7;

	}

	public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		/*
		 * emojiSize = (int)
		 * context.getResources().getDimension(R.dimen.emoji_size) / (int)
		 * context.getResources().getDisplayMetrics().density + 7;
		 */
		emojiSize = Integer.parseInt(context.getResources().getString(R.string.emoji_size_actual)) + 7;
	}

	public void setEmojiText(String text) {
		// text = text.replace("\\u0026", "&").replace("\\u0025",
		// "%").replace("\\u002B", "+");
		CharSequence spanned = EmoticonUtils.getSmiledText(context, text, emojiSize);
		setText(spanned);
	}

	public void setEmojiText(SpannableString text) {
		// text = text.replace("\\u0026", "&").replace("\\u0025",
		// "%").replace("\\u002B", "+");
		CharSequence spanned = EmoticonUtils.getSmiledText(context, text, emojiSize);
		setText(spanned);
	}

	private ImageGetter emojiGetter = new ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Drawable emoji = getResources().getDrawable(
					getResources().getIdentifier(source, "drawable", context.getPackageName()));
			emoji.setBounds(0, 0, emojiSize, emojiSize);
			return emoji;
		}
	};

/*	public CharSequence convertedText(String text) {
		String convetredText = EmojiUtils.convertTag(text);
		CharSequence spanned = Html.fromHtml(convetredText, emojiGetter, null);
		return spanned;
	}*/

}