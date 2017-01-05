package technoindians.key.sticker.mapping;

import java.util.HashMap;

public class StickerHashmapPatternToDrawable {
    public static HashMap<String, Integer> stickerMap = new HashMap<>();
    static {

        int arrayListSize, i;

        arrayListSize = StickerDrawableArrayListBear.stickerBear.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListBear.stickerBear.get(i),
                    StickerDrawableArrayListBear.stickerBear.get(i));
        }

        arrayListSize = StickerDrawableArrayListGeek.stickerGeek.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListGeek.stickerGeek.get(i),
                    StickerDrawableArrayListGeek.stickerGeek.get(i));
        }

        arrayListSize = StickerDrawableArrayListGery.stickerGery.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListGery.stickerGery.get(i),
                    StickerDrawableArrayListGery.stickerGery.get(i));
        }

        arrayListSize = StickerDrawableArrayListGery.stickerGery.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListGery.stickerGery.get(i),
                    StickerDrawableArrayListGery.stickerGery.get(i));
        }

        arrayListSize = StickerDrawableArrayListHappyGhost.stickerHappyGhost.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListHappyGhost.stickerHappyGhost.get(i),
                    StickerDrawableArrayListHappyGhost.stickerHappyGhost.get(i));
        }

        arrayListSize = StickerDrawableArrayListLittleDyno.stickerLittleDyno.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListLittleDyno.stickerLittleDyno.get(i),
                    StickerDrawableArrayListLittleDyno.stickerLittleDyno.get(i));
        }

        arrayListSize = StickerDrawableArrayListMomon.stickerMomon.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListMomon.stickerMomon.get(i),
                    StickerDrawableArrayListMomon.stickerMomon.get(i));
        }

        arrayListSize = StickerDrawableArrayListMonster.stickerMonster.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListMonster.stickerMonster.get(i),
                    StickerDrawableArrayListMonster.stickerMonster.get(i));
        }

        arrayListSize = StickerDrawableArrayListPenguin.stickerPenguin.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListPenguin.stickerPenguin.get(i),
                    StickerDrawableArrayListPenguin.stickerPenguin.get(i));
        }

        arrayListSize = StickerDrawableArrayListPirate.stickerPirate.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListPirate.stickerPirate.get(i),
                    StickerDrawableArrayListPirate.stickerPirate.get(i));
        }

        arrayListSize = StickerDrawableArrayListSkaterBoy.stickerSkaterBoy.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListSkaterBoy.stickerSkaterBoy.get(i),
                    StickerDrawableArrayListSkaterBoy.stickerSkaterBoy.get(i));
        }

        arrayListSize = StickerDrawableArrayListVampire.stickerVampire.size();
        for (i = 0; i < arrayListSize; i++) {
            stickerMap.put(StickerPatternArrayListVampire.stickerVampire.get(i),
                    StickerDrawableArrayListVampire.stickerVampire.get(i));
        }
    }
}
