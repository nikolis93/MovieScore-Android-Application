package com.netdil.recapp;


import android.widget.Toast;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RTCTools {

    static long lastCon = 0;
    static int period = 700;
    private static boolean debugging =  BuildConfig.DEBUG;;
    private static Document connect(String url) {
        long elapsed = System.currentTimeMillis() - lastCon;
        if (elapsed < period) {
            try {
                Thread.sleep(period - elapsed);
            } catch (Exception e) {
            }
        }
        lastCon = System.currentTimeMillis();
        try {
            return Jsoup.connect(url).timeout(100 * 1000).get();
        } catch (Exception e) {
            if(debugging) {
                System.out.println("ERROR: FTEEI TO CONNECTION " + e.getMessage());
            }
            CrawlUserAsyncTask.setMessage("Problem while importing your ratings, please check your connection");
            return null;
        }
    }

    public static ArrayList<String> crawlUser(String url,StringBuffer uName) {
        url = "https://www.rottentomatoes.com/user/id/" + url+ "/ratings";
        if(debugging) {
            System.out.println("user profile url: " + url);
        }
        ArrayList<String> userRatings = new ArrayList<String>(); //edw mpenoun oles oi tainies pou exei kanei rating enas user
        Document doc = null;
        doc = connect(url);
        if (doc == null) {
            if(debugging) {
                System.out.println("ERROR: null doc in crawlUser");
            }
            CrawlUserAsyncTask.setMessage("Problem while importing your ratings, please make sure you entered the right profile id or check your connection");
            return null;
        }

        uName.replace(0, uName.length(), doc.getElementsByTag("h3").get(2).html());
        if(debugging) {
            System.out.println("UserName: " + uName);
        }

        Elements els = doc.getElementsByClass("media-body");

        float userRating;
        String movieName;
        String movieYear;
        String movieUrl;
        String imageUrl;
        if(debugging) {
            System.out.println("els :" + els.size());
        }
        int counter = 0;
        for (Element e : els) {
            if(debugging) {
                System.out.println("On movie " + counter + " of " + els.size());
            }
            userRating = e.getElementsByClass("glyphicon-star").size();
            try {
                String checkForHalfRating = e.getElementsByTag("div").get(3).html();
                if (checkForHalfRating.contains("½")) {//gia na paroume to miso asteri
                    userRating += 0.5;                   
                }
            } catch (Exception exp) {
                if(debugging) {
                    System.out.println("ERROR: in halfRating:  " + exp.getMessage());
                }
                return userRatings;
            }

            if (userRating == 0) {
                continue;
            }
            imageUrl = doc.getElementsByClass("bottom_divider").get(counter++).getElementsByTag("img").attr("src");
            movieName = e.getElementsByTag("a").get(1).html();
            try {
                movieYear = e.getElementsByTag("span").get(0).html();
                // movieName += " " + movieYear;
            } catch (Exception ex) {
                if(debugging) {
                    System.out.println("ERROR: stin tainia: " + movieName);
                    System.out.println(ex.getMessage());
                }
                continue;
            }

            movieUrl = "https://www.rottentomatoes.com" + e.getElementsByTag("a").get(1).attr("href");
            userRatings.add(movieUrl + "-@-" + userRating + "-@-" + movieName + "-@-" + imageUrl);
            
        }
        if(debugging) {
            System.out.println("Done crawling user " + url);
        }
        CrawlUserAsyncTask.setMessage("Imported ratings from user " + uName);
        return userRatings;
    }
}
