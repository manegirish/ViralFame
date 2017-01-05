package technoindians.key.sticker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.technoindians.emojikeyboard.R;

import technoindians.key.emoji.SmileyKeyBoard;
import technoindians.key.sticker.adapter.SlidingStripAdapter;
import technoindians.key.sticker.adapter.StickerGridviewImageAdapter;
import technoindians.key.sticker.mapping.RecentlyUsedStickers;
import technoindians.key.sticker.mapping.StickerDrawableArrayListBear;
import technoindians.key.sticker.mapping.StickerDrawableArrayListGeek;
import technoindians.key.sticker.mapping.StickerDrawableArrayListGery;
import technoindians.key.sticker.mapping.StickerDrawableArrayListHappyGhost;
import technoindians.key.sticker.mapping.StickerDrawableArrayListLittleDyno;
import technoindians.key.sticker.mapping.StickerDrawableArrayListMomon;
import technoindians.key.sticker.mapping.StickerDrawableArrayListMonster;
import technoindians.key.sticker.mapping.StickerDrawableArrayListPenguin;
import technoindians.key.sticker.mapping.StickerDrawableArrayListPirate;
import technoindians.key.sticker.mapping.StickerDrawableArrayListSkaterBoy;
import technoindians.key.sticker.mapping.StickerDrawableArrayListVampire;
import technoindians.key.sticker.mapping.StickerHashmapPatternToDrawable;
import technoindians.key.sticker.mapping.StickerPatternArrayListBear;
import technoindians.key.sticker.mapping.StickerPatternArrayListGeek;
import technoindians.key.sticker.mapping.StickerPatternArrayListGery;
import technoindians.key.sticker.mapping.StickerPatternArrayListHappyGhost;
import technoindians.key.sticker.mapping.StickerPatternArrayListLittleDyno;
import technoindians.key.sticker.mapping.StickerPatternArrayListMomon;
import technoindians.key.sticker.mapping.StickerPatternArrayListMonster;
import technoindians.key.sticker.mapping.StickerPatternArrayListPenguin;
import technoindians.key.sticker.mapping.StickerPatternArrayListPirate;
import technoindians.key.sticker.mapping.StickerPatternArrayListSkaterBoy;
import technoindians.key.sticker.mapping.StickerPatternArrayListVampire;
import technoindians.viewpager.PagerSlidingTabStrip;

/*import com.sagesurfer.emoticonkeyboard.R;
import com.sagesurfer.sage.emoji.SmileyKeyBoard;
import com.sagesurfer.sage.sticker.adapter.SlidingStripAdapter;
import com.sagesurfer.sage.sticker.adapter.StickerGridviewImageAdapter;
import com.sagesurfer.sage.sticker.mapping.RecentlyUsedStickers;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListBear;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListGeek;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListGery;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListHappyGhost;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListLittleDyno;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListMomon;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListMonster;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListPenguin;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListPirate;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListSkaterBoy;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListVampire;
import com.sagesurfer.sage.sticker.mapping.StickerHashmapPatternToDrawable;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListBear;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListGeek;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListGery;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListHappyGhost;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListLittleDyno;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListMomon;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListMonster;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListPenguin;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListPirate;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListSkaterBoy;
import com.sagesurfer.sage.sticker.mapping.StickerPatternArrayListVampire;
import com.sagesurfer.viewpager.PagerSlidingTabStrip;*/
/*import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.inscripts.emoji.SmileyKeyBoard;
import com.inscripts.emojikeyboard.R;
import com.inscripts.sticker.adapter.SlidingStripAdapter;
import com.inscripts.sticker.adapter.StickerGridviewImageAdapter;
import com.inscripts.sticker.mapping.RecentlyUsedStickers;
import com.inscripts.sticker.mapping.StickerDrawableArrayListBear;
import com.inscripts.sticker.mapping.StickerDrawableArrayListGeek;
import com.inscripts.sticker.mapping.StickerDrawableArrayListGery;
import com.inscripts.sticker.mapping.StickerDrawableArrayListHappyGhost;
import com.inscripts.sticker.mapping.StickerDrawableArrayListLittleDyno;
import com.inscripts.sticker.mapping.StickerDrawableArrayListMomon;
import com.inscripts.sticker.mapping.StickerDrawableArrayListMonster;
import com.inscripts.sticker.mapping.StickerDrawableArrayListPenguin;
import com.inscripts.sticker.mapping.StickerDrawableArrayListPirate;
import com.inscripts.sticker.mapping.StickerDrawableArrayListSkaterBoy;
import com.inscripts.sticker.mapping.StickerDrawableArrayListVampire;
import com.inscripts.sticker.mapping.StickerHashmapPatternToDrawable;
import com.inscripts.sticker.mapping.StickerPatternArrayListBear;
import com.inscripts.sticker.mapping.StickerPatternArrayListGeek;
import com.inscripts.sticker.mapping.StickerPatternArrayListGery;
import com.inscripts.sticker.mapping.StickerPatternArrayListHappyGhost;
import com.inscripts.sticker.mapping.StickerPatternArrayListLittleDyno;
import com.inscripts.sticker.mapping.StickerPatternArrayListMomon;
import com.inscripts.sticker.mapping.StickerPatternArrayListMonster;
import com.inscripts.sticker.mapping.StickerPatternArrayListPenguin;
import com.inscripts.sticker.mapping.StickerPatternArrayListPirate;
import com.inscripts.sticker.mapping.StickerPatternArrayListSkaterBoy;
import com.inscripts.sticker.mapping.StickerPatternArrayListVampire;*/

