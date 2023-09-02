package com.netdil.recapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class SearchForAMovieActivity extends AppCompatActivity {
    TextView response;
    Button searchButton;
    Context ctx;
    private AutoCompleteTextView autoCompTextView;
    private static boolean isButtonClicked = false;
    private static ProgressDialog mProgressDialog;
    private static boolean isDialogShowing = false;
    ArrayList<String> searchedMovieNames = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private boolean debugging= true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_amovie);


        Intent intent = getIntent();

        debugging = BuildConfig.DEBUG;

        final String action = intent.getStringExtra("com.netdil.SearchForAMovieActivity");
        ctx = SearchForAMovieActivity.this;
        searchButton = (Button) findViewById(R.id.searchButton);
        response = (TextView) findViewById(R.id.responseTextViewSearch);
        autoCompTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewSearchForAMovie);

        initAdapter();


        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_actionbar);
        }

        autoCompTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompTextView.showDropDown();
            }
        });     


        mProgressDialog = new ProgressDialog(ctx);
        if(isDialogShowing){
            if(debugging) {
                System.out.println("showing dialog again");
            }
            mProgressDialog.setMessage("Connecting ...");
            mProgressDialog.show();
        }

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!isButtonClicked) {

                    String movieName = autoCompTextView.getText().toString();
                    if (movieName.equalsIgnoreCase("")) {
                        Toast.makeText(ctx, "Please Enter A Movie Name", Toast.LENGTH_SHORT).show();
                    } else {
                        isButtonClicked = true;
                        mProgressDialog.setMessage("Connecting ...");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                        isDialogShowing = true;

                        Client Client = new Client(response, SearchForAMovieActivity.this, action, movieName);
                        Client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);                        

                        hideKeyboard();
                        ArrayList<String> fileContents = FileFunctions.readFromInternalStorage(ApplContext.getAppContext(),"searchedMovieNames.txt");
                        boolean found = false;
                        for(String s: fileContents){
                            if(s.split("-@-")[0].equalsIgnoreCase(movieName)){
                                found = true;
                            }
                        }
                        if(!found) {
                            publicFunctions.addNewLineToAHistoryFile(movieName,"searchedMovieNames.txt");
                            initAdapter();
                        }else{
                            if(debugging) {
                                System.out.println("to " + movieName + "iparxei idi sto arxeio");
                            }
                        }



                    }
                }else{
                    Toast.makeText(ctx, "Another action in progress", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        response.setText("");
        isButtonClicked = false;
        dismissDialog();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( mProgressDialog!=null && mProgressDialog.isShowing() ){
            mProgressDialog.cancel();
        }
    }


    private void initAdapter(){
        searchedMovieNames = FileFunctions.readFromInternalStorage(ctx,"searchedMovieNames.txt");
        Collections.reverse(searchedMovieNames);
        adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,searchedMovieNames);
        autoCompTextView.setAdapter(adapter);
    }
    public static void setIsButtonClicked(boolean isBtnClicked) {
       isButtonClicked = isBtnClicked;
    }


    public static void dismissDialog(){
        mProgressDialog.dismiss();
        isDialogShowing = false;
    }


    private void hideKeyboard(){
        publicFunctions.hideKeyboard(this);
    }

}
