package com.netdil.recapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.preference.PreferenceManager;

import static android.content.Context.MODE_PRIVATE;

public class DemoFragment extends Fragment {
    public String movieNam = "";
    private String movieUrl = "";
    private boolean forward = true , backward = false;
    private static String filterState = "waiting";
    private  Handler handler;
    private Runnable run;
    private boolean isRatingChanged = false;
    private boolean watchingListIconIsHidden = false;
    private boolean debugging= true;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.fragment_demo, container, false);
        debugging = BuildConfig.DEBUG;
        // Get the arguments that was supplied when
        // the fragment was instantiated in the
        // CustomPagerAdapter
        Bundle args = getArguments();
        //movieName + "-@-" + recom + "-@-" + movieUrl + "-@-" + imageUrl + "-@-" + ageRating + "-@-" + gerne + "-@-" + director + "-@-" + duration + "-@-" + yearInTheaters

        
        if( args.getString("movieLine").equalsIgnoreCase("end-@-3-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing")){
            rootView = inflater.inflate(R.layout.end_of_results, container, false);
        }
        String []movieInfo = args.getString("movieLine").split("-@-");
        final String movieInfoFull = args.getString("movieLine");

        if(debugging) {
            System.out.println("ta periexomena to movieInfo sto fragment:");
            for (String s : movieInfo) System.out.println(s);
        }
        this.movieNam = movieInfo[0];
        this.movieUrl = movieInfo[2];

        FloatingActionButton myFab = (FloatingActionButton) rootView.findViewById(R.id.myFab);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFilterActivity();
                handler = new  Handler();
                run = new Runnable() {
                    @Override
                    public void  run() {//code to run every second                       
                        handler.postDelayed(this,1000);
                        if(filterState.equalsIgnoreCase("noFilter")){
                            stopHandler();
                         }
                        if(filterState.equalsIgnoreCase("applyedFilter")){
                            startFilter2();
                            filterState="waiting";
                            stopHandler();
                        }
                        if(filterState.equalsIgnoreCase("clearing")){
                            startFilter2();
                            filterState="waiting";
                            stopHandler();
                        }
                    }
                };
                handler.post(run);
            }
        });

        if( args.getString("movieLine").equalsIgnoreCase("end-@-3-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing")){
            return rootView;
        }



        final ImageView img = (ImageView) rootView.findViewById(R.id.movieImage);
        //movieUrl + "-@-" + movieName + "-@-" + imageUrl;
        String s = movieUrl +"-@-"+movieNam+"-@-"+movieInfo[3];

        //movieUrl, movieName, imageUrl
        final ImageView watchingListImage = (ImageView) rootView.findViewById(R.id.addToWatchingListImage);
        if(watchingListIconIsHidden){
            watchingListImage.setVisibility(View.GONE);
        }
        if(debugging) {
            System.out.println("demo Tag:" + s);
        }
        watchingListImage.setTag(s);
        watchingListImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicFunctions.addToWatchingList(watchingListImage.getTag().toString(),"watchingList.txt");
                watchingListImage.setVisibility(View.GONE);
                watchingListIconIsHidden = true;

            }
        });

        if(debugging) {
            System.out.println("rb tag: " + s);
        }
        final RatingBar rb = (RatingBar) rootView.findViewById(R.id.demoSingleMovieRatingBar);
        rb.setTag(s);
        rb.setNumStars(5);
        rb.setMax(5);
        rb.setStepSize((float) 0.5);




        final ImageView deleteImage = (ImageView) rootView.findViewById(R.id.deleteImage);
        if(!isRatingChanged){
            deleteImage.setVisibility(View.GONE);
        }
        deleteImage.setTag(s.split("-@-")[0]);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRatingChanged) {                    
                    isRatingChanged = false;
                    publicFunctions.deleteAMovie(deleteImage,"demoFragment");
                    rb.setRating(0);
                    watchingListImage.setVisibility(View.VISIBLE);
                    watchingListIconIsHidden = false;
                    deleteImage.setVisibility(View.GONE);                 
                }else{
                    Toast.makeText(ApplContext.getAppContext(), "You have not rate this movie yet", Toast.LENGTH_SHORT).show();
                }

            }
        });


        rb.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        publicFunctions.setStarColor(ratingBar);
                        if(fromUser) {
                            publicFunctions.saveNewRating((String) ratingBar.getTag(), rating);
                            publicFunctions.setStarColor(ratingBar);
                            isRatingChanged = true;
                            watchingListImage.setVisibility(View.GONE);
                            watchingListIconIsHidden = true;
                            deleteImage.setVisibility(View.VISIBLE);
                        }
                    }
                });

        if(!MyPreferencesActivity.checkForDataSave()) {

            if (showRecomendationsActivity.alreadyDownloadedImage(movieUrl)) {//an idi iparxei to i eikona sto hashmap dn ti ksana katevazw
                if(debugging) {
                    System.out.println("i tainia: " + movieNam + " url:" + movieUrl + " iparxei idi sto hashmap opote tin vazw apo ekei");
                }
                img.setImageBitmap(showRecomendationsActivity.getMovieImage(movieUrl));
            } else {
                if(debugging) {
                    System.out.println("i tainia: " + movieNam + " url:" + movieUrl + " den iparxei sto hashmap opote tin katevazw");
                }
                img.setImageResource(R.drawable.noimage);
                img.setMaxHeight(300);
                img.setMaxWidth(200);
                new ImageDownloader(img).execute(movieInfo[3]);

            }
        }else{
            if(debugging) {
                System.out.println("Saving data, image is not downloading");
            }
            img.setImageResource(R.drawable.noimage);
        }
        
        String year = movieInfo[8];
        final String searchInGoogle;
        if(!year.equalsIgnoreCase("Unknown")){
            searchInGoogle = "https://www.google.com/search?q="+movieNam.replace(" " , "+").replace("&","") +"+" +movieInfo[8] + "+movie";
        }else{
            searchInGoogle = "https://www.google.com/search?q="+movieNam.replace(" " , "+").replace("&","") + "+movie";
        }
        if(debugging) {
            System.out.println("google: " + searchInGoogle);
        }
        img.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);               
                intent.setData(Uri.parse(searchInGoogle));
                if(debugging) {
                    System.out.println("to url tis tainias pou anigei ston browser: " + movieUrl);
                }
                startActivity(intent);
            }
        }));
        try {
            ((TextView) rootView.findViewById(R.id.movieName)).setText(movieInfo[0]);
            ((TextView) rootView.findViewById(R.id.genre)).setText("\n" + movieInfo[5]);
            ((TextView) rootView.findViewById(R.id.recom)).setText("\n" + movieInfo[1]);
            ((TextView) rootView.findViewById(R.id.ageRating)).setText("\n" + movieInfo[4]);
            ((TextView) rootView.findViewById(R.id.director)).setText("\n" + movieInfo[6]);
            ((TextView) rootView.findViewById(R.id.duration)).setText("\n" + movieInfo[7]);
            ((TextView) rootView.findViewById(R.id.yearInTheaters)).setText("\n" + movieInfo[8]);
        }catch(Exception e){
            if(debugging) {
                System.out.println("ERROR: stin tainia: " + movieNam + " " + e.getMessage());
            }
        }
        return rootView;
    }





   class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public ImageDownloader(ImageView bmImage ) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            String url = urls[0];
            Bitmap mIcon = null;

            try {
                InputStream in = new java.net.URL(url).openStream();
                if(debugging) {
                    System.out.println("KANEI NEW CONNECTION, stin tainia:" + movieNam);
                    System.out.println("downloading image: " + url);
                }
                mIcon = BitmapFactory.decodeStream(in);
                mIcon = Bitmap.createScaledBitmap(mIcon, 200, 300, true);
            }catch(RuntimeException e ){
                if(debugging) {
                    System.out.println("RuntimeException in imagedownload");
                }
            }
            catch (Exception e) {
                if(debugging) {
                    Log.e("Error", e.getMessage());
                }
                mIcon = BitmapFactory.decodeResource(ApplContext.getAppContext().getResources(),
                        R.drawable.noimage);
                return mIcon;
            }


            showRecomendationsActivity.addMovieImage(movieUrl,mIcon);

            if(mIcon == null) {
                mIcon = BitmapFactory.decodeResource(ApplContext.getAppContext().getResources(),
                        R.drawable.noimage);
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }




    private void startFilterActivity(){
        Intent intent = new Intent(ApplContext.getAppContext(), FilterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplContext.getAppContext().startActivity(intent);
    }
    private void stopHandler(){
        handler.removeCallbacks(run);
    }
    public void startFilter2(){
        ArrayList<FragmentList> fragments = showRecomendationsActivity.getFragmentList();
        for(FragmentList fragment: fragments){
            if(debugging) {
                System.out.println("deleting fragment: " + fragment.getmName());
            }
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(fragment.getFragment()).commitAllowingStateLoss();
            if(debugging) {
                System.out.println("deleted fragment: " + fragment.getmName());
            }

        }
        ArrayList<String> rec = RecommendationHomePageActivity.getFilteredArrayList();

        Intent intent = new Intent(ApplContext.getAppContext(), showRecomendationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.netdil.ShowRecommendations", rec);
        ApplContext.getAppContext().startActivity(intent);
        showRecomendationsActivity.notifyMe();
        showRecomendationsActivity.setLastShowedPosition(0);       
        getActivity().finish();
    }


    public static String getFilterState() {
        return filterState;
    }

    public static void setFilterState(String filterSt) {
        filterState = filterSt;
    }

}