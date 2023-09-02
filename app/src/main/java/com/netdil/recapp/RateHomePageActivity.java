package com.netdil.recapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateHomePageActivity extends AppCompatActivity {

    TextView response ;
    TextView  moviesCount;
    Button buttonImportFromRotten, rateMoviesButton, myRatingsButton, searchButton;
    Context ctx;
    private static ProgressDialog mProgressDialog;
    private static boolean isRateMoviesButtonClicked = false;
    private static boolean isDialogShowing = false;
    private boolean debugging= true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_home_page);
        Intent intent = getIntent();

        ctx = RateHomePageActivity.this;
        buttonImportFromRotten = (Button) findViewById(R.id.importFromRotten);
        rateMoviesButton = (Button) findViewById(R.id.rateMoviesButton);
        myRatingsButton = (Button) findViewById(R.id.myRatingsButton);
        searchButton = (Button) findViewById(R.id.searchButton2);
        response = (TextView) findViewById(R.id.responseTextView);
        moviesCount = (TextView) findViewById(R.id.movieCount);

        debugging = BuildConfig.DEBUG;

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


        moviesCount.setText("Current Ratings: " +  FileFunctions.getMyRatings().size() );
        buttonImportFromRotten.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startImportFromROttenActivity();
            }
        });

        rateMoviesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!isRateMoviesButtonClicked) {
                    mProgressDialog.setMessage("Connecting ...");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                    isDialogShowing = true;
                    isRateMoviesButtonClicked = true;

                    Client Client = new Client(response, ctx, "getMoviesToRate", "");
                    Client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);                   

                }else{
                    Toast.makeText(ctx, "Another action in progress", Toast.LENGTH_SHORT).show();
                }
               
            }
        });


        myRatingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startMyRatingsActivity();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startSearchActivity();
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
        if(debugging) {
            System.out.println("ONRESTART:");
        }
        response.setText("");
        moviesCount.setText("Current Ratings onRestart: " + FileFunctions.getMyRatings().size() );
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( mProgressDialog!=null && mProgressDialog.isShowing() ){
            mProgressDialog.cancel();
        }
    }
    public static void dismissDialog(){
        mProgressDialog.dismiss();
        isDialogShowing = false;

    }
    public static void  setRateMoviesButtonClicked(boolean rateMoviesButtonClicked) {
        isRateMoviesButtonClicked = rateMoviesButtonClicked;
    }

    private void startSearchActivity(){
        Intent intent = new Intent(ctx, SearchForAMovieActivity.class);
        intent.putExtra("com.netdil.SearchForAMovieActivity","search");
        ctx.startActivity(intent);
    }
    public void startRateMoviesActivity() {
        Intent intent = new Intent(ctx, RateMovies.class);
        intent.putExtra("com.netdil.StartRateEvent",FileFunctions.getFileSize(ctx,"user_ratings_netdil.txt")+"");
        ctx.startActivity(intent);
    }
    private void startImportFromROttenActivity(){
        Intent intent = new Intent(ctx, ImportFromRottenActivity.class);
        ctx.startActivity(intent);
    }

    private void startMyRatingsActivity(){

        if(FileFunctions.getMyRatings().size() > 0){
            Intent intent = new Intent(ctx, MyRatingsActivity.class);
            ctx.startActivity(intent);
        }else{
            Toast.makeText(ctx, "You haven't rate any movies yet", Toast.LENGTH_SHORT).show();
        }

    }
    public  String getRecommendationResource(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String recSource = sharedPrefs.getString("recSource","1");
        return recSource;
    }
    private int getFileSize(){
        int filesize = 0;
        if(getRecommendationResource().equalsIgnoreCase("1")){
            System.out.println("reading both files");
            filesize += FileFunctions.getFileSize(ctx, "rottenUserRatings.txt");
            filesize += FileFunctions.getFileSize(ctx, "user_ratings_netdil.txt");

        }
        if(getRecommendationResource().equalsIgnoreCase("2")){
            System.out.println("reading local file");
            filesize = FileFunctions.getFileSize(ctx, "user_ratings_netdil.txt");
        }
        if(getRecommendationResource().equalsIgnoreCase("3")){
            System.out.println("reading rotten file");
            filesize = FileFunctions.getFileSize(ctx, "rottenUserRatings.txt");
        }
        return filesize;
    }
}
