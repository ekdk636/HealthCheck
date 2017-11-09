package com.test.ekdk636.healthcheck;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by KJY on 2017-11-01.
 */

public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        Typekit.getInstance().addNormal(Typekit.createFromAsset(this, "fonts/cookie_cotton.ttf"));
    }
}
