package com.netdil.recapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.netdil.recapp.CrawlUserAsyncTask.EXTRA_MESSAGE;

public class showRecomendationsActivity extends FragmentActivity {
    private static ArrayList<String> rec = new ArrayList<String>();
    private static HashMap<String,Bitmap> movieImages = new HashMap<String,Bitmap>();
    private static ArrayList<FragmentList> fragmentList = new ArrayList<FragmentList>();
    static CustomPagerAdapter mCustomPagerAdapter;
    static ViewPager mViewPager;
    static  int lastShowedPosition = 0;
    private static String[] filterArray = { "", "", "" ,"","",""};
    private static ArrayList<String> myRatings = new ArrayList<String>();
    private static boolean debugging= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recomendations);


        Intent intent = getIntent();
        debugging = BuildConfig.DEBUG;
        rec = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_MESSAGE);
      

        mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mCustomPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }

        });


        
        myRatings = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(), "user_ratings_netdil.txt");
        if(debugging) {
            for (String s : myRatings) System.out.println(s.split("-@-")[0]);
        }
        //  }
        if(debugging) {
            System.out.println("showRecommendationActivity called at page: " + mViewPager.getCurrentItem());
        }
      
    }



    public static ArrayList<String> getMyRatings(){
        return myRatings;
    }

    public static void setMyRatings(){
        myRatings = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(), "user_ratings_netdil.txt");
        if(debugging) {
            System.out.println("setMyRatings called and returned: " + myRatings.size());
        }
    }

    public static void clearFilterArray(){
        for(int i=0;i<filterArray.length;i++){
            filterArray[i] = "";
        }
    }
    public static String[] getFilterArray() {
        return filterArray;
    }
    public static void setFilterArray(String[] filterAr) {
        filterArray = filterAr;
    }
    public static ArrayList<String> getAllRec(){
        return rec;
    }
    public static void notifyMe(){
        mCustomPagerAdapter.notifyDataSetChanged();
    }
    public static void addToFragmentList(FragmentList fragmentListItem){
        fragmentList.add(fragmentListItem);
    }
    public static ArrayList<FragmentList> getFragmentList() {//epistrefei olo to list
        return  fragmentList;
    }
    public static void setLastShowedPosition(int pos){
        lastShowedPosition = pos;
    }
    public static String getRec(int i){return rec.get(i); }
    public static int maxPageNumber(){
        return rec.size();
    }
    public static void addMovieImage(String movieUrl, Bitmap img){
        movieImages.put(movieUrl,img);
        if(debugging) {
            System.out.println("new image added in hashmap for movie: " + movieUrl);
        }
    }
    public static Bitmap getMovieImage(String movieUrl){
        return movieImages.get(movieUrl);
    }
    public static boolean alreadyDownloadedImage(String movieUrl){
        return movieImages.containsKey(movieUrl);
    }
    public static int getMovieImagesSize(){
        return movieImages.size();
    }
    public static void clearImagesHashMap(){
        if(debugging) {
            System.out.println("CLEARING MOVIEIMAGES");
        }
        movieImages.clear();
    
    }

}
