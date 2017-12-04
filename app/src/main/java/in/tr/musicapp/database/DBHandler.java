/*
 * Created by Mohamed Ibrahim N
 * Created on : 20/11/17 11:43 PM
 * File name : DBHandler.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 20/11/17 11:43 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.tr.musicapp.model.Music;
import in.tr.musicapp.utility.AppConstant;
import in.tr.musicapp.utility.SharedPrefUtils;

public class DBHandler extends SQLiteOpenHelper {

    private static DBHandler mInstance = null;

    private static final String TAG = "DBHandler";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "musicapp";

    public static DBHandler getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DBHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }


    private DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_FAVORITES = "CREATE TABLE IF NOT EXISTS " + AppConstant.TABLE_FAVORITES + "("
                + AppConstant.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + AppConstant.MEDIA_ID + " INTEGER NOT NULL, "
                + AppConstant.CR_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";

        db.execSQL(CREATE_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    //Adding favorite in local database for further querying media

    public long addFavorite(int mediaId) {

        long id = 0;
        try {
            SQLiteDatabase dataBase = getWritableDatabase();

            if (!isMediaIdAlreadyExists(dataBase, mediaId)) {
                ContentValues favContentValues = new ContentValues();
                favContentValues.put(AppConstant.MEDIA_ID, mediaId);
                id = dataBase.insertWithOnConflict(AppConstant.TABLE_FAVORITES, null, favContentValues, SQLiteDatabase.CONFLICT_REPLACE);
            } else {
                id = 0;
            }
        } finally {

        }
        return id;
    }

    //Checking for duplicate in favorite while adding favorite

    private boolean isMediaIdAlreadyExists(SQLiteDatabase db, int mediaId) {
        Cursor cursor = null;
        try {
            String Query = "select * from " + AppConstant.TABLE_FAVORITES + " where " + AppConstant.MEDIA_ID + " = " + mediaId;
            cursor = db.rawQuery(Query, null);
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }

    //Getting all the favorites to pass selection arguments for content resolver

    private List<String> getAllFavorites() {

        Cursor cursor = null;
        List<String> mediaId = new ArrayList<String>();
        try {
            SQLiteDatabase dataBase = this.getReadableDatabase();
            cursor = dataBase.rawQuery("select " + AppConstant.MEDIA_ID + " from " + AppConstant.TABLE_FAVORITES, null);
            mediaId.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    mediaId.add(cursor.getString(cursor.getColumnIndex(AppConstant.MEDIA_ID)));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();

        }
        return mediaId;
    }

    //Deleting the favorite mediaId from the table
    public void removeFavorite(int mediaId) {
        try {
            SQLiteDatabase dataBase = getWritableDatabase();
            String Query = "delete from " + AppConstant.TABLE_FAVORITES + " where " + AppConstant.MEDIA_ID + " = " + mediaId;
            dataBase.execSQL(Query);
            Log.d(TAG, Query);
        } finally {

        }
    }

    //Getting the downloaded files using the device content resolver
    public List<Music> getAllAudioFromDevice(final Context context) {

        final List<Music> downloadedAudioList = new ArrayList<>();

        String folder = SharedPrefUtils.getDownloadedFolder(context);
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.SIZE,};
        String selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + " = "
                + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + " = "
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ") AND "
                + MediaStore.Audio.Media.DATA + " like ? ";

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, new String[]{"%" + folder + "%"}, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music music = new Music();
                music.buildObjectFromCursor(cursor);
                downloadedAudioList.add(music);
            }
            cursor.close();
        }

        return downloadedAudioList;
    }

    //Getting the favorite files using the device content resolver with the local data ids
    public List<Music> getFavoriteAudioFromDevice(final Context context) {

        final List<Music> downloadedAudioList = new ArrayList<>();

        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.SIZE,};
        String selection = MediaStore.Audio.Media._ID + " IN ( " + TextUtils.join(", ", getAllFavorites()) + " )";

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music music = new Music();
                music.buildObjectFromCursor(cursor);
                music.setFavorite(true);
                downloadedAudioList.add(music);
            }
            cursor.close();
        }

        return downloadedAudioList;
    }
}