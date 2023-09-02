package com.netdil.recapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendationForASingleMovieActivity extends AppCompatActivity {

    TextView response;
    EditText movieUrlEditText;
    Button singleMovieRecButton;
    Context ctx;
    private static boolean debugging= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_for_asingle_movie);

        Intent intent = getIntent();

        ctx = RecommendationForASingleMovieActivity.this;
        movieUrlEditText = (EditText) findViewById(R.id.movieUrlEditText);
        singleMovieRecButton = (Button) findViewById(R.id.singleMovieRecButton);
        response = (TextView) findViewById(R.id.responseTextView);

        singleMovieRecButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                response.setText("");
                Toast.makeText(ctx, "recomendation button clicked", Toast.LENGTH_SHORT).show();               
                int filesize = FileFunctions.getMyRatings().size();
                if(filesize< 20){
                    response.setText("Please Rate atleast 20 movies");
                    return;
                }else{
                    Client Client = new Client(response, RecommendationForASingleMovieActivity.this,"recommendSingleMovie",movieUrlEditText.getText()
                            .toString());
                    Client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    
                }

            }
        });
    }


}
