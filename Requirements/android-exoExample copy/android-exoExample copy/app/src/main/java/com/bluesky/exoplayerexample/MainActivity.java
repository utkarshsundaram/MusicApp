package com.bluesky.exoplayerexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bluesky.exoplayerexample.Model.MusicObject;
import com.bluesky.exoplayerexample.PlayerService.PlayerService;
import com.bluesky.exoplayerexample.PlayerService.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PlayerBroadcastReceiver mPlayerBroadcastReceiver;
    private IntentFilter mIntentFilter;


    private ImageView mPlayPauseBtn;
    private ImageView mBackBtn ;
    private ImageView mNextBtn ;
    private RecyclerView mRecycleView ;
    private TextView nameTextView;
    private SeekBar mSeekBar ;
    private Handler mTimeHandle = new Handler();
    private Runnable timtRunable ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlayPauseBtn = (ImageView)findViewById(R.id.playpause_btn);
        mNextBtn = (ImageView)findViewById(R.id.next_btn);
        mBackBtn = (ImageView)findViewById(R.id.back_btn);
        mRecycleView = (RecyclerView)findViewById(R.id.recyclerview);
        nameTextView = (TextView)findViewById(R.id.name);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);

        List<MusicObject> objects = new ArrayList<MusicObject>();
        objects.add(new MusicObject("Marseilles -- The Arrival", "https://archive.org/download/count_monte_cristo_0711_librivox/count_of_monte_cristo_001_dumas.mp3"));
        objects.add(new MusicObject("Father and Son", "https://archive.org/download/count_monte_cristo_0711_librivox/count_of_monte_cristo_002_dumas.mp3"));
        objects.add(new MusicObject("The Catalans", "https://archive.org/download/count_monte_cristo_0711_librivox/count_of_monte_cristo_003_dumas.mp3"));

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(new ListMusicObjectAdapter(objects , this));

        initBroadcast();


        mPlayPauseBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        timtRunable = new Runnable() {
            @Override
            public void run() {
                mSeekBar.setMax(PlayerUtil.getTotalTime());
                Log.e("Aaa", PlayerUtil.getTotalTime() + "");
                mSeekBar.setProgress(PlayerUtil.getCurrentTime());

                Log.e("Aaa", PlayerUtil.getCurrentTime() + "");
                mTimeHandle.postDelayed(this , 500);
            }
        };

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    if(PlayerUtil.playerIsInit()){
                        PlayerUtil.seek(i);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeHandle.removeCallbacks(timtRunable);
        PlayerUtil.stop();
        PlayerUtil.unbindAll();
    }

    private void initBroadcast() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(PlayerService.PLAYSTATE_CHANGED);
        mIntentFilter.addAction(PlayerService.SONG_END);
        mIntentFilter.addAction(PlayerService.SONG_PREPARING);
        mIntentFilter.addAction(PlayerService.SONG_PREPARE_FAIL);
        mIntentFilter.addAction(PlayerService.SETUP_SONG);
        mIntentFilter.addAction(PlayerService.REFRESH_PLAYLIST);
        mIntentFilter.addAction(PlayerService.PLAYLIST_CHANGE);

        mPlayerBroadcastReceiver = new PlayerBroadcastReceiver();
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUpdate();

    }
    public void startUpdate() {
        registerReceiver(mPlayerBroadcastReceiver, mIntentFilter);
    }

    public void stopUpdate() {
        try {
            unregisterReceiver(mPlayerBroadcastReceiver);
            // playerBroadcastReceiver = null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view == mPlayPauseBtn){
            if(!PlayerUtil.playerIsInit()){
                return;
            }
            if(PlayerUtil.isPlaying()){
                PlayerUtil.pause();
            }else {
                PlayerUtil.play();
            }
        }else if (view == mBackBtn){
            if(!PlayerUtil.playerIsInit()){
                return;
            }
            PlayerUtil.back();
        }else if (view == mNextBtn){
            PlayerUtil.next();
        }
    }

    public class PlayerBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // String action = intent.getAction();
            if (PlayerService.SONG_PREPARING.equals(intent.getAction())) {
//                Log.e("Player", "SONG_PREPARING");
            } else if (PlayerService.PLAYSTATE_CHANGED.equals(intent
                    .getAction())) {
                if(PlayerUtil.isPlaying()){
                    mPlayPauseBtn.setSelected(true);
                }else {
                    mPlayPauseBtn.setSelected(false);

                }
//                Log.e("Player", "PLAYSTATE_CHANGED");
            } else if (PlayerService.PLAYLIST_CHANGE.equals(intent.getAction())) {
                boolean playerIsinit = PlayerUtil.playerIsInit();
//                Log.e("playerisInit", playerIsinit + " ");
                if (!playerIsinit) {
                    return;
                }

            } else if (PlayerService.SETUP_SONG.equals(intent.getAction())) {
                if(PlayerUtil.playerIsInit()) {
                    nameTextView.setText(PlayerUtil.getCurrentSongPlay().getTitle());
                    mTimeHandle.postDelayed(timtRunable, 500);
                }

            } else if (PlayerService.SONG_END.equals(intent.getAction())) {

            } else if (PlayerService.SONG_ERROR.equals(intent.getAction())) {
//                Log.e("MainActivity", "SONG_ERROR");
                // have error while play one song
            } else if (PlayerService.SONG_PREPARE_FAIL.equals(intent
                    .getAction())) {
//                Log.e("MainActivity", "SONG_PREPARE_FAIL");
            }
        }

    }
}
