/*
 * Created by Mohamed Ibrahim N
 * Created on : 6/12/17 8:34 PM
 * File name : HomeScreenActivity.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 6/12/17 8:29 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.ads.*;

import in.tr.musicapp.R;
import in.tr.musicapp.adapter.PlaylistItemAdapter;
import in.tr.musicapp.fragment.DownloadsFragment;
import in.tr.musicapp.fragment.FavoritesFragment;
import in.tr.musicapp.fragment.SearchFragment;
import in.tr.musicapp.interfaces.OnNavigationListener;
import in.tr.musicapp.model.Music;
import in.tr.musicapp.utility.AppPermissions;
import in.tr.musicapp.utility.DownloadFile;

public class HomeScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnNavigationListener {
    private static final String TAG = "HomeScreenActivity";
    private static final int STORAGE_CODE = 100;
    Toolbar toolbar;
    SimpleExoPlayer exoPlayer;
    SimpleExoPlayerView simpleExoPlayerView;
    DynamicConcatenatingMediaSource uriMediaSource;
    Map<MediaSource, Music> playlist;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private RewardedVideoAd rewardedVideoAd;
    private FragmentManager.OnBackStackChangedListener
            mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            syncActionBarArrowState();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                0,
                0
        ) {

            public void onDrawerClosed(View view) {
                syncActionBarArrowState();
            }

            public void onDrawerOpened(View drawerView) {
                toggle.setDrawerIndicatorEnabled(true);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.audio_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SearchFragment()).commit();
        checkRunTimePermissions();
        rewardVideoAds();

    }

    public void rewardVideoAds() {
        rewardedVideoAd = new RewardedVideoAd(this, getString(R.string.fb_fullscreen_ad));
        rewardedVideoAd.setAdListener(new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward

                // Call method to give reward
                // giveReward();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
            }
        });
        rewardedVideoAd.loadAd();
    }

    private void syncActionBarArrowState() {
        int backStackEntryCount =
                getSupportFragmentManager().getBackStackEntryCount();
        toggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
    }

    public void checkRunTimePermissions() {
        AppPermissions appPermissions = new AppPermissions(this);
        if (appPermissions.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Storage Permissions granted
        } else {
            appPermissions.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(HomeScreenActivity.this, "Storage permissions required for proper functioning of the app", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(HomeScreenActivity.this, "Storage Permissions granted", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            exit();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_downloaded) {
            // Replace the fragment with downloaded files fragment
            replaceFragment(new DownloadsFragment());
        } else if (id == R.id.nav_favorites) {
            // Replace the fragment with favorites fragment
            replaceFragment(new FavoritesFragment());
        } else if (id == R.id.nav_settings) {
            // Open app settings activity
            settings();
        } else if (id == R.id.nav_share) {
            shareApp(); //share the app url with friends
        } else if (id == R.id.nav_rate) {
            rateApp();  //rate the app in play store
        } else if (id == R.id.nav_exit) {
            exit();   //exit confirmation alert dialog
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.content,
                fragment).addToBackStack(null).commit();
    }

    public void settings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void shareApp() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_content));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_title)));
    }

    public void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void exit() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.exit_app)
                .setMessage(R.string.exit_message)
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog exitDialog = dialogBuilder.create();
        exitDialog.show();
    }

    @Override
    public void onNavigationItemClickedListener(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onListItemClickedListener(String url, String name) {
        prepareExoPlayerFromURL(url, name);
    }

    @Override
    public void onListItemClickedListener(Music music) {
        prepareExoPlayerFromFileUri(music);
    }

    @Override
    public void onAddedPlaylist(Music music) {
        if (uriMediaSource != null) {
            addMediaSource(music);
        } else {
            Toast.makeText(HomeScreenActivity.this, "No Music in the queue. Can't add", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemovePlaylist(int position) {
        playlist.remove(uriMediaSource.getMediaSource(position));
        uriMediaSource.removeMediaSource(position);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
        if (rewardedVideoAd != null) {
            rewardedVideoAd.destroy();
            rewardedVideoAd = null;
        }
        super.onDestroy();
        releaseExoPlayer();
    }

    /**
     * Prepares exoplayer for audio playback from a local file
     *
     * @param music
     */
    private void prepareExoPlayerFromFileUri(final Music music) {
        setUpExoPlayer(new DefaultTrackSelector(), new DefaultLoadControl());
        uriMediaSource = new DynamicConcatenatingMediaSource();
        playlist = new HashMap<>();
        addMediaSource(music);
        exoPlayer.prepare(uriMediaSource);
        exoPlayer.setPlayWhenReady(true);
        LinearLayout play_control = simpleExoPlayerView.findViewById(R.id.fullScreen);
        play_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFullScreenPlayer();
            }
        });
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (getCurrentMusic() != null) {
                    Music currentMusic = getCurrentMusic();
                    final String name = currentMusic.getName();
                    final Uri uri = currentMusic.getUri();
                    TextView musicTitle = simpleExoPlayerView.findViewById(R.id.exo_title);
                    musicTitle.setText(name);
                    musicTitle.setSelected(true);
                    ImageButton download = simpleExoPlayerView.findViewById(R.id.exo_download);
                    download.setVisibility(View.GONE);
                    ImageButton share = simpleExoPlayerView.findViewById(R.id.exo_share);
                    share.setVisibility(View.VISIBLE);
                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareFile(uri, name);
                        }
                    });
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    /**
     * Prepares exoplayer for audio playback from a remote URL audiofile. Should work with most
     * popular audiofile types (.mp3, .m4a,...)
     *
     * @param url Provide a Url in a form of http://blabla.bleble.com/blublu.mp3)
     */
    private void prepareExoPlayerFromURL(final String url, final String name) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        setUpExoPlayer(trackSelector, new DefaultLoadControl());
        ImageButton play = simpleExoPlayerView.findViewById(R.id.exo_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicConcatenatingMediaSource mediaSource = new DynamicConcatenatingMediaSource();
                mediaSource.addMediaSource(getMediaSourceFromURL(url));
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            }
        });

        TextView musicTitle = simpleExoPlayerView.findViewById(R.id.exo_title);
        musicTitle.setText(name);
        musicTitle.setSelected(true);
        ImageButton share = simpleExoPlayerView.findViewById(R.id.exo_share);
        share.setVisibility(View.GONE);
        ImageButton download = simpleExoPlayerView.findViewById(R.id.exo_download);
        download.setVisibility(View.VISIBLE);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoAd();
                DownloadFile.download(HomeScreenActivity.this, url, name);
            }
        });
    }

    public void showVideoAd() {
        if (rewardedVideoAd != null && rewardedVideoAd.isAdLoaded()) {
            rewardedVideoAd.show();
        }
    }

    public void setUpExoPlayer(TrackSelector trackSelector, LoadControl loadControl) {
        releaseExoPlayer();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        simpleExoPlayerView.setPlayer(exoPlayer);
    }

    public void addMediaSource(Music music) {
        MediaSource mediaSource = getMediaSourceFromURI(music.getUri());
        uriMediaSource.addMediaSource(mediaSource);
        playlist.put(mediaSource, music);
    }

    public MediaSource getMediaSourceFromURI(Uri uri) {
        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        return new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);
    }

    public MediaSource getMediaSourceFromURL(String url) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "exoplayer"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.parse(url),
                dataSourceFactory, extractorsFactory, null, null);
    }

    private void shareFile(Uri uri, String name) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share " + name));
    }

    private void releaseExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void setFullScreenPlayer() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_fullscreen_player);
        Toolbar toolbar = dialog.findViewById(R.id.dialog_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        ListView listView = dialog.findViewById(R.id.listView);
        List<Music> list = new ArrayList<Music>(playlist.values());
        PlaylistItemAdapter dataAdapter = new PlaylistItemAdapter(this, R.layout.playlist_song_item, list);
        listView.setAdapter(dataAdapter);
        dataAdapter.setCallback(this);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (getCurrentMusic() != null) {
                    Music currentMusic = getCurrentMusic();
                    final String name = currentMusic.getName();
                    final Uri uri = currentMusic.getUri();
                    AppCompatTextView musicTitle = dialog.findViewById(R.id.exo_title);
                    musicTitle.setText(name);
                    musicTitle.setSelected(true);
                    ImageButton share = dialog.findViewById(R.id.exo_share);
                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareFile(uri, name);
                        }
                    });
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });

        exoPlayer.setPlayWhenReady(false);
        SimpleExoPlayerView simplePlayerView = dialog.findViewById(R.id.fullscreen_view);
        simplePlayerView.setPlayer(exoPlayer);
        exoPlayer.setPlayWhenReady(true);
        dialog.show();
    }

    private Music getCurrentMusic() {
        if (uriMediaSource.getSize() > 0) {
            MediaSource currentMediaSource = uriMediaSource.getMediaSource(exoPlayer.getCurrentWindowIndex());
            return playlist.get(currentMediaSource);
        } else {
            return null;
        }
    }

}
