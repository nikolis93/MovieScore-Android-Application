package com.netdil.recapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by admin on 9/11/2016.
 */

public class publicFunctions {



    public static int randomWithRange(int min, int max) {
        int range = Math.abs(max - min) - 1;
        return (int) (Math.random() * range) + (min <= max ? min : max);


    }

    public static boolean isInMyRatings(String line){
        boolean debugging = BuildConfig.DEBUG;
        ArrayList<String> myRatings = FileFunctions.getMyRatings();
        boolean found = false;
        String fullLine = "";
        String movieUrl = line.split("-@-")[0];
        for(int i=0;i<myRatings.size();i++){
            fullLine = myRatings.get(i);
            if(fullLine.split("-@-")[0].equalsIgnoreCase(movieUrl)){
                found = true;
                if(debugging) {
                    System.out.println(line + " found in my ratings");
                }
                break;
            }
        }
        if(!found){
            if(debugging) {
                System.out.println(line + " not found in: ratings");
            }
        }
        return found;
    }

    public static void deleteAMovieFromWatchingList(View movie,String line){
        boolean debugging = BuildConfig.DEBUG;
        ArrayList<String> fileContents  = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),"watchingList.txt");
        String movieUrl = line.split("-@-")[0];
        boolean found = false;

        for (Iterator<String> iterator = fileContents.iterator(); iterator.hasNext(); ) {
            String s = iterator.next();
            if(s.split("-@-")[0].equalsIgnoreCase(movieUrl)){
                fileContents.remove(s);
                FileFunctions.writeToInternalStorage(ApplContext.getAppContext(),fileContents,"watchingList.txt",false);
                Toast.makeText(ApplContext.getAppContext(), line.split("-@-")[1]+ " deleted from your watchlist", Toast.LENGTH_SHORT).show();
                found = true;
                if(debugging) {
                    System.out.println("deleted movie: " + line.split("-@-")[1] + " found: " + found);
                }
                movie.setVisibility(View.GONE);
               return;
            }
        }
        if(!found){
            Toast.makeText(ApplContext.getAppContext(), line.split("-@-")[1]+ " not found in your watchlist", Toast.LENGTH_SHORT).show();
        }

    }
    public static void addToWatchingList(String line, String filename){
        if(isInMyRatings(line)){
            Toast.makeText(ApplContext.getAppContext(), "You have rate "+line.split("-@-")[1] + " and it cannot be added to watchlist", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean debugging = BuildConfig.DEBUG;
        ArrayList<String> fileContents  = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),filename);
        if(debugging) {
            System.out.println("printing contets of " + filename);
            for (String s : fileContents) System.out.println(s);
        }
        String movieUrl = line.split("-@-")[0];
        boolean found = false;
        for(String s: fileContents){
            if(s.split("-@-")[0].equalsIgnoreCase(movieUrl)){
                found = true;
                break;
            }
        }

        if(found){
            Toast.makeText(ApplContext.getAppContext(), line.split("-@-")[1]+" is already in your watchlist", Toast.LENGTH_SHORT).show();
        }else{
            fileContents.add(line);
            FileFunctions.writeToInternalStorage(ApplContext.getAppContext(),fileContents,filename,false);
            Toast.makeText(ApplContext.getAppContext(), line.split("-@-")[1]+" added to watchlist", Toast.LENGTH_SHORT).show();
        }


    }

    public static void addNewLineToAHistoryFile(String line , String filename){//gia na grafeis sto arxeio rottenIdsAndNames or searchedMovieNames mia nea grami
        boolean debugging = BuildConfig.DEBUG;
        ArrayList<String> fileContents  = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),filename);
        while(fileContents.size()>50){
            if(debugging) {
                System.out.println("removing from " + filename);
            }
            fileContents.remove(0);
        }
        if(debugging) {
            System.out.println("newLine: " + line);
        }
        fileContents.add(line);
        FileFunctions.writeToInternalStorage(ApplContext.getAppContext(),fileContents,filename,false);

    }

    public static void deleteAMovie(View movie, String calledFrom){
        boolean debugging = BuildConfig.DEBUG;

        String tag = (String) movie.getTag();
        if(debugging) {
            System.out.println("Tag: " + tag);
        }
        ArrayList<String> fileContents  = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),"user_ratings_netdil.txt");
        String fileLine = "";
        boolean found = false;
        for(int i=0;i<fileContents.size();i++){
            fileLine = fileContents.get(i);
            if(fileLine.split("-@-")[0].equalsIgnoreCase(tag)){
                if(debugging) {
                    System.out.println("Deleted movie: " + fileLine.split("-@-")[2]);
                }
                Toast.makeText(ApplContext.getAppContext(), "Deleted movie: " +fileLine.split("-@-")[2] , Toast.LENGTH_SHORT).show();
                fileContents.remove(i);
                if(calledFrom.equalsIgnoreCase("myRatingsActivity")) {
                    movie.setVisibility(View.GONE);
                }
                found = true;
                break;
            }
        }
        if(!found){
            String fullLine = "";
            ArrayList<String> rottenRatings  = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),"rottenUserRatings.txt");
            if(debugging) {
                System.out.println("its rotten rating! ");
            }
            for(int j=0;j<rottenRatings.size();j++){
                fullLine = rottenRatings.get(j);
                if(fullLine.split("-@-")[0].equalsIgnoreCase(tag)){
                    Toast.makeText(ApplContext.getAppContext(),  fullLine.split("-@-")[2] + " is a rotten rating and cannot be deleted", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            return ;
        }
        FileFunctions.writeToInternalStorage(ApplContext.getAppContext(),fileContents,"user_ratings_netdil.txt",false);
    }



    public static boolean isInFile(String line, String fileName){
        boolean debugging = BuildConfig.DEBUG;
        ArrayList<String> fileContents  = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),fileName);
        boolean found = false;
        String fullLine = "";
        String movieUrl = line.split("-@-")[0];
        for(int i=0;i<fileContents.size();i++){
            fullLine = fileContents.get(i);
            if(fullLine.split("-@-")[0].equalsIgnoreCase(movieUrl)){
                found = true;
                if(debugging) {
                    System.out.println(line + " found in: " + fileName);
                }
                break;
            }
        }
        if(!found){
            if(debugging) {
                System.out.println(line + " not found in: " + fileName);
            }
        }
        return found;
    }
    public static void saveNewRating(String tag , float rating){
        boolean debugging = BuildConfig.DEBUG;
        ArrayList<String> fileContents  = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),"user_ratings_netdil.txt");
        boolean found = false;
        String fullLine = "";
        String movieUrl = tag.split("-@-")[0];
        for(int i=0;i<fileContents.size();i++){
            fullLine = fileContents.get(i);
            if(fullLine.split("-@-")[0].equalsIgnoreCase(movieUrl)){
                fileContents.remove(i);
                fileContents.add(i,replaceLine(fullLine , rating));
                found = true;
                Toast.makeText(ApplContext.getAppContext(), tag.split("-@-")[1] + " " + rating, Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if(!found){
            String [] parts = tag.split("-@-");
            String newLine = parts[0] + "-@-" + rating + "-@-"  + parts[1] + "-@-" + parts[2];
            Toast.makeText(ApplContext.getAppContext(), tag.split("-@-")[1] + " " + rating, Toast.LENGTH_SHORT).show();
            if(debugging) {
                System.out.println("newLine: " + newLine);
            }
            fileContents.add(newLine);
        }

        if(isInFile(tag,"watchingList.txt")){
            deleteAMovieFromWatchingList(new View(ApplContext.getAppContext()),tag);
        }
        FileFunctions.writeToInternalStorage(ApplContext.getAppContext(),fileContents,"user_ratings_netdil.txt",false);
    }
    public static String replaceLine(String line, float newRating){
        //movieUrl , rating , movieName , imageUrl
        boolean debugging = BuildConfig.DEBUG;
        String [] lineParts = line.split("-@-");
        String newLine = lineParts[0] + "-@-" + newRating + "-@-" + lineParts[2] + "-@-" + lineParts[3] ;
        if(debugging) {
            System.out.println("new line: " + newLine);
        }

        return newLine;
    }

    public static void setStarColor(RatingBar ratingBar){
        boolean debugging = BuildConfig.DEBUG;
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        if(debugging) {
            System.out.println("color changed in : " + ((String) ratingBar.getTag()).split("-@-")[1]);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