public class StickerKeyboard {
	private View popUpView;
	private ViewPager viewPager;
	private PopupWindow popupWindow;
	private LinearLayout emoticonsCover;
	private int previousHeightDiffrence = 0, keyboardHeight, userKeyboardHeight = -1;
	private boolean isKeyBoardVisible;
	private ImageView ivEmojiClear;
	private Activity activity;
	int count = 0;
	private static ViewTreeObserver.OnGlobalLayoutListener layoutListener;
	private SlidingStripAdapter adapterEmojiPopup;
	private static int stickerSize = 0;

	public void enable(Activity activity, StickerGridviewImageAdapter.StickerClickInterface clickListener,
			Integer emojiFooterId, final EditText edtMessage) {
		this.activity = activity;
		popUpView = activity.getLayoutInflater().inflate(R.layout.activity_fixed_tabs_sticker, null);
		viewPager = (ViewPager) popUpView.findViewById(R.id.pager_popviewPopupEmoji);
		viewPager.setOffscreenPageLimit(12);
		emoticonsCover = (LinearLayout) activity.findViewById(emojiFooterId);
		ivEmojiClear = (ImageView) popUpView.findViewById(R.id.ivClearEmoji);

		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) popUpView.findViewById(R.id.tabs);
		adapterEmojiPopup = new SlidingStripAdapter(activity, Color.CYAN, Color.BLACK, clickListener);

		viewPager.setAdapter(adapterEmojiPopup);
		tabs.setViewPager(viewPager);
		viewPager.setCurrentItem(1);

		popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight, false);

		ivEmojiClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				edtMessage.dispatchKeyEvent(event);
			}
		});

	}

	/**
	 * Set height for sticker keyboard.
	 * The sticker keyboard adjusts its height to match the height of your in-build keyboard. If the height is not
	 * matching, then you can use this function to set the height.
	 * @param height
	 *            Pass the height in pixels e.g 600 without any unit.<br>
	 *            Pass -1 to reset the keyboard height and let sticker keyboard to handle the height adjustment
	 */
	public void setKeyBoardHeight(int height) {
		userKeyboardHeight = height;
	}

	/**
	 * Change the size of sticker which will be shown in your chat
	 * @param size
	 */
	public static void setStickerSize(int size) {
		stickerSize = size;
	}

	/**
	 * Show sticker message to sticker image
	 * @param context
	 * @param message
	 * @return
	 */
	public static SpannableString showSticker(Context context, String message) {
		Resources res = context.getResources();
		Integer abc = StickerHashmapPatternToDrawable.stickerMap.get(message);
		SpannableString spannable = new SpannableString(message);
		if (abc == null) {
			return spannable;
		} else {
			Drawable drawable = res.getDrawable(abc);
			if (stickerSize < 350) {
				stickerSize = 350;
			}
			drawable.setBounds(0, 0, stickerSize, stickerSize);
			spannable.setSpan(new ImageSpan(drawable), 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			return spannable;
		}
	}

	public void showKeyboard(View parentLayout) {
		calculateKeyboardHeight(parentLayout);
		if (!popupWindow.isShowing()) {
			if (userKeyboardHeight == -1) {
				popupWindow.setHeight(keyboardHeight);
			} else {
				popupWindow.setHeight(userKeyboardHeight);
			}
			if (isKeyBoardVisible) {
				checkKeyboardHeight(parentLayout, false);
				emoticonsCover.setVisibility(View.GONE);
			} else {
				checkKeyboardHeight(parentLayout, true);
				emoticonsCover.setVisibility(View.VISIBLE);
			}
			popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
		} else {
			checkKeyboardHeight(parentLayout, false);
			emoticonsCover.setVisibility(View.GONE);
			popupWindow.dismiss();
		}
	}

	public void dismissKeyboard() {

		if (popupWindow.isShowing()) {
			emoticonsCover.setVisibility(View.GONE);
			popupWindow.dismiss();
		}
	}

	public boolean isKeyboardVisibile() {
		return null != popupWindow && popupWindow.isShowing();
	}

	@SuppressLint("NewApi")
	public void checkKeyboardHeight(final View parentLayout) {
		layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (count < 10) {
					calculateKeyboardHeight(parentLayout);
					count++;
				} else {
					count = 0;
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						parentLayout.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
					} else {
						parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
					}
				}
			}
		};

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

	}

	@SuppressLint("NewApi")
	private void checkKeyboardHeight(final View parentLayout, boolean removeLayoutListener) {
		layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				calculateKeyboardHeight(parentLayout);
			}
		};
		if (removeLayoutListener) {
			parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
		} else {
			parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
		}
	}

	private void calculateKeyboardHeight(final View parentLayout) {
		Rect r = new Rect();
		parentLayout.getWindowVisibleDisplayFrame(r);

		int screenHeight = parentLayout.getRootView().getHeight();

		int heightDifference = screenHeight - (r.bottom);
		if (getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
				int bottomNavigationHeight;
				Resources resources = activity.getResources();
				int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
				if (resourceId > 0) {
					bottomNavigationHeight = resources.getDimensionPixelSize(resourceId);
				} else {
					bottomNavigationHeight = 100;
				}
				// heightDifference = heightDifference - bottomNavigationHeight;
			}
			if (previousHeightDiffrence - heightDifference > 50) {
				popupWindow.dismiss();
			}
			previousHeightDiffrence = heightDifference;
			if (heightDifference > 100) {
				isKeyBoardVisible = true;
				changeKeyboardHeight(heightDifference);
			} else {
				isKeyBoardVisible = false;

			}
		} else if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			heightDifference = screenHeight / 2 - 50;
			changeKeyboardHeight(heightDifference);
		}
	}

	private int getScreenOrientation() {
		Display getOrient = activity.getWindowManager().getDefaultDisplay();
		int orientation = Configuration.ORIENTATION_UNDEFINED;
		if (getOrient.getWidth() == getOrient.getHeight()) {
			orientation = Configuration.ORIENTATION_SQUARE;
		} else {
			if (getOrient.getWidth() < getOrient.getHeight()) {
				orientation = Configuration.ORIENTATION_PORTRAIT;
			} else {
				orientation = Configuration.ORIENTATION_LANDSCAPE;
			}
		}
		return orientation;
	}

	private void changeKeyboardHeight(int height) {
		keyboardHeight = height;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				keyboardHeight);
		emoticonsCover.setLayoutParams(params);
	}

	public void enableFooterView(EditText messageField) {

		messageField.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SmileyKeyBoard.isKeyboardVisibile()) {
					SmileyKeyBoard.dismissKeyboard();
				} else if (isKeyboardVisibile()) {
					dismissKeyboard();
				}
			}
		});
	}

	public String getClickedSticker(int gridviewItemPosition) {
		int pagePosition = viewPager.getCurrentItem();
		String unicodeToBeSend = "";
		int drawableId = 0;
		switch (pagePosition) {
		case 0: {
			drawableId = RecentlyUsedStickers.stickerDrawableArrayListRecentCategory.get(gridviewItemPosition);
			unicodeToBeSend = RecentlyUsedStickers.stickerUnicodeArrayListRecentcategory.get(gridviewItemPosition);
			break;
		}
		case 1: {
			drawableId = StickerDrawableArrayListBear.stickerBear.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListBear.stickerBear.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 2: {
			drawableId = StickerDrawableArrayListGeek.stickerGeek.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListGeek.stickerGeek.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 3: {
			drawableId = StickerDrawableArrayListGery.stickerGery.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListGery.stickerGery.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}

		case 4: {
			drawableId = StickerDrawableArrayListHappyGhost.stickerHappyGhost.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListHappyGhost.stickerHappyGhost.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 5: {
			drawableId = StickerDrawableArrayListLittleDyno.stickerLittleDyno.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListLittleDyno.stickerLittleDyno.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 6: {
			drawableId = StickerDrawableArrayListMomon.stickerMomon.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListMomon.stickerMomon.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 7: {
			drawableId = StickerDrawableArrayListMonster.stickerMonster.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListMonster.stickerMonster.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 8: {
			drawableId = StickerDrawableArrayListPenguin.stickerPenguin.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListPenguin.stickerPenguin.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 9: {
			drawableId = StickerDrawableArrayListPirate.stickerPirate.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListPirate.stickerPirate.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 10: {
			drawableId = StickerDrawableArrayListSkaterBoy.stickerSkaterBoy.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListSkaterBoy.stickerSkaterBoy.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 11: {
			drawableId = StickerDrawableArrayListVampire.stickerVampire.get(gridviewItemPosition);
			unicodeToBeSend = StickerPatternArrayListVampire.stickerVampire.get(gridviewItemPosition);
			RecentlyUsedStickers.addSticker(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		default:
			break;
		}
		return unicodeToBeSend;
	}
}
