/*
 * Created by Mohamed Ibrahim N
 * Created on : 29/11/17 9:09 PM
 * File name : SharedPrefUtils.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 29/11/17 9:09 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

public class SharedPrefUtils {
    private static SharedPreferences musicPreference;

    private static void setSharedPref(String key, String value, Context context) {
        musicPreference = context.getSharedPreferences(AppConstant.PREF_NAME, 0);
        SharedPreferences.Editor musicPreferenceEditor = musicPreference.edit();
        musicPreferenceEditor.putString(key, value);
        musicPreferenceEditor.apply();
    }

    private static String getSharedPref(String key, Context context) {
        musicPreference = context.getSharedPreferences(AppConstant.PREF_NAME, 0);
        return musicPreference.getString(key, null);
    }


    public static void setDownloadedFolder(String value, Context context) {

        setSharedPref(AppConstant.DOWNLOADED_DIRECTORY, value, context);

    }

    public static String getDownloadedFolder(Context context) {
        return !TextUtils.isEmpty(getSharedPref(AppConstant.DOWNLOADED_DIRECTORY, context)) ?
                getSharedPref(AppConstant.DOWNLOADED_DIRECTORY, context) :
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    }
}
