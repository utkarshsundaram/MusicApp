/*
 * Created by Mohamed Ibrahim N
 * Created on : 20/11/17 6:39 PM
 * File name : MusicDateComparator.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 20/11/17 6:39 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.utility;

import java.util.Comparator;

import in.tr.musicapp.model.Music;

public class MusicDateComparator implements Comparator<Music> {

    @Override
    public int compare(Music music1, Music music2) {
        return music2.getCreatedDate().compareTo(music1.getCreatedDate());
    }
}
