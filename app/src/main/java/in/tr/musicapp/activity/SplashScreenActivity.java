/*
 * Created by Mohamed Ibrahim N
 * Created on : 17/11/17 5:59 PM
 * File name : SplashScreenActivity.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 17/11/17 5:59 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import in.tr.musicapp.R;

public class SplashScreenActivity extends AppCompatActivity {
    private CountDownTimer splashTimer;
    private AppCompatImageView musicLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startSplashTimer();
    }

    private void startSplashTimer() {
        splashTimer = new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
                startLogoAnimation();
            }

            public void onFinish() {
                try {
                    startHomeScreenActivity();
                } catch (Exception ex) {
                    splashTimer.cancel();
                }
            }
        }.start();
    }

    private void startLogoAnimation() {
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        zoomIn.reset();
        musicLogo = (AppCompatImageView) findViewById(R.id.music_logo);
        musicLogo.clearAnimation();
        musicLogo.startAnimation(zoomIn);
    }

    private void startHomeScreenActivity() {
        Intent homeScreenActivity = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
        startActivity(homeScreenActivity);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        splashTimer.cancel();
        splashTimer.onFinish();
    }
}
