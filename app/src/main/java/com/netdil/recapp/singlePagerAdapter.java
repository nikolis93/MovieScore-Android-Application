package com.netdil.recapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class singlePagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public singlePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new specificFragment();

        Bundle args = new Bundle();
        args.putString("movieLine",showSingleFragmentActivity.getArItem(position));

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return showSingleFragmentActivity.getMaxPage();
    }
}
