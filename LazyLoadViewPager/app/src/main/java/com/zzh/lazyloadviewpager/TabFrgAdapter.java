package com.zzh.lazyloadviewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * add by zzh for 20180802 MainActivity viewpager and fragment adapter
 * */
public class TabFrgAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "TabFrgAdapter";
    List<Fragment> mListfragment;
    List<String> mListTabTitle;

    public TabFrgAdapter(FragmentManager fm, List<Fragment> fragments, List<String> tabTitles) {
        super(fm);
        mListfragment = fragments;
        mListTabTitle = tabTitles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mListTabTitle.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mListfragment.get(position);
    }

    @Override
    public int getCount() {
        return mListfragment.size();
    }
}
