package com.zzh.lazyloadviewpager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * created by zzh 20180802 for anchor image text publish, anchor interaction with audience, game record fragment
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // view
    private TabLayout mAnchorTabLayout;
    private LazyViewPager mAnchorViewPager;
    // adapter
    TabFrgAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initViewPagerAdapter(getAnchorFragmentList(), addAnchorTab());
    }

    private void initView() {
        mAnchorTabLayout = (TabLayout) findViewById(R.id.anchor_home_tablayout);
        mAnchorViewPager = (LazyViewPager) findViewById(R.id.anchor_viewpage);
        mAnchorViewPager.setOnPageChangeListener(mViewPagerChangeListener);
    }

    private ArrayList<String> addAnchorTab() {
        ArrayList<String> tabTitleArray = new ArrayList<String>();
        tabTitleArray.add(0, "Tab1");
        tabTitleArray.add(1, "Tab2");
        tabTitleArray.add(2, "Tab3");
        if (mAnchorTabLayout != null) {
            mAnchorTabLayout.addTab(mAnchorTabLayout.newTab().setText(tabTitleArray.get(0)));
            mAnchorTabLayout.addTab(mAnchorTabLayout.newTab().setText(tabTitleArray.get(1)));
            mAnchorTabLayout.addTab(mAnchorTabLayout.newTab().setText(tabTitleArray.get(2)));
        }
        return tabTitleArray;
    }

    @SuppressLint("LongLogTag")
    private ArrayList<Fragment> getAnchorFragmentList() {
        ArrayList<Fragment> fragmentsArray = new ArrayList<Fragment>();
        fragmentsArray.add(0, new FirstFragment());
        fragmentsArray.add(1, new SecondFragment());
        fragmentsArray.add(2, new ThirdFragment());
        Log.i(TAG, "test log:getAnchorFragmentList");
        return fragmentsArray;
    }

    private void initViewPagerAdapter(ArrayList<Fragment> fragments, ArrayList<String> tabTitles) {
        mViewPagerAdapter = new TabFrgAdapter(getSupportFragmentManager(), fragments, tabTitles);

        // set view pager adapter
        if (mAnchorViewPager != null) {
            mAnchorViewPager.setAdapter(mViewPagerAdapter);
            // connect tablayout and viewpager
            //mAnchorTabLayout.setupWithViewPager(mAnchorViewPager);
        }

        // make click tab view item to make view pager to scroll to relevant position
        for (int i = 0; i < mAnchorTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mAnchorTabLayout.getTabAt(i);
            if (tab != null) {
                // use reflect to get tab object, then to get Class
                Class c = tab.getClass();
                try {
                    // c.getDeclaredField get private attribute
                    // view is tab's private attribute, type is TabView, TabLayout private inner class
                    // if dependence com.android.support:design:28.0.0 use "view", if below this version, then the field name is mView
                    Field field = c.getDeclaredField("mView");
                    if (field == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    final View view = (View) field.get(tab);
                    if (view == null) {
                        continue;
                    }
                    view.setTag(i);
                    view.setOnClickListener(mTabOnClickListener);
                } catch (NoSuchFieldException e) {
                    Log.e(TAG, "NoSuchFieldException, message="+e.getMessage());
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "IllegalAccessException, message="+e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Exception, message="+e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // make tab layout scroll to correct position tab when viewpager scroll
    LazyViewPager.OnPageChangeListener mViewPagerChangeListener = new LazyViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mAnchorTabLayout.getTabAt(position).select();
        }

        @Override
        public void onPageSelected(int position) {
            mAnchorTabLayout.getTabAt(position).select();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            /* this function called when state is changed, state has 3 state:0, 1, 2
                state ==1 : page is scrolling
                state==2 : page is scroll finish
                state==0 : page do nothing */
        }
    };

    // make viewpager to relevant position when user click tablayout tab
    private View.OnClickListener mTabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = (int) view.getTag();
            TabLayout.Tab tab = mAnchorTabLayout.getTabAt(pos);
            if (tab != null) {
                tab.select();
            }
            mAnchorViewPager.setCurrentItem(pos, false);
        }
    };
}
