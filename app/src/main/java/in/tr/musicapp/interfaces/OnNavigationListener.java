/*
 * Created by Mohamed Ibrahim N
 * Created on : 17/11/17 9:20 PM
 * File name : OnNavigationListener.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 15/11/17 11:21 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.interfaces;

import android.net.Uri;

import in.tr.musicapp.model.Music;

public interface OnNavigationListener {
    public void onNavigationItemClickedListener(String title);

    public void onListItemClickedListener(String url, String name);

    public void onListItemClickedListener(Music music);

    public void onAddedPlaylist(Music music);

    public void onRemovePlaylist(int position);
}
