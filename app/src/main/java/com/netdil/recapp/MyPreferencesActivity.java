package com.netdil.recapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;

public class MyPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Context ctx = MyPreferencesActivity.this;
    EditTextPreference name;
    private boolean debugging= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        debugging = BuildConfig.DEBUG;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Map<String, ?> preferencesMap = sharedPrefs.getAll();


        getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.movieb2));

        Iterator it = preferencesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next(); //current entry in a loop
            if(debugging) {
                System.out.println("key: " + entry.getKey());
            }
            if(entry.getKey().toString().equalsIgnoreCase("username")){
              
            }
             if(entry instanceof  EditTextPreference){
                 if(debugging) {
                     System.out.println("VREHIKE STO 1O WHILE");
                 }
             }
        }

        checkValues();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }
    }
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if(debugging) {
            System.out.println("sharedprf: " + sharedPreferences);
            System.out.println("key: " + key);
            System.out.println("onSharedPreferenceChanged called");
        }
        if (key.equals("username")) {
            SharedPreferences preferences = ctx.getSharedPreferences("com.netdil.recapp", MODE_PRIVATE);
            String value = preferences.getString("username","none" );  //default is true
            if(debugging) {
                System.out.println("value: " + value);
            }
        }
    }
    private void checkValues()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strUserName = sharedPrefs.getString("username", "NA");
        boolean dataSave = sharedPrefs.getBoolean("dataSave",false);
        String recSource = sharedPrefs.getString("recSource","1");

        String msg = "Cur Values: ";
        msg += "\n userName = " + strUserName;
        msg += "\n dataSave = " + dataSave;
        msg += "\n recSource = " + recSource;
        if(debugging) {
            System.out.println(msg);
        }        
    }

    public static boolean checkForDataSave(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ApplContext.getAppContext());
        boolean dataSave = sharedPrefs.getBoolean("dataSave",false);        
        return dataSave;
    }


    public static String getRottenUserNamePref(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ApplContext.getAppContext());
        String strUserName = sharedPrefs.getString("username", "NA");

        System.out.println("pref rottenUserName: " + strUserName);
        return strUserName;
    }
}