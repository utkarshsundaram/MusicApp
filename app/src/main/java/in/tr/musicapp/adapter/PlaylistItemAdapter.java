/*
 * Created by Mohamed Ibrahim N
 * Created on : 25/11/17 6:15 PM
 * File name : PlaylistItemAdapter.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 23/11/17 8:10 PM
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

import in.tr.musicapp.R;
import in.tr.musicapp.interfaces.OnNavigationListener;
import in.tr.musicapp.model.Music;
import in.tr.musicapp.utility.Utility;

public class PlaylistItemAdapter extends ArrayAdapter<Music> {

    private List<Music> musicArrayList = null;
    private OnNavigationListener onNavigationListener = null;

    public PlaylistItemAdapter(Context context, int textViewResourceId, List<Music> musicArrayList) {
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
        AppCompatTextView musicName;
        AppCompatImageView remove;
    }

    @NonNull
    @Override
    public View getView(final int position, View view, @NonNull final ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.playlist_song_item, null);
            holder = new ViewHolder();
            holder.musicName = view.findViewById(R.id.name);
            holder.remove = view.findViewById(R.id.item_remove);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String name = "Song name: " + musicArrayList.get(position).getName();
        holder.musicName.setText(name);
        holder.musicName.setTag(name);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromQueue(position);
                musicArrayList.remove(position);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    private void removeFromQueue(int position) {
        if (onNavigationListener != null) {
            onNavigationListener.onRemovePlaylist(position);
        }
    }

    public void setCallback(OnNavigationListener onNavigationListener) {
        this.onNavigationListener = onNavigationListener;
    }
}