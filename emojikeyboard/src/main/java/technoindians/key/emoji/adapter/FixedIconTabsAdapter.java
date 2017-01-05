package technoindians.key.emoji.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.technoindians.emojikeyboard.R;

import technoindians.viewpager.TabsAdapter;
import technoindians.viewpager.ViewPagerTabButton;

public class FixedIconTabsAdapter implements TabsAdapter {

	private Activity mContext;

	private int[] mIcons = { R.drawable.clock_tab, R.drawable.smiley_tab, R.drawable.flower_tab, R.drawable.bell_tab,
			R.drawable.car_tab, R.drawable.filter_tab };

	public FixedIconTabsAdapter(Activity ctx) {
		this.mContext = ctx;
	}

	@Override
	public View getView(int position) {
		ViewPagerTabButton tab;
		LayoutInflater inflater = mContext.getLayoutInflater();
		tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_fixed, null);
		if (position < mIcons.length)
			tab.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(mIcons[position]),
					null, null);
		return tab;
	}

}
