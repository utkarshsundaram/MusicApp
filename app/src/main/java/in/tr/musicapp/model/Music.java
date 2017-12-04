/*
 * Created by Mohamed Ibrahim N
 * Created on : 17/11/17 10:23 PM
 * File name : Music.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 17/11/17 10:23 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.Date;

import in.tr.musicapp.database.DbObject;

public class Music implements DbObject {
    private String redirect;
    private String duration;
    private String licence;
    private String downloadable;
    @SerializedName("image_link")
    private String imageLink;
    @SerializedName("stream_link")
    private String streamLink;
    @SerializedName("artist_name")
    private String artistName;
    private String videoType;
    private String name;
    private String videoId;
    private int id;
    private Uri uri;
    private long size;
    private Date createdDate;
    private boolean isFavorite = false;

    public Music() {
    }

    public Music(String redirect, String duration, String licence, String downloadable, String imageLink, String streamLink, String artistName, String videoType, String name, String videoId) {
        this.redirect = redirect;
        this.duration = duration;
        this.licence = licence;
        this.downloadable = downloadable;
        this.imageLink = imageLink;
        this.streamLink = streamLink;
        this.artistName = artistName;
        this.videoType = videoType;
        this.name = name;
        this.videoId = videoId;
    }

    public Music(String duration, String artistName, String name, int id, Uri uri, long size, Date createdDate, boolean isFavorite) {
        this.duration = duration;
        this.artistName = artistName;
        this.name = name;
        this.id = id;
        this.uri = uri;
        this.size = size;
        this.createdDate = createdDate;
        this.isFavorite = isFavorite;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getDownloadable() {
        return downloadable;
    }

    public void setDownloadable(String downloadable) {
        this.downloadable = downloadable;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getStreamLink() {
        return streamLink;
    }

    public void setStreamLink(String streamLink) {
        this.streamLink = streamLink;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public void buildObjectFromCursor(Cursor cursor) {

        String path = cursor.getString(0);
        this.duration = cursor.getString(1);
        this.artistName = cursor.getString(2);
        this.id = cursor.getInt(3);
        this.name = cursor.getString(4);
        this.size = cursor.getLong(5);
        File file = new File(path);
        this.uri = Uri.fromFile(file);
        this.createdDate = new Date(file.lastModified());

    }

    @Override
    public ContentValues getContentValues() {
        return null;
    }

}
