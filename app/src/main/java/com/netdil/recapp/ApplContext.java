package com.netdil.recapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by admin on 31/10/2016.
 */

public class ApplContext extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        ApplContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplContext.context;
    }
}
