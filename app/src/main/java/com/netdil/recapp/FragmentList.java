package com.netdil.recapp;

import android.support.v4.app.Fragment;

/**
 * Created by admin on 18/11/2016.
 */

public class FragmentList {

    private  String mName;
    private  String recommendation;
    private  String ageRating;
    private  String gerne;
    private  String director;
    private  String duration;
    private  String yearInTheaters;
    private Fragment fragment;


    FragmentList(Fragment fragment, String... movieInfo){
        mName = movieInfo[0];
        recommendation = movieInfo[1];
         ageRating = movieInfo[4];
         gerne = movieInfo[5];
         director = movieInfo[6];
         duration = movieInfo[7];
         yearInTheaters = movieInfo[8];
        this.fragment=fragment;
    }

    public String toString(){
        return mName +" "+ recommendation +" " + ageRating +" " + gerne +" " + director +" " + duration +" " + yearInTheaters;
    }
    public String getmName() {
        return mName;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public String getGerne() {
        return gerne;
    }

    public String getDirector() {
        return director;
    }

    public String getDuration() {
        return duration;
    }

    public String getYearInTheaters() {
        return yearInTheaters;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
