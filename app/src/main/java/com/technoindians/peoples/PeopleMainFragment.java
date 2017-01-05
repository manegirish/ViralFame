package com.technoindians.peoples;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dataappsinfo.viralfame.MainActivity_new;
import com.dataappsinfo.viralfame.R;
import com.technoindians.constants.Fragment_;
import com.technoindians.preferences.Preferences;

/**
 * Created by girish on 12/8/16.
 */
public class PeopleMainFragment extends Fragment {

    private TabLayout tabs;
    FloatingActionButton floatingActionButton;

    private Activity activity;
    private FollowersFragment followersFragment;
    private FollowingFragment followingFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.opportunities_layout,null);

        activity = getActivity();

        Preferences.initialize(activity.getApplicationContext());
        MainActivity_new.mainActivity.setTitle(activity.getApplicationContext()
                .getResources().getString(R.string.my_people));

        followersFragment = new FollowersFragment();
        followingFragment = new FollowingFragment();

        tabs = (TabLayout)view.findViewById(R.id.opportunities_tabs);

        tabs.addTab(tabs.newTab().setText("Followers"));
        tabs.addTab(tabs.newTab().setText("Following"));

        tabs.setOnTabSelectedListener(tabSelectedListener);

        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.create_job_float);
        floatingActionButton.setVisibility(View.GONE);

        setTabFragment(0);
        return view;
    }

    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            setTabFragment(position);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

    private void setTabFragment(int tabPosition)
    {
        switch (tabPosition)
        {
            case 0 :
                MainActivity_new.mainActivity.closeSearch();
                floatingActionButton.setVisibility(View.GONE);
                replaceFragment(followersFragment);
                break;
            case 1 :
                MainActivity_new.mainActivity.closeSearch();
                floatingActionButton.setVisibility(View.GONE);
                replaceFragment(followingFragment);
                break;
            default:
                MainActivity_new.mainActivity.closeSearch();
                floatingActionButton.setVisibility(View.GONE);
                replaceFragment(followersFragment);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment oldFragment = fm.findFragmentByTag(Fragment_.FOLLOWERS);
        if (oldFragment!=null){
            ft.remove(oldFragment);
        }
        ft.replace(R.id.opportunities_container, fragment, Fragment_.FOLLOWERS);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment oldFragment = fm.findFragmentByTag(Fragment_.FOLLOWERS);
        if (oldFragment!=null){
            ft.remove(oldFragment);
        }
    }
}
