package com.netdil.recapp;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    TextView response, netdilTextView;
    Button buttonRecommend, rateButton,settingsButton,watchingListButton,aboutButton;
    Context ctx;
    SharedPreferences preferences ;
    public  static String EXTRA_MESSAGE = "com.netdil.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ctx = MainActivity.this;
        buttonRecommend = (Button) findViewById(R.id.recommendButton);
        rateButton = (Button) findViewById(R.id.rateButton);
        watchingListButton = (Button) findViewById(R.id.watcingListButton);
        settingsButton  = (Button) findViewById(R.id.settingsButton);
        aboutButton = (Button) findViewById(R.id.aboutButton);
        response = (TextView) findViewById(R.id.responseTextView);
        netdilTextView = (TextView) findViewById(R.id.netdilText);



        checkForFileExistance();


        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_actionbar);
        }else{
            Toast.makeText(ctx, "action bar is null", Toast.LENGTH_SHORT).show();
        }

        buttonRecommend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startRecommendationHomePageActivity();
            }
        });

        rateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startRateHomePageActivity();
            }
        });

        watchingListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> watchingList = FileFunctions.readFromInternalStorage(ctx,"watchingList.txt");
                if(watchingList.size()>0){
                    Intent intent = new Intent(ctx, WatchingList.class);
                    intent.putStringArrayListExtra("com.netdil.startWatchingListActivity",watchingList);
                    ctx.startActivity(intent);
                }else{
                    Toast.makeText(ctx, "Your watchlist is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });


        settingsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startSettingsActivity();
            }
        });

        aboutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog();
            }
        });


        netdilTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://sites.google.com/site/netdilab"));
                startActivity(intent);
            }
        });

        if(BuildConfig.DEBUG){
            System.out.println("DEBUGABLE IS TRUE, all system outs should be showing");
        }else{
            System.out.println("DEBUGABLE IS FALSE, all system outs are hidden");
        }
        
    }





     private void popDialog(){

         String st = "<p> <br>Personalized Movie Recommendation App developed at the Technological Educational Institute of Crete, showcasing " +
                 "<a href='https://sites.google.com/site/netdilab/research/scor'>SCoR</a>, a novel recommendation algorithm" +
                 "</p>" +
                 "<p>   Credits: <br> Background image : <a href='http://www.freepik.com/free-vector/movie-night_898377.htm'>Designed by Freepik</a> " +
                 "<br> Popcorn icon made by <a href='http://www.freepik.com'>Freepik</a> from <a href='http://www.flaticon.com'>www.flaticon.com</a> is licensed by <a href='http://creativecommons.org/licenses/by/3.0/' >CC 3.0 BY</a>" +
                 "</p>";

         Spanned s ;
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             s =  Html.fromHtml(st, Html.FROM_HTML_MODE_LEGACY);
         } else {
             s = Html.fromHtml(st);
         }

         TextView netdil = new TextView(ctx);
         netdil.setTextSize(15);
         netdil.setTextColor(Color.parseColor("#000000"));
         netdil.setPadding(25,5,25,0);
         
         netdil.setText(s);
         netdil.setMovementMethod(LinkMovementMethod.getInstance());

         AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
         alertDialog.setTitle("About");
         
         alertDialog.setView(netdil);
         alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close",
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                     }
                 });
         alertDialog.show();
     }


    private void checkForFileExistance(){

        System.out.println("---------------------------------------------------");
        if(fileExistance("rottenUserRatings.txt")) {

        }else{
            FileFunctions.writeToInternalStorage(ctx, new ArrayList<String>(), "rottenUserRatings.txt", false);
        }

        if(fileExistance("user_ratings_netdil.txt")) {
        }else{
            FileFunctions.writeToInternalStorage(ctx, new ArrayList<String>(), "user_ratings_netdil.txt", false);
        }

        if(fileExistance("rottenIdsAndNames.txt")) {

        }else{
            FileFunctions.writeToInternalStorage(ctx, new ArrayList<String>(), "rottenIdsAndNames.txt", false);
        }


        if(fileExistance("searchedMovieNames.txt")) {
        }else{
            FileFunctions.writeToInternalStorage(ctx, new ArrayList<String>(), "searchedMovieNames.txt", false);
        }


        if(fileExistance("watchingList.txt")) {
        }else{
            FileFunctions.writeToInternalStorage(ctx, new ArrayList<String>(), "watchingList.txt", false);
        }

    }


    private void startRecommendationHomePageActivity(){
        Intent i = new Intent(this, RecommendationHomePageActivity.class);
        ctx.startActivity(i);
    }

    private void startSettingsActivity(){
        Intent i = new Intent(this, MyPreferencesActivity.class);
        ctx.startActivity(i);
    }
    public void startRateHomePageActivity() {
        Context context = MainActivity.this;
        Intent intent = new Intent(context, RateHomePageActivity.class);
        
        context.startActivity(intent);
    }




    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
}

