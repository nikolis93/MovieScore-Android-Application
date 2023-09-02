package com.netdil.recapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class RecommendationHomePageActivity extends AppCompatActivity {

    TextView response ;
    Button recommendationButton, singleMovieRecButton;
    Context ctx;
    private static String recommendationType = "";
    private static boolean isRecButtonClicked = false, isSpecificRecButtonClicked = false;
    private static ProgressDialog mProgressDialog ;
    private static boolean isDialogShowing = false;
    private static ArrayList<String> filteredArrayList = new ArrayList<String>();
    private static ArrayList<String> fullRecArrayList = new ArrayList<String>();
    private  boolean debugging= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_home_page);

        Intent intent = getIntent();

        debugging = BuildConfig.DEBUG;

        ctx = RecommendationHomePageActivity.this;
        recommendationButton = (Button) findViewById(R.id.recommendButton);
        singleMovieRecButton = (Button) findViewById(R.id.singleRecommendationButton);
        response = (TextView) findViewById(R.id.responseTextViewRec);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_actionbar);
        }


        mProgressDialog = new ProgressDialog(ctx);

        if(isDialogShowing){
            if(debugging) {
                System.out.println("showing dialog again");
            }
            mProgressDialog.setMessage("Connecting ...");
            mProgressDialog.show();
        }
        recommendationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!isRecButtonClicked) {
                    isRecButtonClicked = true;
                    response.setText("");                    
                    if(debugging) {
                        System.out.println("buttonrecomend clicked!");
                    }
                    int filesize = FileFunctions.getMyRatings().size();
                    if (filesize < 20) {                        
                        Toast.makeText(ctx, "Please Rate atleast 20 movies", Toast.LENGTH_SHORT).show();
                        isRecButtonClicked = false;
                        return;
                    } else {

                        mProgressDialog.setMessage("Connecting ...");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                        isDialogShowing = true;
                        Client Client = new Client(response, RecommendationHomePageActivity.this, "recommend", "");
                        Client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);                        
                        recommendationType = "recommendation";

                    }
                }else{
                    Toast.makeText(ctx, "Another action in progress", Toast.LENGTH_SHORT).show();
                }

            }
        });

        singleMovieRecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int filesize = FileFunctions.getMyRatings().size();
                if (filesize < 20) {
                    Toast.makeText(ctx, "Please Rate atleast 20 movies", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!isSpecificRecButtonClicked) {
                        isSpecificRecButtonClicked = true;
                        startSearchActivity();
                    } else {
                        Toast.makeText(ctx, "Another action in progress", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

    }
    @Override
    protected void onRestart(){
        super.onRestart();
        response.setText("");
        if(debugging) {
            System.out.println("recHomePage onRestart called filter is false.");
        }
        String[] filter = new String[6];
        for(int i=0;i<filter.length;i++){
            filter[i]="";
        }
        FilterActivity.setPopularityPosition(0);
        FilterActivity.setAgeRatingPosition(0);
        FilterActivity.setGenrePosition(0);
        showRecomendationsActivity.setFilterArray(filter);
        isSpecificRecButtonClicked = false;
        isRecButtonClicked = false;
        mProgressDialog.dismiss();
        if(debugging) {
            System.out.println("movieImagesSize on restart: " + showRecomendationsActivity.getMovieImagesSize());
        }
        showRecomendationsActivity.clearImagesHashMap();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( mProgressDialog!=null && mProgressDialog.isShowing() ){
            mProgressDialog.cancel();
        }
    }


    public static void setFullRecArrayList(ArrayList<String> fullArl){
        fullRecArrayList = fullArl;
    }
    public static ArrayList<String> getFullRecArrayList(){
        return fullRecArrayList;
    }
    public static ArrayList<String> getFilteredArrayList(){
        return filteredArrayList;
    }
    public static void setFilteredArrayList(ArrayList<String> filteredArl){
        filteredArrayList = filteredArl;
    }
    public static void dismissDialog(){
        mProgressDialog.dismiss();
        isDialogShowing = false;
    }

    public static void setIsSpecificRecButtonClicked(boolean isSpRecButtonClicked) {
        isSpecificRecButtonClicked = isSpRecButtonClicked;
    }

    public static void setIsRecButtonClicked(boolean isRButtonClicked) {
       isRecButtonClicked = isRButtonClicked;
    }

    private void startRecommendationForAsingleMovieActivity(){
         Intent i = new Intent(this, RecommendationForASingleMovieActivity.class);
         ctx.startActivity(i);
     }
    private  void startSearchActivity(){
        Intent intent = new Intent(ctx, SearchForAMovieActivity.class);
        intent.putExtra("com.netdil.SearchForAMovieActivity","searchForRecommendation");
        ctx.startActivity(intent);
        recommendationType = "specificRecommendation";
    }

    public static String getRecommendationType() {
        return recommendationType;
    }

    public static void setRecommendationType(String recType) {
       recommendationType = recType;
    }
}
