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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.ACTION_EDIT;

public class Client extends AsyncTask<Void, Void, Void> {
    public final static String EXTRA_MESSAGE = "com.netdil.ShowRecommendations";
    String response = "";
    String message = "";
    TextView textResponse;
    static String serverIP = "10.0.32.215";
    static int serverPort = 4444;
    static Socket socket = null;
    private Context context;
    boolean errorHappend = false;
    private String action;
    private boolean timerFinished = false;
    private AsyncTask<Void,Void,Void> me = this;
    ArrayList<String> rec = new ArrayList<String>();
    ArrayList<String> movies = new ArrayList<String>();
    ArrayList<String> userRatings = new ArrayList<String>();
    CountDownTimer timer;
    private boolean debugging= true;
    Client(TextView textResponse, Context cont, String action,String message) {
        System.out.println("Client constructor called");
        this.textResponse = textResponse;
        this.context = cont;
        this.action = action;
        this.message= message;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        System.out.println("Client do in backGround started");
        debugging = BuildConfig.DEBUG;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                 timer = new CountDownTimer(90000,1000 ) {//10000 is the remaining time,   1000 is the period of tick
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
                        if(action.equalsIgnoreCase("recommend")) startshowRecomendationsActivity(rec);
                        if(action.equalsIgnoreCase("getMoviesToRate")) startRateMoviesActivity();
                        if(action.equalsIgnoreCase("search")) startRateMoviesActivity();
                        if(action.equalsIgnoreCase("searchForRecommendation")) startspecificForRecommendationsActivity();
                        me.cancel(true);
                    }
                }.start();
            }
        });

        if(action.equalsIgnoreCase("recommend"))connectForRecommendation();
        else if(action.equalsIgnoreCase("getMoviesToRate"))connectForMovieInfos();
        else if(action.equalsIgnoreCase("search") || action.equalsIgnoreCase("searchForRecommendation"))connectForSearch();

        return null;
    }



    private void connectForSearch(){
        if(debugging) {
            System.out.println("connectForSearch function started");
        }
        ObjectOutputStream  objectOutput = null;
        try {
            socket = new Socket(serverIP, serverPort);
            System.out.println("socket destIp: " + serverIP + " port:" + serverPort);
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(action);
            objectOutput.flush();
            if(debugging) {
                System.out.println("connected to server");
            }
        } catch (java.net.ConnectException e) {
            errorHappend = true;
            if(debugging) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            errorHappend = true;
            if(debugging) {
                e.printStackTrace();
            }
        }
        if(debugging) {
            System.out.println("sending query for movie: " + message);
        }
        try {
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(message);
            objectOutput.flush();
        } catch (java.lang.RuntimeException e) {
            errorHappend = true;
            if(debugging) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            errorHappend = true;
            if(debugging) {
                e.printStackTrace();
                System.out.println("ERROR: while sending movieName");
            }
        }

        if(action.equalsIgnoreCase("searchForRecommendation")){
            userRatings = FileFunctions.getMyRatings();
            if(userRatings.size() > 0) {
                if(debugging) {
                    System.out.println("Printing user ratings in client.java");
                    for (String s : userRatings) System.out.println(s);
                    System.out.println("sending userRatings to server");
                }
                
                try {
                    objectOutput.writeObject(userRatings);
                    objectOutput.flush();
                } catch (java.lang.RuntimeException ex) {
                    errorHappend = true;
                } catch (IOException ex) {
                    errorHappend = true;
                    if(debugging) {
                        System.out.println("ERROR: while sending arraylist to the server");
                    }
                }
            }
        }
        if(debugging) {
            System.out.println("waiting for server to respond");
        }
        try {
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            Object object = objectInput.readObject();
            movies = (ArrayList<String>) object;
        } catch (IOException ex) {
            errorHappend = true;
            if(debugging) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            errorHappend = true;
            if(debugging) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex){
            errorHappend = true;
            if(debugging) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   

    }

    private void connectForMovieInfos(){//gia na pernei apla moviesname,movieurl,imageurl wste na xrisimopoihthoun sto ratemovies
        if(debugging) {
            System.out.println("movieInfos function started");
        }
        ObjectOutputStream  objectOutput = null;
        try {
            socket = new Socket(serverIP, serverPort);
            if(debugging) {
                System.out.println("socket destIp: " + serverIP + " port:" + serverPort);
            }
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject("getMoviesToRate");
            objectOutput.flush();
            if(debugging) {
                System.out.println("connected to server");
            }
        } catch (Exception e) {
            errorHappend = true;
            if(debugging) {
                e.printStackTrace();
            }
        }
        if(debugging) {
            System.out.println("waiting for server to respond");
        }
        try {
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            Object object = objectInput.readObject();
            movies = (ArrayList<String>) object;
        } catch (IOException ex) {
            errorHappend = true;
            if(debugging) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            errorHappend = true;
            if(debugging) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex){
            errorHappend = true;
            if(debugging) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
  
    }


    private void connectForRecommendation(){ //gia na pernei recommendation
        if(debugging) {
            System.out.println("recommendation function started");
        }

        ObjectOutputStream  objectOutput = null;
        userRatings = FileFunctions.getMyRatings();
        if(userRatings.size() > 0) {
            if(debugging) {
                System.out.println("Printing user ratings in client.java");
                for (String s : userRatings) System.out.println(s);
            }


            try {
                if(debugging) {
                    System.out.println("trying to open socket with server " + serverIP + " at port: " + serverPort);
                }
                socket = new Socket(serverIP, serverPort);
                if(debugging) {
                    System.out.println("socket destIp: " + serverIP + " port:" + serverPort);
                }
                objectOutput = new ObjectOutputStream(socket.getOutputStream());
                objectOutput.writeObject("recommend");
                objectOutput.flush();
                if(debugging) {
                    System.out.println("connected to server");
                }
            } catch (java.net.ConnectException e) {
                if(debugging) {
                    System.out.println("Mpike sto connectException");
                    e.printStackTrace();
                }               
            } catch (Exception e) {
                if(debugging) {
                    e.printStackTrace();
                }              
            }

            if(debugging) {
                System.out.println("sending message to server");
            }
           
            try {
                objectOutput.writeObject(userRatings);
                objectOutput.flush();
            } catch (IOException ex) {
                if(debugging) {
                    System.out.println("ERROR: while sending arraylist to the server");
                }
            }  catch (java.lang.RuntimeException ex) {
                if(debugging) {
                    System.out.println("ERROR: while sending arraylist to the server");
                }
            }

            if(debugging) {
                System.out.println("waiting for server to respond");
            }
            try {
                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
                Object object = objectInput.readObject();
                rec = (ArrayList<String>) object;
            } catch (IOException ex) {
                if(debugging) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ClassNotFoundException ex) {
                if(debugging) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  catch (java.lang.RuntimeException ex) {
                if(debugging) {
                    System.out.println("ERROR: while reading arraylist from the server");
                }
            }
            if(debugging) {
                for (String s : rec) {
                    System.out.println(s);
                }
            }


        }
    }


    @Override
    protected void onPostExecute(Void result) {
        textResponse.setText(response);
        if(debugging) {
            System.out.println("stopping timer");
        }
        timer.cancel();
        if(action.equalsIgnoreCase("recommend")) startshowRecomendationsActivity(rec);
        if(action.equalsIgnoreCase("recommendSingleMovie")) startshowRecomendationsActivity(rec);
        if(action.equalsIgnoreCase("getMoviesToRate")) startRateMoviesActivity();
        if(action.equalsIgnoreCase("search")) startRateMoviesActivity();
        if(action.equalsIgnoreCase("searchForRecommendation")) startspecificForRecommendationsActivity();

        super.onPostExecute(result);
    }
    public void startshowRecomendationsActivity(ArrayList<String> recom) {
        RecommendationHomePageActivity.setIsRecButtonClicked(false);
        RecommendationHomePageActivity.dismissDialog();
        if(recom.size()>0) {
            response = "Recommendation Done, Loading...";
            Intent intent = new Intent(context, showRecomendationsActivity.class);
            intent.putExtra(EXTRA_MESSAGE, recom);
            context.startActivity(intent);
            RecommendationHomePageActivity.setFullRecArrayList(recom);
        }else{
            if(timerFinished){
                Toast.makeText(context, "Error, please try again", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "Problem connecting to the server, check your connection and try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startspecificForRecommendationsActivity(){
        SearchForAMovieActivity.setIsButtonClicked(false);
        SearchForAMovieActivity.dismissDialog();
        if(movies.size()>0) {
            response = "Search Done, Loading...";
            Intent intent = new Intent(context, specificRecommendationsActivity.class);
            intent.putStringArrayListExtra("com.netdil.specificRecommendations", movies);
            context.startActivity(intent);
        }else{
            if(timerFinished) {
                Toast.makeText(context, "Error, please try again", Toast.LENGTH_SHORT).show();
            }else {
                if (errorHappend) {
                    Toast.makeText(context, "Problem connecting to the server, check your connection and try again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No movie found, try another one", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startPickAMovieForRecommendationActivity(){
        Intent intent = new Intent(context, PickAMovieForRecommendationActivity.class);
        intent.putStringArrayListExtra("com.netdil.selectMovieForRecommendation",movies);
        context.startActivity(intent);
    }
    public void startRateMoviesActivity() {
        if(action.equalsIgnoreCase("getMoviesToRate")) {
            RateHomePageActivity.setRateMoviesButtonClicked(false);
            RateHomePageActivity.dismissDialog();
        }else{
            SearchForAMovieActivity.setIsButtonClicked(false);
            SearchForAMovieActivity.dismissDialog();

        }
        if(movies.size()>0) {
            response = "Results done, Loading...";
            Intent intent = new Intent(context, RateMovies.class);
            intent.putStringArrayListExtra("com.netdil.StartRateEvent", movies);
            context.startActivity(intent);
        }else{
            if(timerFinished){
                Toast.makeText(context, "Error, please try again", Toast.LENGTH_SHORT).show();
            }else {
                if (errorHappend) {
                    Toast.makeText(context, "Problem connecting to the server, check your connection and try again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No movie found, try another one", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public  String getRecommendationResource(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String recSource = sharedPrefs.getString("recSource","1");
        return recSource;
    }

}