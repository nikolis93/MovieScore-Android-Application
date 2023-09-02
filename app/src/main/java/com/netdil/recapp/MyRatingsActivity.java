package com.netdil.recapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MyRatingsActivity extends AppCompatActivity {

    Context ctx = MyRatingsActivity.this;
    ArrayList<String> arl = new ArrayList<String>();
    ArrayList<String>  rottenAr = new ArrayList<String>();
    ArrayList<String>  localAr = new ArrayList<String>();
    HashMap<String,Float> changedRatings = new HashMap<String,Float>();
    ArrayList<ImageDownloader> backGroundDownloads = new ArrayList<ImageDownloader>();
    private ImageDownloader imgDownloader;
    private static boolean debugging= true;


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(debugging) {
            System.out.println("onDestroy called");
        }
        stopDownloads(backGroundDownloads);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ratings);

        Intent intent = getIntent();

        debugging = BuildConfig.DEBUG;

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_actionbar);
        }

        arl = FileFunctions.getMyRatings();
        if (arl != null) {

            LinearLayout la = (LinearLayout) findViewById(R.id.linearMyRatings);
            LinearLayout rootLA = (LinearLayout) findViewById(R.id.activity_show_recomendations);


            int counter = 0;
            for (String s : arl) {
                final String[] lineParts = s.split("-@-");

                LayoutInflater inflater = (LayoutInflater)   ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View singleMovie = inflater.inflate(R.layout.movie_rated_layout, null);

                singleMovie.setTag(lineParts[0]);
                if(debugging) {
                    System.out.println("singeMovie tag: " + singleMovie.getTag());
                }

                final ImageView img = (ImageView) singleMovie.findViewById(R.id.singleMoviePoster);

                singleMovie.setOnTouchListener(new OnSwipeTouchListener(MyRatingsActivity.this) {

                    public void onSwipeTop() {                       

                    }
                    public void onSwipeRight() {                       
                        publicFunctions.deleteAMovie(singleMovie,"myRatingsActivity");                        
                    }
                    public void onSwipeLeft() {                      
                        publicFunctions.deleteAMovie(singleMovie,"myRatingsActivity");                     
                    }
                    public void onSwipeBottom() {
                        
                    }

                });


                if(!MyPreferencesActivity.checkForDataSave()) {
                    img.setImageResource(R.drawable.noimage);
                    imgDownloader = new ImageDownloader(img);
                    imgDownloader.execute(lineParts[3]);
                    backGroundDownloads.add(imgDownloader);
                }else{
                    if(debugging) {
                        System.out.println("Saving Data, image is not downloading");
                    }
                    img.setImageResource(R.drawable.noimage);
                }

                //end touch event

                ImageView deleteImage = (ImageView) singleMovie.findViewById(R.id.deleteImage);
                deleteImage.setVisibility(View.GONE);

                TextView tv =   (TextView) singleMovie.findViewById(R.id.singleMovieTitle);
                tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                tv.setText(lineParts[2]);
                tv.setTextSize(14);


                RatingBar rb = (RatingBar) singleMovie.findViewById(R.id.singleMovieRatingBar);

                rb.setTag(s);

                rb.setNumStars(5);
                rb.setMax(5);
                rb.setStepSize((float) 0.5);
                rb.setRating(Float.valueOf(lineParts[1]));
                LayerDrawable stars = (LayerDrawable) rb.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

                rb.setOnRatingBarChangeListener(
                        new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                if (fromUser) {
                                    String tag = (String) ratingBar.getTag();
                                    String[] tagParts = tag.split("-@-");
                                    LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                                    stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                                    stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                                   // Toast.makeText(ctx, tagParts[2] + " Rating: " + rating, Toast.LENGTH_SHORT).show();
                                    if(debugging) {
                                        System.out.println("line: " + tagParts[2]);
                                    }
                                    changeMovieRating(tag, rating, ratingBar);
                                }
                            }
                        });

                la.addView(singleMovie);
            }

            Button btn = new Button(ctx);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            btn.setLayoutParams(layoutParams);
            btn.setText("Save changes");



            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if(debugging) {
                        System.out.println("saving changes");
                    }
                    ArrayList<String> changes = RateMovies.fromHashMapToArrayList(changedRatings);
                    saveChanges(changes);                  
                }
            });           
        }
    }



    private String replaceLine(String line, float newRating){
        //movieUrl , rating , movieName , imageUrl
        String [] lineParts = line.split("-@-");
        String newLine = lineParts[0] + "-@-" + newRating + "-@-" + lineParts[2] + "-@-" + lineParts[3] ;
        if(debugging) {
            System.out.println("new line: " + newLine);
        }

        return newLine;
    }

    private boolean changeMovieRating(String tag, float rating , RatingBar rb){
        ArrayList<String> fileContents  = FileFunctions.readFromInternalStorage(ctx,"user_ratings_netdil.txt");
        boolean found = false;
        String fullLine = "";
        String movieUrl = tag.split("-@-")[0];
        for(int i=0;i<fileContents.size();i++){
            fullLine = fileContents.get(i);
            if(fullLine.split("-@-")[0].equalsIgnoreCase(movieUrl)){
                fileContents.remove(i);
                fileContents.add(i,replaceLine(fullLine , rating));
                found = true;
                Toast.makeText(ctx, tag.split("-@-")[2] + " Rating: " + rating, Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if(!found){ //an dn iparxei sto arraylist simenei oti einai rottenrating ara to vazw sto telos or dn to vazw katholou kai epistrefw false
            ArrayList<String> rottenRatings  = FileFunctions.readFromInternalStorage(ctx,"rottenUserRatings.txt");
            if(debugging) {
                System.out.println("its rotten rating! ");
            }
            for(int j=0;j<rottenRatings.size();j++){
                fullLine = rottenRatings.get(j);
                if(fullLine.split("-@-")[0].equalsIgnoreCase(movieUrl)){
                    Toast.makeText(ctx,  tag.split("-@-")[2] + " is a rotten rating and cannot be changed", Toast.LENGTH_SHORT).show();
                    rb.setRating(Float.valueOf(fullLine.split("-@-")[1]));
                    break;
                }
            }

            return found;
        }
        FileFunctions.writeToInternalStorage(ctx,fileContents,"user_ratings_netdil.txt",false);

        return found;
    }

    private void saveChanges(ArrayList<String> changedRatings){
        String changedLine = ""; //to url apo to line pou egine i alagi
        String finalLine = "";//to full line pou tha sothei sto arxeio
        String temp = "";
        String [] parts;
        int countChages = 0;
        for(String st : changedRatings) {
            changedLine= st.split("-@-")[0];
            for (int i=0;i< arl.size();i++) {
                temp = arl.get(i);
                if (temp.contains(changedLine)){
                    parts = temp.split("-@-");
                    finalLine = parts[0] + "-@-" + st.split("-@-")[1] + "-@-" +parts[2] + "-@-" + parts[3];
                    if(debugging) {
                        System.out.println("finalLine: " + finalLine);
                    }
                    arl.remove(i);
                    countChages++;
                    arl.add(i,finalLine);
                }

            }
        }
        if(debugging) {
            System.out.println("eginan: " + countChages + "  alages sto arxeio");
        }
        FileFunctions.writeToInternalStorage(ctx, arl, "user_ratings_netdil.txt", false);
    }




    public static void stopDownloads(ArrayList<ImageDownloader> backGroundDownloads){
        for(ImageDownloader i : backGroundDownloads){
            if(!i.isCancelled()) {
                if(debugging) {
                    System.out.println("stoping: " + i.toString());
                }
                i.cancel(true);
                if(i.isCancelled()){
                    if(debugging) {
                        System.out.println("ontos psofise");
                    }
                }
            }
        }
    }


}
