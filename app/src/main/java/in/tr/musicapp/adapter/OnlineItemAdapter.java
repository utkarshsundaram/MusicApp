/*
 * Created by Mohamed Ibrahim N
 * Created on : 20/11/17 1:21 AM
 * File name : OnlineItemAdapter.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 20/11/17 1:21 AM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.tr.musicapp.R;
import in.tr.musicapp.model.Music;
import in.tr.musicapp.utility.Utility;

public class OnlineItemAdapter extends ArrayAdapter<Music> {

    private List<Music> musicArrayList = null;

    public OnlineItemAdapter(Context context, int textViewResourceId, List<Music> musicArrayList) {
        super(context, textViewResourceId, musicArrayList);
        this.musicArrayList = musicArrayList;   //Original Music List - from online search
    }

    @Override
    public int getCount() {
        return musicArrayList.size();
    }

    @Nullable
    @Override
    public Music getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private class ViewHolder {
        AppCompatTextView musicName, artistName, duration;
        AppCompatImageView thumbnail;
    }

    @NonNull
    @Override
    public View getView(final int position, View view, @NonNull final ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.online_song_item, null);
            holder = new ViewHolder();
            holder.musicName = view.findViewById(R.id.name);
            holder.artistName = view.findViewById(R.id.artist);
            holder.duration = view.findViewById(R.id.duration);
            holder.thumbnail = view.findViewById(R.id.thumbnail);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String name = "Song name: " + musicArrayList.get(position).getName();
        holder.musicName.setText(name);
        holder.musicName.setTag(name);
        String artist = "Artist: " + musicArrayList.get(position).getArtistName();
        holder.artistName.setText(artist);
        holder.artistName.setTag(artist);
        long milliseconds = Long.parseLong(musicArrayList.get(position).getDuration());
        String timeDuration = "Duration: " + Utility.getLength(milliseconds);
        holder.duration.setText(timeDuration);
        holder.duration.setTag(timeDuration);
        String imageURL = musicArrayList.get(position).getImageLink();
        if (!TextUtils.isEmpty(imageURL)) {
            Picasso.with(parent.getContext())
                    .load(imageURL)
                    .placeholder(R.drawable.ic_music_thumbnail)
                    .resize(60, 60)
                    .centerCrop()
                    .error(R.drawable.ic_music_thumbnail)
                    .into(holder.thumbnail);
            holder.thumbnail.setTag(imageURL);
        }
        return view;
    }
}