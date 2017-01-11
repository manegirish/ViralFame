package com.technoindians.opportunities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
 * Created by Girish M on 27/6/16.
 */
public class OpportunitiesMainFragment extends Fragment {

    private TabLayout tabs;
    FloatingActionButton floatingActionButton;

    SentListFragment sentListFragment;
    ReceivedListFragment receivedListFragment;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.opportunities_layout, null);

        activity = getActivity();

        Preferences.initialize(activity.getApplicationContext());
        MainActivity_new.mainActivity.setTitle(activity.getApplicationContext()
                .getResources().getString(R.string.opportunities));

        sentListFragment = new SentListFragment();
        receivedListFragment = new ReceivedListFragment();

        tabs = (TabLayout) view.findViewById(R.id.opportunities_tabs);

        tabs.addTab(tabs.newTab().setText("Received"));
        tabs.addTab(tabs.newTab().setText("Own"));

        tabs.setOnTabSelectedListener(tabSelectedListener);

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.create_job_float);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle nextAnimation = ActivityOptions.makeCustomAnimation
                        (activity.getApplicationContext(), R.anim.animation_bottom_to_up, R.anim.animation_top_to_bottom).toBundle();
                Intent postIntent = new Intent(activity.getApplicationContext(), OpportunityPostActivity.class);
                postIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(postIntent, nextAnimation);
            }
        });
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

    private void setTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(receivedListFragment, Fragment_.OPPORTUNITIES_RECEIVED);
                break;
            case 1:
                replaceFragment(sentListFragment, Fragment_.OPPORTUNITIES_SENT);
                break;
            default:
                replaceFragment(receivedListFragment, Fragment_.OPPORTUNITIES_RECEIVED);
                break;
        }
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment oldFragment = fm.findFragmentByTag(Fragment_.OPPORTUNITIES_RECEIVED);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fm.findFragmentByTag(Fragment_.OPPORTUNITIES_SENT);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }

        ft.replace(R.id.opportunities_container, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
