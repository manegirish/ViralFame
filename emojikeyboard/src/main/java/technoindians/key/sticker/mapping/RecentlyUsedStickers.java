package technoindians.key.sticker.mapping;

import java.util.ArrayList;

public class RecentlyUsedStickers {
	public static ArrayList<Integer> stickerDrawableArrayListRecentCategory = new ArrayList<>();

	public static ArrayList<String> stickerUnicodeArrayListRecentcategory = new ArrayList<>();

	private final static int recentlyUsedStickerCount = 30;

	public static void addSticker(String unicode, Integer drawableId, RefreshGridViewAdapterListener listener) {
		int arrayListSize = stickerDrawableArrayListRecentCategory.size();

		if (!stickerDrawableArrayListRecentCategory.contains(drawableId)) {
			if (arrayListSize < recentlyUsedStickerCount) {
				stickerDrawableArrayListRecentCategory.add(0, drawableId);
				stickerUnicodeArrayListRecentcategory.add(0, unicode);
			} else {
				stickerDrawableArrayListRecentCategory.remove(recentlyUsedStickerCount - 1);
				stickerUnicodeArrayListRecentcategory.remove(recentlyUsedStickerCount - 1);
				stickerDrawableArrayListRecentCategory.add(0, drawableId);
				stickerUnicodeArrayListRecentcategory.add(0, unicode);
			}

		} else {
			stickerDrawableArrayListRecentCategory.remove(drawableId);
			stickerUnicodeArrayListRecentcategory.remove(unicode);
			stickerDrawableArrayListRecentCategory.add(0, drawableId);
			stickerUnicodeArrayListRecentcategory.add(0, unicode);
		}
		listener.onNewStickerUsed();
	}

	public interface RefreshGridViewAdapterListener {
		public void onNewStickerUsed();
	}
}
