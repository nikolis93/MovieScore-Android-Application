package com.netdil.recapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.widget.Space;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class WatchingList extends AppCompatActivity {
    ArrayList<String> arl = new ArrayList<String>();
    ArrayList<String> ratedMoviesFromFile = new ArrayList<String>();
    HashMap<String,Float> userRatesInApp = new HashMap<String,Float>();
    ArrayList<ImageDownloader> backGroundDownloads = new ArrayList<ImageDownloader>();
    Context ctx = WatchingList.this;
    private ImageDownloader imgDownloader;
    private boolean isRatingChanged = false;
    private HashMap<RatingBar,View> singleMovieHashMap = new HashMap<>();
    private boolean debugging= true;

    @Override
    protected void onDestroy(){
        super.onDestroy();
        System.out.println("onDestroy called");
        MyRatingsActivity.stopDownloads(backGroundDownloads);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movies);

        Intent intent = getIntent();


        debugging = BuildConfig.DEBUG;
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_actionbar);
        }

        arl = intent.getStringArrayListExtra("com.netdil.startWatchingListActivity");
        if (arl != null){
            if(debugging) {
                String lastLine = "";
                System.out.println("printing contents of crawledratings:");
                for (String s : arl) {
                    System.out.println(s);
                    lastLine = s;
                }
            }

            LinearLayout la = (LinearLayout) findViewById(R.id.linearRate);
            LinearLayout rootLA = (LinearLayout) findViewById(R.id.activity_rate_movies);

            int counter = 0;

            ratedMoviesFromFile = FileFunctions.getMyRatings();




            for (String s : arl) {

                LayoutInflater inflater = (LayoutInflater)   ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View singleMovie = inflater.inflate(R.layout.movie_rated_layout, null);

                final String[] lineParts = s.split("-@-");

                //movieUrl + "-@-" + movieName + "-@-" + imageUrl;
                final ImageView img = (ImageView) singleMovie.findViewById(R.id.singleMoviePoster);

                if(debugging) {
                    System.out.println("watchinglist singleMovie tag" + s);
                }
                singleMovie.setTag(s);
                singleMovie.setOnTouchListener(new OnSwipeTouchListener(WatchingList.this) {

                    public void onSwipeTop() {
                        // Toast.makeText(MyRatingsActivity.this, "top", Toast.LENGTH_SHORT).show();
                    }
                    public void onSwipeRight() {
                        // Toast.makeText(MyRatingsActivity.this, "right", Toast.LENGTH_SHORT).show();
                        publicFunctions.deleteAMovieFromWatchingList(singleMovie,singleMovie.getTag().toString());
                    }
                    public void onSwipeLeft() {
                        //  Toast.makeText(MyRatingsActivity.this, "left", Toast.LENGTH_SHORT).show();
                        publicFunctions.deleteAMovieFromWatchingList(singleMovie,singleMovie.getTag().toString());
                    }
                    public void onSwipeBottom() {
                        //Toast.makeText(MyRatingsActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                    }

                });



                if(!MyPreferencesActivity.checkForDataSave()) {
                    img.setImageResource(R.drawable.noimage);
                    imgDownloader = new ImageDownloader(img);
                    imgDownloader.execute(lineParts[2]);
                    backGroundDownloads.add(imgDownloader);
                }else{
                    if(debugging) {
                        System.out.println("Saving data, image is not downloading");
                    }
                    img.setImageResource(R.drawable.noimage);
                }

                TextView tv =   (TextView) singleMovie.findViewById(R.id.singleMovieTitle);
                tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                tv.setText(lineParts[1]);
                tv.setTextSize(14);


                final RatingBar rb = (RatingBar) singleMovie.findViewById(R.id.singleMovieRatingBar);
                rb.setId(counter++);
                rb.setTag(s);
                rb.setNumStars(5);
                rb.setMax(5);
                rb.setStepSize((float) 0.5);

                singleMovieHashMap.put(rb,singleMovie);


                final ImageView deleteImage = (ImageView) singleMovie.findViewById(R.id.deleteImage);
                if(!isRatingChanged){
                    deleteImage.setVisibility(View.GONE);
                }
                deleteImage.setTag(s.split("-@-")[0]);
                deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isRatingChanged) {
                            //Toast.makeText(ApplContext.getAppContext(), "Deleted movie: " + ((String) ((ImageView) v).getTag()).split("-@-")[1], Toast.LENGTH_SHORT).show();
                            isRatingChanged = false;
                            publicFunctions.deleteAMovie(deleteImage,"rateMoviesActivity");
                            rb.setRating(0);
                            deleteImage.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(ApplContext.getAppContext(), "You have not rate this movie yet", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                rb.setOnRatingBarChangeListener(
                        new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                if(fromUser) {
                                    publicFunctions.saveNewRating((String) ratingBar.getTag(), rating);
                                    publicFunctions.setStarColor(ratingBar);
                                    
                                    if(debugging) {
                                        System.out.println("hashMap tag: " + ((View) (singleMovieHashMap.get(rb))).getTag().toString());
                                    }                                   
                                    isRatingChanged = true;
                                    deleteImage.setVisibility(View.VISIBLE);                                   
                                }

                            }
                        });

                la.addView(singleMovie);

            }
            if(debugging) {
                System.out.println("hashmap size: " + singleMovieHashMap.size());
            }


        }else{
            if(debugging) {
                System.out.println("arl is null :/");
            }
        }
    }

}
