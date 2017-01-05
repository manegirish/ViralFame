package technoindians.key.emoji.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.Spanned;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import technoindians.key.emoji.mapping.EmojiHashmapUnicodeToDrawable;


@SuppressWarnings("unchecked")
public class EmoticonUtils {
	private static final Factory spannableFactory = Factory.getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	private static HashMap<String, Integer> emojiMap = new HashMap<String, Integer>();

	private final static int maxElements = 100;
	private static LinkedHashMap<CharSequence, Spannable> processedEmoji = new LinkedHashMap<CharSequence, Spannable>(
			maxElements, 0.75F, false) {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<CharSequence, Spannable> eldest) {
			return size() > maxElements;
		};
	};

	static {
		emojiMap = EmojiHashmapUnicodeToDrawable.emojiMap;
		@SuppressWarnings("rawtypes")
		Iterator iterator = emojiMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> pairs = (Entry<String, Integer>) iterator.next();
			addPattern(emoticons, pairs.getKey(), pairs.getValue());
		}
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile, int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	private static Spannable addSmiles(Context context, Spannable spannable, int emojiSize) {
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start() && spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					Drawable drawable = context.getResources().getDrawable(entry.getValue());
					drawable.setBounds(0, 0, emojiSize, emojiSize);
					spannable.setSpan(new ImageSpan(drawable), matcher.start(), matcher.end(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return spannable;
	}

	public static Spannable getSmiledText(Context context, CharSequence text, int emojiSize) {
		if (processedEmoji.containsKey(text)) {
			return processedEmoji.get(text);
		} else {
			Spannable spannable = spannableFactory.newSpannable(text);
			spannable = addSmiles(context, spannable, emojiSize);
			processedEmoji.put(text, spannable);
			return spannable;
		}
	}

}
