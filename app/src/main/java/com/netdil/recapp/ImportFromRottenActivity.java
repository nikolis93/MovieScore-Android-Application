package com.netdil.recapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class ImportFromRottenActivity extends AppCompatActivity {

    TextView response;
    Button crawlButton;
    Context ctx;
    private AutoCompleteTextView autoCompTextView;
    private static boolean isButtonClicked = false;
    private static ProgressDialog mProgressDialog ;
    private static boolean isDialogShowing = false;
    ArrayList<String> readFromFile;
    ArrayList<rottenIdAndName> ids;
    public static class rottenIdAndName{
        private int id;
        private String name;
        public  rottenIdAndName(String s){
            id = Integer.valueOf(s.split("-@-")[0]);
            name = s.split("-@-")[1];
        }

        @Override
        public String toString() {
            return String.valueOf(id) + " " + name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_from_rotten);

        Intent intent = getIntent();


        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_actionbar);
        }

        ctx = ImportFromRottenActivity.this;
        crawlButton = (Button) findViewById(R.id.crawlButton);
        response = (TextView) findViewById(R.id.responseTextView);
        autoCompTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String rottenUserName = sharedPrefs.getString("username", "None");
        response.setText("Current rottentomatoes profile: " + rottenUserName);

        initAdapter();


        autoCompTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAdapter();
                autoCompTextView.showDropDown();
            }
        });
        autoCompTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long arg3) {

                autoCompTextView.setText(ids.get(position).toString().split(" ")[0]);                
            }
        });
        mProgressDialog = new ProgressDialog(ctx);

        if(isDialogShowing){           
            mProgressDialog.setMessage("Connecting ...");
            mProgressDialog.show();
        }

        crawlButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!isButtonClicked) {
                    if (autoCompTextView.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ctx, "Please enter a profile id", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            int num = Integer.parseInt(autoCompTextView.getText().toString());
                            // is an integer!
                        } catch (NumberFormatException e) {
                            Toast.makeText(ctx, "Profile id must be a number", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        mProgressDialog.setMessage("Importing ...");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                        isDialogShowing = true;
                        isButtonClicked = true;

                        CrawlUserAsyncTask crawlUserAT = new CrawlUserAsyncTask(autoCompTextView.getText()
                                .toString(), response, ctx);
                        crawlUserAT.execute();
                        hideKeyboard();
                    }
                }else{
                    Toast.makeText(ctx, "Another action in progress", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( mProgressDialog!=null && mProgressDialog.isShowing() ){
            mProgressDialog.cancel();
        }
    }

    public void initAdapter(){
        readFromFile = FileFunctions.readFromInternalStorage(ctx,"rottenIdsAndNames.txt");
        ids = new ArrayList<rottenIdAndName>();
        Collections.reverse(readFromFile);
        for(String s: readFromFile){
            ids.add(new rottenIdAndName(s));
        }

        ArrayAdapter<rottenIdAndName> adapter = new ArrayAdapter<rottenIdAndName>
                (this,android.R.layout.simple_list_item_1,ids);
        autoCompTextView.setAdapter(adapter);
    }
    public static void setIsButtonClicked(boolean isButtonClicked) {
        ImportFromRottenActivity.isButtonClicked = isButtonClicked;
    }

    public static void dismissDialog(){
        mProgressDialog.dismiss();
        isDialogShowing = false;
    }
    private  void hideKeyboard(){
        publicFunctions.hideKeyboard(this);
    }

}
