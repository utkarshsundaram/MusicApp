package com.bluesky.exoplayerexample.Utils;


import com.bluesky.exoplayerexample.BuildConfig;

/**
 * Created by mac on 7/5/17.
 */

public class Constant {
//    public static String MAIN_PACKAGE = "com.mediaprostudio.musicplayerbeta";

    public static String MAIN_PACKAGE = BuildConfig.APPLICATION_ID;
    public static final String PLAYER_SHUFFLE_MODE = "PLAYER_SHUFFLE_MODE";
    public static final String PLAYER_REPEATE_MODE = "PLAYER_REPEATE_MODE";
    public static final String NOTIFICATION_MODE = "NOTIFICATION_MODE";
    public static final int NOTIFICATION_MODE_ON = 0 ;
    public static final int NOTIFICATION_MODE_OFF = 1 ;
    public static final String BUNDLE_TITLE_NOTIFICATION = MAIN_PACKAGE + "BUNDLE_TITLE_NOTIFICATION";
    public static final String BUNDLE_BODY_NOTIFICATION = MAIN_PACKAGE + "BUNDLE_BODY_NOTIFICATION";

    public static final String KEYWORD_EXTRA = MAIN_PACKAGE + "KEYWORDEXTRA";
}
