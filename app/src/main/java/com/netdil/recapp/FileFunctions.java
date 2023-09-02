package com.netdil.recapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_DOCUMENTS;

/**
 * Created by admin on 10/10/2016.
 */

public class FileFunctions extends Activity{


    public static boolean deleteAFile(String filename){
       return new File(filename).delete();
    }



    public static ArrayList<String> getMyRatings(){
        boolean debugging= BuildConfig.DEBUG;
        ArrayList<String> mergedArraylist = new ArrayList<String>();

        if(getRecommendationResource().equalsIgnoreCase("1")){
            ArrayList<String> rottenAr = new ArrayList<String>();
            ArrayList<String> localAr = new ArrayList<String>();
            if(debugging) {
                System.out.println("reading both files");
            }
            rottenAr = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(), "rottenUserRatings.txt");
            localAr = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(), "user_ratings_netdil.txt");
            Collections.reverse(localAr);
            mergedArraylist.addAll(localAr);
            boolean found = false;
            for(String s : rottenAr){
                for(String st : mergedArraylist){
                    if(s.split("-@-")[0].equalsIgnoreCase(st.split("-@-")[0])){
                        found=true;
                        break;                       
                    }
                }
                if(!found){
                    mergedArraylist.add(s);
                }
                found = false;
            }

        }
        if(getRecommendationResource().equalsIgnoreCase("2")){
            if(debugging) {
                System.out.println("reading local file");
            }
            mergedArraylist = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(), "user_ratings_netdil.txt");
            Collections.reverse(mergedArraylist);
        }
        if(getRecommendationResource().equalsIgnoreCase("3")){
            if(debugging) {
                System.out.println("reading rotten file");
            }
            mergedArraylist = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(), "rottenUserRatings.txt");
        }

        return mergedArraylist;
    }

    public static String getRecommendationResource(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ApplContext.getAppContext());
        String recSource = sharedPrefs.getString("recSource","1");
        return recSource;
    }

    public static void writeToInternalStorage(Context ctx, ArrayList<String> userRatings, String filename, boolean append){
        boolean debugging= BuildConfig.DEBUG;
        try {
            FileOutputStream fOut;
            if(append){
                fOut = ctx.openFileOutput(filename,
                        MODE_PRIVATE | MODE_APPEND);
            }else{
                fOut = ctx.openFileOutput(filename,
                        MODE_PRIVATE);
            }


            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            if(debugging) {
                System.out.println("writing file: " + filename);
            }
            for(String s: userRatings) {
                osw.write(s + "\n");
            }
            osw.flush();
            osw.close();



        } catch (IOException ioe)
        {ioe.printStackTrace();}
    }

    public  static ArrayList<String> readFromInternalStorage(Context ctx, String filename){
        //user_ratings_netdil.txt
        boolean debugging= BuildConfig.DEBUG;
        ArrayList<String> ar = new ArrayList<String>();
            FileInputStream fIn = null;
            try {
                fIn = ctx.openFileInput(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(fIn));
            String line = null;

            int countLines = 0;
       
            try {
                while ((line = in.readLine()) != null) {
                    ar.add(line);                
                    countLines++;
                }
            } catch (IOException e) {
                if(debugging) {
                    System.out.println("ERROR: while reading file:" + filename + " from internal storage");
                    e.printStackTrace();
                }
            }
        if(debugging) {
            System.out.println("done printing file");
            System.out.println("the file has " + countLines + " lines");
        }
        return ar;

    }

    public static int getFileSize(Context ctx ,String filename){
        boolean debugging= BuildConfig.DEBUG;
        FileInputStream fIn = null;
        try {
            fIn = ctx.openFileInput(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(fIn));
        String line = null;

        int countLines = 0;
        if(debugging) {
            System.out.println("printing contents of file " + filename);
        }
        try {
            while ((line = in.readLine()) != null) {
                countLines++;
            }
        } catch (IOException e) {
            if(debugging) {
                System.out.println("ERROR: in getFileSize while reading file:" + filename + " from internal storage");
                e.printStackTrace();
            }
        }
        if(debugging) {
            System.out.println("FILESIZE: " + countLines);
        }
        return countLines;
    }



}
