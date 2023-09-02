package com.netdil.recapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.ACTION_EDIT;

public class CrawlUserAsyncTask extends AsyncTask<Void, Void, Void> {
    public final static String EXTRA_MESSAGE = "com.netdil.ShowRecommendations";
    String dstAddress;
    String response = "";
    TextView textResponse;
    String userId = "";
    private Context context;
    private ArrayList<String> userRatings;
    CountDownTimer timer;
    private boolean timerFinished = false;
    private static String message = "";
    StringBuffer uName;
    private boolean debugging= true;
    private AsyncTask<Void,Void,Void> me = this;
    CrawlUserAsyncTask(String userId, TextView textResponse, Context cont) {
        this.userId = userId;
        this.textResponse = textResponse;
        this.context = cont;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        debugging = BuildConfig.DEBUG;


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                timer = new CountDownTimer(300000,1000 ) {//10000 is the remaining time,   1000 is the period of tick
                    @Override
                    public void   onTick(    long  millisUntilFinished) {
                        if(debugging) {
                            System.out.println("Remaining time " + String.valueOf(millisUntilFinished));
                        }
                    }
                    @Override
                    public void onFinish() {
                        if(debugging) {
                            System.out.println("Timer finished");
                        }
                        timerFinished = true;
                        ImportFromRottenActivity.dismissDialog();
                        Toast.makeText(context, "Error, please try again", Toast.LENGTH_SHORT).show();
                        me.cancel(true);
                    }
                }.start();
            }
        });

        if(debugging) {
            System.out.println("calling crawlUser");
        }
        uName = new StringBuffer("");
        userRatings = RTCTools.crawlUser(userId,uName);        
        if(userRatings != null) {
            if(debugging) {
                System.out.println("Printing user ratings in CrawlUserAsyncTask.java");
                for (String s : userRatings) System.out.println(s);
            }
            FileFunctions.writeToInternalStorage(context, userRatings, "rottenUserRatings.txt", false);           
            setRottenUserNameToPref(uName.toString());
            response = "Imported ratings from user " + uName;

            ArrayList<String> fileContents = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),"rottenIdsAndNames.txt");
            boolean found = false;
            for(String s: fileContents){
                if(s.split("-@-")[0].equalsIgnoreCase(userId)){
                    found = true;
                }
            }
            if(!found) {
                String idAndName = userId + "-@-" + "(" + uName + ")";
                publicFunctions.addNewLineToAHistoryFile(idAndName,"rottenIdsAndNames.txt");
            }else{
                if(debugging) {
                    System.out.println("to " + userId + " " + uName + " iparxei idi sto arxeio");
                }
            }

        }else{

            response = "Please make your profile public or enter a valid profile,rate some movies or check your connection";
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        ImportFromRottenActivity.setIsButtonClicked(false);
        ImportFromRottenActivity.dismissDialog();
        if(timerFinished){
            Toast.makeText(context, "Error, please try again", Toast.LENGTH_SHORT).show();
        }else {
            if(!message.equalsIgnoreCase("")) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(response.contains("Imported ratings from user")){
                    textResponse.setText("Current rottentomatoes profile:" + uName);
                }
            }else{
                Toast.makeText(context, "Error, please try again", Toast.LENGTH_SHORT).show();
            }          
        }
       
        timer.cancel();
        super.onPostExecute(result);
    }



    public void sendMessage(ArrayList<String> recom) {
        Intent intent = new Intent(context, showRecomendationsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, recom);
        context.startActivity(intent);
    }

    public void startRateMoviesActivity() {
        Intent intent = new Intent(context, RateMovies.class);
        intent.putExtra("com.netdil.StartRateEvent","");
        context.startActivity(intent);
    }


    private void setRottenUserNameToPref(String uname){
        if(debugging) {
            System.out.println("new rotten user name : " + uname);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplContext.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", uname);
        editor.commit();
    }

    public static void setMessage(String mes) {
        message = mes;
    }
}