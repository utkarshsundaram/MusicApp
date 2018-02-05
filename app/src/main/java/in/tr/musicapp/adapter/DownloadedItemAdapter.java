/*
 * Created by Mohamed Ibrahim N
 * Created on : 5/2/18 5:51 PM
 * File name : DownloadedItemAdapter.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 5/2/18 3:06 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2018. All rights reserved.
 */

package in.tr.musicapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.tr.musicapp.BuildConfig;
import in.tr.musicapp.R;
import in.tr.musicapp.database.DBHandler;
import in.tr.musicapp.interfaces.OnNavigationListener;
import in.tr.musicapp.model.Music;
import in.tr.musicapp.utility.Utility;

public class DownloadedItemAdapter extends ArrayAdapter<Music> {

    private ArrayList<Music> musicArrayList;
    private List<Music> filteredMusicArrayList = null;
    private OnNavigationListener onNavigationListener = null;
    private DBHandler dbHandler;

    public DownloadedItemAdapter(Context context, int textViewResourceId, List<Music> musicArrayList) {
        super(context, textViewResourceId, musicArrayList);
        this.filteredMusicArrayList = musicArrayList;   //Filtered Music List --- By default it has all the fetched records
        this.musicArrayList = new ArrayList<Music>();   //Original Music List --- Without search applied
        this.musicArrayList.addAll(musicArrayList);     //Copying all the values to the Original list
        dbHandler = DBHandler.getInstance(context);
    }

    @Override
    public int getCount() {
        return filteredMusicArrayList.size();
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
        AppCompatImageView thumbnail, menu;
    }

    @NonNull
    @Override
    public View getView(final int position, View view, @NonNull final ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.downloaded_song_item, null);
            holder = new ViewHolder();
            holder.musicName = view.findViewById(R.id.name);
            holder.artistName = view.findViewById(R.id.artist);
            holder.duration = view.findViewById(R.id.duration);
            holder.thumbnail = view.findViewById(R.id.thumbnail);
            holder.menu = view.findViewById(R.id.item_menu);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final String name = "Song name: " + filteredMusicArrayList.get(position).getName();
        holder.musicName.setText(name);
        holder.musicName.setTag(name);
        String artist = "Artist: " + filteredMusicArrayList.get(position).getArtistName();
        holder.artistName.setText(artist);
        holder.artistName.setTag(artist);
        long milliseconds = Long.parseLong(filteredMusicArrayList.get(position).getDuration());
        String timeDuration = "Duration: " + Utility.getLength(milliseconds);
        holder.duration.setText(timeDuration);
        holder.duration.setTag(timeDuration);
        String imageURL = filteredMusicArrayList.get(position).getImageLink();
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
        final Uri uri = filteredMusicArrayList.get(position).getUri();
        final boolean isFavorite = filteredMusicArrayList.get(position).isFavorite();
        final int id = filteredMusicArrayList.get(position).getId();
        final Music music = filteredMusicArrayList.get(position);
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(parent.getContext(), view);
                popup.getMenuInflater().inflate(R.menu.music_menu,
                        popup.getMenu());
                if (isFavorite) {
                    MenuItem menuItem = popup.getMenu().findItem(R.id.add_to_favorite);
                    menuItem.setTitle(R.string.remove_favorite);
                }
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.play:
                                //play the music
                                playMusic(music);
                                break;
                            case R.id.add_to_queue:
                                //add music to the current playing queue
                                addToQueue(music);
                                break;
                            case R.id.add_to_favorite:
                                //add to favorite list
                                if (isFavorite) {
                                    dbHandler.removeFavorite(id);
                                    musicArrayList.remove(getItem(position));
                                    filteredMusicArrayList.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    long favId = dbHandler.addFavorite(id);
                                    if (favId > 0) {
                                        Toast.makeText(getContext(), name + " Added to favorites", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), name + " Already added to favorites", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            case R.id.open_folder:
                                openFolder(uri);
                                break;
                            case R.id.delete:
                                //delete the file
                                deleteFile(uri, name, position);
                                break;
                            case R.id.share_file:
                                //share the file
                                shareFile(uri, name);
                                break;
                            default:
                                break;
                        }

                        return true;
                    }
                });
            }
        });
        return view;
    }

    private void playMusic(Music music) {
        if (onNavigationListener != null) {
            onNavigationListener.onListItemClickedListener(music);
        }
    }

    private void addToQueue(Music music) {
        if (onNavigationListener != null) {
            onNavigationListener.onAddedPlaylist(music);
        }
    }

    private void openFolder(Uri uri) {
        File file = new File(uri.getPath());
        Uri fileUri = Uri.parse(file.getParentFile().getPath());
        Intent openFolder = new Intent(Intent.ACTION_VIEW);
        openFolder.setDataAndType(fileUri, "resource/folder");
        if (openFolder.resolveActivityInfo(getContext().getPackageManager(), 0) != null) {
            getContext().startActivity(openFolder);
        } else {
            Toast.makeText(getContext(), "File explorer not found. Can't open.", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteFile(final Uri uri, final String name, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.delete) + " " + name)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(uri.getPath());
                        if (file.exists()) {
                            if (file.delete()) {
                                getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                musicArrayList.remove(getItem(position));
                                filteredMusicArrayList.remove(position);
                                Toast.makeText(getContext(), "Deleted " + name, Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "Can't Delete " + name, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog deleteDialog = dialogBuilder.create();
        deleteDialog.show();
    }

    private void shareFile(Uri uri, String name) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        getContext().startActivity(Intent.createChooser(share, "Share " + name));
    }


    public void search(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        filteredMusicArrayList.clear();
        if (charText.length() == 0) {
            filteredMusicArrayList.addAll(musicArrayList);
        } else {
            for (Music ep : musicArrayList) {
                if (((!TextUtils.isEmpty(ep.getName())) && (ep.getName()
                        .toLowerCase(Locale.getDefault()).contains(charText)))
                        || ((!TextUtils.isEmpty(ep.getArtistName())) && (ep.getArtistName()
                        .toLowerCase(Locale.getDefault()).contains(charText)))) {
                    filteredMusicArrayList.add(ep);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCallback(OnNavigationListener onNavigationListener) {
        this.onNavigationListener = onNavigationListener;
    }
}