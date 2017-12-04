/*
 * Created by Mohamed Ibrahim N
 * Created on : 30/11/17 6:44 PM
 * File name : SuggestionProvider.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 30/11/17 6:44 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.utility;

import android.content.SearchRecentSuggestionsProvider;

public class SuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "in.tr.musicapp.utility.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
