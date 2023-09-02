package com.netdil.recapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import static com.netdil.recapp.CrawlUserAsyncTask.EXTRA_MESSAGE;

public class showSingleFragmentActivity extends FragmentActivity {

    singlePagerAdapter singlePagerAdapter;
    ViewPager mViewPager;
    private static ArrayList<String> ar = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recomendations);

        Intent intent = getIntent();
        ar = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_MESSAGE);

        singlePagerAdapter = new singlePagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(singlePagerAdapter);
    }

    public static String getArItem(int position){
        return ar.get(position);
    }
    public static int getMaxPage(){
        return ar.size();
    }
}
