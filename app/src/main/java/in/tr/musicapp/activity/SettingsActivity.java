/*
 * Created by Mohamed Ibrahim N
 * Created on : 25/11/17 6:46 PM
 * File name : SettingsActivity.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 25/11/17 6:46 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.ads.*;

import java.io.File;

import in.tr.musicapp.R;
import in.tr.musicapp.utility.SharedPrefUtils;
import yogesh.firzen.filelister.FileListerDialog;
import yogesh.firzen.filelister.OnFileSelectedListener;

public class SettingsActivity extends AppCompatActivity {
    AppCompatTextView folder;
    AppCompatImageButton selector;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        folder = (AppCompatTextView) findViewById(R.id.folder);
        selector = (AppCompatImageButton) findViewById(R.id.selector);
        setValues(SharedPrefUtils.getDownloadedFolder(this));
        selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFolder();
            }
        });
        //fb banner ads
        adView = new AdView(this, getString(R.string.fb_banner_id), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();

    }

    private void setValues(String path) {
        folder.setText(path);
    }

    private void selectFolder() {
        FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(this, R.style.AppTheme);
        fileListerDialog.setOnFileSelectedListener(new OnFileSelectedListener() {
            @Override
            public void onFileSelected(File file, String path) {
                SharedPrefUtils.setDownloadedFolder(path, SettingsActivity.this);
                setValues(path);
            }
        });
        fileListerDialog.setDefaultDir(SharedPrefUtils.getDownloadedFolder(this));
        fileListerDialog.setFileFilter(FileListerDialog.FILE_FILTER.DIRECTORY_ONLY);
        fileListerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}
