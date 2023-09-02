package com.netdil.recapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;


public class CustomPagerAdapter extends FragmentPagerAdapter { //prin ekane extends FragmentStatePagerAdapter

    protected Context mContext;

    private int filterMaxPage = 1;
    private boolean stopSwipe = false;
    private boolean firstFilter = true;
    private boolean debugging= true;


    public CustomPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        debugging = BuildConfig.DEBUG;
    }


    @Override
    // This method returns the fragment associated with
    // the specified position.
    //
    // It is called when the Adapter needs a fragment
    // and it does not exists.
    public Fragment getItem(int position) {

        if(debugging) {
            System.out.println("firstFilter: " + firstFilter);
        }

        // Create fragment object
        Fragment fragment = new DemoFragment();



        // Attach some data to it that we'll
        // use to populate our fragment layouts
        Bundle args = new Bundle();

        String line = showRecomendationsActivity.getRec(position);
        args.putString("movieLine",line);
        


        if(debugging) {
            System.out.println("the final line of the fragment: " + line);
        }
        FragmentList fragmentListItem = new FragmentList(fragment,line.split("-@-"));
        showRecomendationsActivity.addToFragmentList(fragmentListItem);

        // Set the arguments on the fragment
        // that will be fetched in DemoFragment@onCreateView
        fragment.setArguments(args);
        return fragment;
    }


    private int setFilterMaxPage(){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        int counter = 0;
        int notShowing = 0;
        System.out.println("counter: " + counter);
        for (String s : showRecomendationsActivity.getAllRec()) {
            if (FilterActivity.filterMovie(showRecomendationsActivity.getFilterArray(), s.split("-@-"))) {
                System.out.println("counter++ for: " + s.split("-@-")[0] + " " + s.split("-@-")[8]);
                counter++;
            }else{
                notShowing++;
            }
        }
        System.out.println("counter returning: " + counter);
        System.out.println("notShowing: " + notShowing);
        System.out.println("full counters size: " + (counter+notShowing));
        if(counter==0){counter++;}
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        return counter;

    }

    @Override
    public int getCount() {       
       return showRecomendationsActivity.maxPageNumber();
    }



}
