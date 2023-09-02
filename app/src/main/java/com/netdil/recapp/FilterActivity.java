package com.netdil.recapp;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    EditText yearET,directorET,ratingET;
    Button filterButton, clearButton,genreButton,ageRatingButton,popularityButton;
    private boolean applyedFilter = false;
    private static String genre= "";
    private static String ageRating = "";
    private static String popularity = "";
    private static int genrePosition = 0 , ageRatingPosition = 0, popularityPosition = 0;
    private boolean selectedAFilter = false ;//gia na vgazw to toast minima an dn exei epileksei filtro
    private static boolean debugging = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getIntent();
        debugging = BuildConfig.DEBUG;
        yearET = (EditText) findViewById(R.id.filterByYear);
        directorET = (EditText) findViewById(R.id.filterByDirector);
        ratingET = (EditText) findViewById(R.id.filterByRating);
        filterButton = (Button) findViewById(R.id.filterButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        genreButton = (Button) findViewById(R.id.genreButton);
        ageRatingButton = (Button) findViewById(R.id.ageRatingButton);
        popularityButton = (Button) findViewById(R.id.popularityButton);

        checkForPrevFilter();


        genreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                myDialogFragment genres = new myDialogFragment();
                Bundle args = new Bundle();
                args.putString("calledFrom", "genres");
                args.putInt("prevPosition",genrePosition);
                genres.setArguments(args);
                genres.show(fm, "dialog_fragment");
                selectedAFilter = true;
            }
        });


        ageRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                myDialogFragment genres = new myDialogFragment();
                Bundle args = new Bundle();
                args.putString("calledFrom", "ageRating");
                args.putInt("prevPosition",ageRatingPosition);
                genres.setArguments(args);
                genres.show(fm, "dialog_fragment");
                selectedAFilter = true;
            }
        });

        popularityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                myDialogFragment genres = new myDialogFragment();
                Bundle args = new Bundle();
                args.putString("calledFrom", "popularity");
                args.putInt("prevPosition",popularityPosition);
                genres.setArguments(args);
                genres.show(fm, "dialog_fragment");
                selectedAFilter = true;
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecomendationsActivity.setMyRatings();
                showRecomendationsActivity.clearFilterArray();
                ratingET.setText("");
                yearET.setText("");
                directorET.setText("");
                setPopularityPosition(0);
                setAgeRatingPosition(0);
                setGenrePosition(0);
                ArrayList<String> filteredMovies = filterMovies(RecommendationHomePageActivity.getFullRecArrayList());
                if(debugging) {
                    System.out.println("filterdMovies size in clear: " + filteredMovies.size());
                }
                RecommendationHomePageActivity.setFilteredArrayList(filteredMovies);
                if(filteredMovies.isEmpty()){
                    Toast.makeText(FilterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }

                DemoFragment.setFilterState("clearing");
                applyedFilter = true;                
                finish();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecomendationsActivity.setMyRatings();
                String[] filter = new String[6];
                for (int i = 0; i < filter.length; i++) {
                    filter[i] = "";
                }


                String year = yearET.getText().toString();
                String director = directorET.getText().toString();
                String rating = ratingET.getText().toString();

                
                if (!year.equalsIgnoreCase("")) {
                    if(debugging) {
                        System.out.println("applyed: year filter");
                    }
                    filter[0] = year;
                }
                if (!director.equalsIgnoreCase("")) {
                    if(debugging) {
                        System.out.println("applyed: director filter");
                    }
                    filter[1] = director;
                }
                if (!rating.equalsIgnoreCase("")) {
                    if(debugging) {
                        System.out.println("applyed: rating filter");
                    }
                    try {
                        float num = Float.parseFloat(rating);
                        filter[2] = rating;
                    } catch (NumberFormatException e) {
                        Toast.makeText(ApplContext.getAppContext(), "Rating must be a number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                if (!genre.equalsIgnoreCase("")) {
                    if(debugging) {
                        System.out.println("applyed: genre filter");
                    }
                    filter[3] = genre;
                }
                if (!ageRating.equalsIgnoreCase("")) {
                    if(debugging) {
                        System.out.println("applyed: ageRating filter");
                    }
                    filter[4] = ageRating;
                }

                if (!popularity.equalsIgnoreCase("")) {
                    if(debugging) {
                        System.out.println("applyed: popularity filter");
                    }
                    filter[5] = popularity;
                }

                if(debugging) {
                    System.out.println("----------------------------------------");
                    System.out.println("Printing contents of filter array: ");
                    for (String s : filter) System.out.println(s);
                    System.out.println("----------------------------------------");
                }


                showRecomendationsActivity.setFilterArray(filter);
                ArrayList<String> filteredMovies = filterMovies(RecommendationHomePageActivity.getFullRecArrayList());
                if(filteredMovies.isEmpty()){
                    Toast.makeText(FilterActivity.this, "No results maching your filter, try another one", Toast.LENGTH_SHORT).show();
                    return;
                }
                RecommendationHomePageActivity.setFilteredArrayList(filteredMovies);
                DemoFragment.setFilterState("applyedFilter");
                applyedFilter = true;
                finish();


            }
        });




    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(!applyedFilter) {
            DemoFragment.setFilterState("noFilter");
        }
    }

    private boolean aTextFieldHasText(){
        boolean hasText = false;
        if(!yearET.getText().equals("")){
            System.out.println("yearET has text");
            hasText = true;
        }
        if(!directorET.getText().equals("")){
            System.out.println("directorET has text");
            hasText = true;
        }
        if(!ratingET.getText().equals("")){
            System.out.println("ratingET has text");
            hasText = true;
        }
        return hasText;
    }
    private void checkForPrevFilter(){
        String [] prevFilter = showRecomendationsActivity.getFilterArray();
        String [] gernes =  ApplContext.getAppContext().getResources().getStringArray(R.array.movieGenres);
        String [] ageRatings =  ApplContext.getAppContext().getResources().getStringArray(R.array.agerating);
        String [] popularities =  ApplContext.getAppContext().getResources().getStringArray(R.array.popularity);
        if(prevFilter!=null){
            if(!prevFilter[0].equalsIgnoreCase("")){
                yearET.setText(prevFilter[0]);
            }
            if(!prevFilter[1].equalsIgnoreCase("")){
                directorET.setText(prevFilter[1]);
            }
            if(!prevFilter[2].equalsIgnoreCase("")){
                ratingET.setText(prevFilter[2]);
            }
            if(!prevFilter[3].equalsIgnoreCase("")){
                for(int i=0;i<gernes.length;i++){
                    if(gernes[i].equalsIgnoreCase(prevFilter[3])){
                        genrePosition = i;
                    }
                }
               
            }

            if(!prevFilter[4].equalsIgnoreCase("")){
                for(int i=0;i<ageRatings.length;i++){
                    if(ageRatings[i].equalsIgnoreCase(prevFilter[4])){
                        ageRatingPosition = i;
                    }
                }
                
            }
            if(!prevFilter[5].equalsIgnoreCase("")){
                for(int i=0;i<popularities.length;i++){
                    if(popularities[i].equalsIgnoreCase(prevFilter[5])){
                        popularityPosition = i;
                    }
                }
                
            }
        }
    }




    public static boolean filterMovie(String []filter , String... movieInfo){
        //movieName + "-@-" + recom + "-@-" + movieUrl + "-@-" + imageUrl + "-@-" + ageRating + "-@-" + gerne + "-@-" + director + "-@-" + duration + "-@-" + yearInTheaters
        boolean show = true;
        String mName = movieInfo[0];
        String recommendation = movieInfo[1];
        String ageRating = movieInfo[4];
        String gerne = movieInfo[5];
        String director = movieInfo[6];
        String duration = movieInfo[7];
        String yearInTheaters = movieInfo[8];
        String maxPage = movieInfo[9];

        
        String yearForFilter = "";
        String directorForFilter = "";
        String ratingForFilter = "";
        String genreForFilter = "";
        String ageRatingForFilter = "";
        String popularityForFilter = "";
        

        if(!filter[0].equalsIgnoreCase("")){
            if(debugging) {
                System.out.println("Year filter active");
            }
            yearForFilter = filter[0];
        }
        if(!filter[1].equalsIgnoreCase("")){
            if(debugging) {
                System.out.println("Director filter active");
            }
            directorForFilter = filter[1];
        }
        if(!filter[2].equalsIgnoreCase("")){
            if(debugging) {
                System.out.println("rating filter active");
            }
            ratingForFilter = filter[2];
        }
        if(!filter[3].equalsIgnoreCase("")){
            if(debugging) {
                System.out.println("genre filter active");
            }
            genreForFilter = filter[3];
        }

        if(!filter[4].equalsIgnoreCase("")){
            if(debugging) {
                System.out.println("age rating filter active");
            }
            ageRatingForFilter = filter[4];
        }

        if(!filter[5].equalsIgnoreCase("")){
            if(debugging) {
                System.out.println("popularity filter active");
            }
            popularityForFilter = filter[5];
        }


        ArrayList<String> myRatings = showRecomendationsActivity.getMyRatings();
      
        for(String s: myRatings){            
            if(s.split("-@-")[0].equalsIgnoreCase(movieInfo[2])){             
                return false;
            }
        }     



        if(!yearForFilter.equalsIgnoreCase("")) {           
            if(!yearInTheaters.equalsIgnoreCase("Unknown")) {
                if (!yearInTheaters.equalsIgnoreCase(yearForFilter)) {                    
                    show = false;
                }
            }
        }


        if(!directorForFilter.equalsIgnoreCase("")) {
           
            if(!director.equalsIgnoreCase(directorForFilter)){                
                show = false;
            }
        }


        if(!ratingForFilter.equalsIgnoreCase("")) {           
            if(Float.valueOf(ratingForFilter) > Float.valueOf(recommendation)){                
                show = false;
            }
        }

        if(!genreForFilter.equalsIgnoreCase("")) {            
            if(!genreForFilter.equalsIgnoreCase("Any")){
                if (!gerne.equalsIgnoreCase("Unrated") && !gerne.contains(genreForFilter)){                    
                    show = false;
                }
            }
        }

        if(!ageRatingForFilter.equalsIgnoreCase("") ) {            
            if(!ageRatingForFilter.equalsIgnoreCase("Any")) {
                if (!ageRating.equalsIgnoreCase("Unrated") && !ageRating.equalsIgnoreCase(ageRatingForFilter)) {                    
                    show = false;
                }
            }
        }

        if(!popularityForFilter.equalsIgnoreCase("")){            
            if(popularityForFilter.equalsIgnoreCase("Medium")){
                if(Integer.valueOf(maxPage)<5){
                    show = false;
                }
            }else if(popularityForFilter.equalsIgnoreCase("High")){
                if(Integer.valueOf(maxPage)<10){
                    show = false;
                }

            }else if(popularityForFilter.equalsIgnoreCase("Very High")){
                if(Integer.valueOf(maxPage)<20){
                    show = false;
                }
            }
        }

        if(debugging) {
            if (show) {
                System.out.println("showing movie: " + mName);
            } else {
                System.out.println("not showing movie: " + mName);
            }
            System.out.println("----------------------------------------------");
        }

        return show;
    }


    private ArrayList<String> filterMovies(ArrayList<String> movies){
        ArrayList<String> filteredArrayList = new ArrayList<String>();
        for(String s: movies){
            if(filterMovie(showRecomendationsActivity.getFilterArray(), s.split("-@-"))){
                filteredArrayList.add(s);
            }
        }
        return filteredArrayList;
    }

    public static void setAgeRating(String ageRat) {
        ageRating = ageRat;
    }
    public static void setPopularity(String popul){
        popularity = popul;
    }
    public static void setGenre(String genr) {
        genre = genr;
    }

    public static void setPopularityPosition(int popularityPs) {      
        popularityPosition = popularityPs;      
    }

    public static void setAgeRatingPosition(int ageRatingPs) {      
        ageRatingPosition = ageRatingPs;     
    }

    public static void setGenrePosition(int genrePs) {      
        genrePosition = genrePs;      
    }
}
