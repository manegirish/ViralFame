package technoindians.key.emoji.mapping;

import java.util.HashMap;

public class EmojiHashmapUnicodeToDrawable {

	public static HashMap<String, Integer> emojiMap = new HashMap<>();

	static {

		int arrayListSize, i;

		arrayListSize = EmojiDrawableArrayListPeopleCategory.emojiPeople.size();
		for (i = 0; i < arrayListSize; i++) {
			emojiMap.put(EmojiUnicodeArrayListPeopleCategory.unicodePeople.get(i),
					EmojiDrawableArrayListPeopleCategory.emojiPeople.get(i));
		}

		arrayListSize = EmojiDrawableArrayListNatureCategory.emojiNature.size();
		for (i = 0; i < arrayListSize; i++) {
			emojiMap.put(EmojiUnicodeArrayListNatureCategory.unicodeNature.get(i),
					EmojiDrawableArrayListNatureCategory.emojiNature.get(i));
		}

		arrayListSize = EmojiDrawableArrayListObjectsCategory.emojiObjects.size();
		for (i = 0; i < arrayListSize; i++) {
			emojiMap.put(EmojiUnicodeArrayListObjectsCategory.unicodeObjects.get(i),
					EmojiDrawableArrayListObjectsCategory.emojiObjects.get(i));
		}

		arrayListSize = EmojiDrawableArrayListCarCategory.emojicars.size();
		for (i = 0; i < arrayListSize; i++) {
			emojiMap.put(EmojiUnicodeArrayListCarCategory.unicodeCar.get(i),
					EmojiDrawableArrayListCarCategory.emojicars.get(i));
		}

		arrayListSize = EmojiDrawableArrayListSymbolsCategory.emojiSymbols.size();

		for (i = 0; i < arrayListSize; i++) {
			emojiMap.put(EmojiUnicodeArrayListSymbolsCategory.unicodeSymbols.get(i),
					EmojiDrawableArrayListSymbolsCategory.emojiSymbols.get(i));
		}

	}
}
