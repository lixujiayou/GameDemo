package com.example.administrator.gamedemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */
public class BaseFragmentAdapterTableLayout extends FragmentPagerAdapter {

    public List<Fragment> list_fragmet;
    private List<String> list_Title;

    public BaseFragmentAdapterTableLayout(List<String> list_Title, List<Fragment> list_fragmet, FragmentManager fm) {
        super(fm);
        this.list_fragmet = list_fragmet;
        this.list_Title = list_Title;
    }


    @Override
    public Fragment getItem(int position) {
        return list_fragmet.get(position);
    }

    @Override
    public int getCount() {
        return list_fragmet.size();
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {

        return list_Title.get(position % list_Title.size());
    }
}
