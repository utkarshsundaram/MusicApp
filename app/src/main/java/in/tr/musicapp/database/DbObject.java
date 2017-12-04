/*
 * Created by Mohamed Ibrahim N
 * Created on : 21/11/17 12:40 AM
 * File name : DbObject.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 25/9/17 2:06 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.database;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONObject;


public interface DbObject {

    void buildObjectFromCursor(Cursor cursor);

    ContentValues getContentValues();
}
