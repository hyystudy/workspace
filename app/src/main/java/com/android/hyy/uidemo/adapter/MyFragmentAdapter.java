package com.android.hyy.uidemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.android.hyy.uidemo.fragment.PagerFragment;

/**
 * Created by hyy on 2016/3/18.
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"我的","发现","推荐"};
    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //这里 将position穿过去 以便于让fragment 知道我们的位置
        return PagerFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
