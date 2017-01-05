package technoindians.key.emoji.mapping;

import java.util.ArrayList;

public class RecentlyUsedEmoji {

	public static ArrayList<Integer> emojiDrawableArrayListRecentCategory = new ArrayList<>();

	public static ArrayList<String> emojiUnicodeArrayListRecentcategory = new ArrayList<>();

	private final static int recentlyUsedEmojiCount = 25;

	public static void addEmoji(String unicode, Integer drawableId, RefreshGridViewAdapterListener listener) {
		int arrayListSize = emojiDrawableArrayListRecentCategory.size();

		if (!emojiDrawableArrayListRecentCategory.contains(drawableId)) {
			if (arrayListSize < recentlyUsedEmojiCount) {
				emojiDrawableArrayListRecentCategory.add(0, drawableId);
				emojiUnicodeArrayListRecentcategory.add(0, unicode);
			} else {
				emojiDrawableArrayListRecentCategory.remove(recentlyUsedEmojiCount - 1);
				emojiUnicodeArrayListRecentcategory.remove(recentlyUsedEmojiCount - 1);
				emojiDrawableArrayListRecentCategory.add(0, drawableId);
				emojiUnicodeArrayListRecentcategory.add(0, unicode);
			}

		} else {
			// this will move existing emoji to first position
			emojiDrawableArrayListRecentCategory.remove(drawableId);
			emojiUnicodeArrayListRecentcategory.remove(unicode);
			emojiDrawableArrayListRecentCategory.add(0, drawableId);
			emojiUnicodeArrayListRecentcategory.add(0, unicode);
		}
		listener.onNewEmojiUsed();
	}

	public interface RefreshGridViewAdapterListener {
		void onNewEmojiUsed();
	}

}
