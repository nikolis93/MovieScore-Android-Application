package com.netdil.recapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class specificFragment extends Fragment {
    public String movieNam = "";
    private String movieUrl = "";
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
        View rootView = inflater.inflate(R.layout.specific_fragment, container, false);

        // Get the arguments that was supplied when
        // the fragment was instantiated in the
        // CustomPagerAdapter
        Bundle args = getArguments();
        //movieName + "-@-" + recom + "-@-" + movieUrl + "-@-" + imageUrl + "-@-" + ageRating + "-@-" + gerne + "-@-" + director + "-@-" + duration + "-@-" + yearInTheaters

        //String []movieInfo =showRecomendationsActivity.getRec(args.getInt("page_position")).split("-@-");
        if( args.getString("movieLine").equalsIgnoreCase("end-@-3-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing")){
            rootView = inflater.inflate(R.layout.end_of_results, container, false);

        }
        String []movieInfo = args.getString("movieLine").split("-@-");

        int position = args.getInt("page_position");

        if(debugging) {
            System.out.println("ta periexomena to movieInfo sto fragment:");
        }
        for(String s: movieInfo) System.out.println(s);
        this.movieNam = movieInfo[0];
        this.movieUrl = movieInfo[2];



        if( args.getString("movieLine").equalsIgnoreCase("end-@-3-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing-@-nothing")){
            return rootView;
        }
        final ImageView img = (ImageView) rootView.findViewById(R.id.movieImage);

        String s = movieUrl +"-@-"+movieNam+"-@-"+movieInfo[3];

        final ImageView watchingListImage = (ImageView) rootView.findViewById(R.id.addToWatchingListImage);
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
                    //Toast.makeText(ApplContext.getAppContext(), "Deleted movie: " + ((String) ((ImageView) v).getTag()).split("-@-")[1], Toast.LENGTH_SHORT).show();
                    isRatingChanged = false;
                    publicFunctions.deleteAMovie(deleteImage,"demoFragment");
                    rb.setRating(0);
                    View singleMovieClicked = specificRecommendationsActivity.getSingleMovieClicked();
                    singleMovieClicked.setVisibility(View.VISIBLE);
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
                        if(fromUser) {
                            publicFunctions.saveNewRating((String) ratingBar.getTag(), rating);
                            publicFunctions.setStarColor(ratingBar);
                            isRatingChanged = true;
                            View singleMovieClicked = specificRecommendationsActivity.getSingleMovieClicked();
                            singleMovieClicked.setVisibility(View.GONE);
                            watchingListImage.setVisibility(View.GONE);
                            watchingListIconIsHidden = true;
                            deleteImage.setVisibility(View.VISIBLE);
                        }
                    }
                });

        if(!MyPreferencesActivity.checkForDataSave()) {
            if(specificRecommendationsActivity.getImageClicked()!=null){
                img.setImageBitmap(specificRecommendationsActivity.getImageClicked());
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
                // intent.setData(Uri.parse(movieUrl));
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

}
