package com.bluesky.exoplayerexample.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by mac on 7/5/17.
 */

public class MusicObject implements Parcelable , Serializable , Cloneable{
    private String url ;
    private String title ;
    public MusicObject(String title , String url) {
        this.url = url ;
        this.title = title;
    }

    public String getMusicUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }



    public MusicObject(Parcel in) {
        url = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(title);
    }

    public static final Creator<MusicObject> CREATOR = new Creator<MusicObject>() {
        public MusicObject createFromParcel(Parcel in) {
            return new MusicObject(in);
        }

        public MusicObject[] newArray(int size) {
            return new MusicObject[size];
        }
    };


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
