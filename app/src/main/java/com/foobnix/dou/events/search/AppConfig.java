package com.foobnix.dou.events.search;

import android.content.Context;
import android.content.SharedPreferences;

import com.foobnix.dou.events.search.net.DouServices;

/**
 * Created by ivan-dev on 11.03.16.
 */
public class AppConfig {

    public static final AppConfig get = new AppConfig();
    public static final String SP_NAME = "MY";

    public String tag = DouServices.ALL_TAGS;
    public String city = DouServices.ALL_CITYIES;

    public static void load(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        get.tag = sp.getString("tag", DouServices.ALL_TAGS);
        get.city = sp.getString("city", DouServices.ALL_CITYIES);
    }

    public static void save(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString("tag", get.tag).putString("city", get.city).commit();
    }
}