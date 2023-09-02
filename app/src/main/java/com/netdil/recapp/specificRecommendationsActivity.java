package com.netdil.recapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.provider.Telephony;
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


public class specificRecommendationsActivity extends AppCompatActivity {
    ArrayList<String> arl = new ArrayList<String>();
    ArrayList<String> ratedMoviesFromFile = new ArrayList<String>();
    HashMap<String,Float> userRatesInApp = new HashMap<String,Float>();
    ArrayList<ImageDownloader> backGroundDownloads = new ArrayList<ImageDownloader>();
    Context ctx = specificRecommendationsActivity.this;
    private ImageDownloader imgDownloader;
    private static Bitmap imageClicked = null;
    String text;
    private static View singleMovieClicked ;
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


        arl = intent.getStringArrayListExtra("com.netdil.specificRecommendations");
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
            final TextView response = new TextView(ctx);

            for (String s : arl) {
                if(movieInRatingsFile(s)){
                    if(debugging) {
                        System.out.println("i tainia " + s.split("-@-")[1] + " iparxi sto arxeio opote dn emfanizete");
                    }
                    continue;
                }

                LayoutInflater inflater = (LayoutInflater)   ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View singleMovie = inflater.inflate(R.layout.movie_rated_layout, null);
                singleMovie.setTag(s);
                singleMovie.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {                       
                        ArrayList<String> ar = new ArrayList<String>();
                        ar.add(singleMovie.getTag().toString());                       
                        imageClicked = ((BitmapDrawable) ((ImageView) ((ViewGroup) singleMovie).getChildAt(0)).getDrawable()).getBitmap();
                        startshowSingleRecomendationsActivity(ar);
                        singleMovieClicked = singleMovie;
                    }

                });


                String[] lineParts = s.split("-@-");

                //movieUrl + "-@-" + movieName + "-@-" + imageUrl;
                //movieName + "-@-" + recom + "-@-" + movieUrl + "-@-" + imageUrl + "-@-" + ageRating + "-@-" + gerne + "-@-" + director + "-@-" + duration + "-@-" + yearInTheaters
                final ImageView img = (ImageView) singleMovie.findViewById(R.id.singleMoviePoster);

                if(!MyPreferencesActivity.checkForDataSave()) {

                    img.setImageResource(R.drawable.noimage);
                    imgDownloader = new ImageDownloader(img);
                    imgDownloader.execute(lineParts[3]);
                    backGroundDownloads.add(imgDownloader);
                }else{
                    if(debugging) {
                        System.out.println("Saving data, image is not downloading");
                    }
                    img.setImageResource(R.drawable.noimage);
                }

                ImageView deleteImage = (ImageView) singleMovie.findViewById(R.id.deleteImage);
                deleteImage.setVisibility(View.GONE);

                TextView tv =   (TextView) singleMovie.findViewById(R.id.singleMovieTitle);
                tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                tv.setText(lineParts[0]);
                tv.setTextSize(14);


                RatingBar rb = (RatingBar) singleMovie.findViewById(R.id.singleMovieRatingBar);
                rb.setId(counter++);
                rb.setTag(s);
                rb.setNumStars(5);
                rb.setMax(5);
                rb.setStepSize((float) 0.5);
                rb.setIsIndicator(true);
                rb.setRating(roundToHalf(Float.valueOf(lineParts[1])));
                publicFunctions.setStarColor(rb);



                rb.setOnRatingBarChangeListener(
                        new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                              
                            }
                        });
                la.addView(singleMovie);

            }



        }else{
            if(debugging) {
                System.out.println("arl is null :/");
            }
        }
    }



    private boolean movieInRatingsFile(String movieLine){

        for(String s:ratedMoviesFromFile){            
            if(movieLine.split("-@-")[2].equalsIgnoreCase(s.split("-@-")[0])){
                return true;
            }
        }
        return false;
    }

    public static float roundToHalf(float d) {
        return (float) ((float) Math.round(d * 2) / 2.0);
    }

    public static Bitmap getImageClicked() {
        return imageClicked;
    }

    public void startshowSingleRecomendationsActivity(ArrayList<String> recom) {


        Intent intent = new Intent(ctx, showSingleFragmentActivity.class);
        intent.putExtra("com.netdil.ShowRecommendations", recom);
        ctx.startActivity(intent);
    }
    public static View getSingleMovieClicked(){
        return singleMovieClicked;
    }
}
