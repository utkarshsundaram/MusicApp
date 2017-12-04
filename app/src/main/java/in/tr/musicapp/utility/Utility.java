/*
 * Created by Mohamed Ibrahim N
 * Created on : 17/11/17 8:02 PM
 * File name : Alert.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 17/11/17 8:02 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.utility;

import android.net.Uri;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utility {
    public static String getLength(long milliseconds) {
        return String.format(Locale.ENGLISH, "%02d:%02d sec",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

}
