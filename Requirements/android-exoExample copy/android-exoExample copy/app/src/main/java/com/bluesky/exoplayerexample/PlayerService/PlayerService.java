package com.bluesky.exoplayerexample.PlayerService;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.bluesky.exoplayerexample.Model.MusicObject;
import com.bluesky.exoplayerexample.Utils.Constant;
import com.devbrackets.android.exomedia.AudioPlayer;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//import com.devbrackets.android.exomedia.core.exception.NativeMediaPlaybackException;
//import com.devbrackets.android.exomedia.core.exception.NativeMediaPlaybackException;

/**
 * Created by mac on 7/5/17.
 */

public class PlayerService extends Service {
    public static final int NOW = 1;
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int PLAYBACKSERVICE_STATUS = 1;
    public static final int SHUFFLE_OFF = 0;
    public static final int SHUFFLE_ON = 1;
    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;

    public static final String PLAYSTATE_CHANGED = (Constant.MAIN_PACKAGE + "playstatechanged");
    public static final String SONG_PREPARING = (Constant.MAIN_PACKAGE + "preparingsong");
    public static final String PLAY_RADIO = (Constant.MAIN_PACKAGE + "playRadio");
    public static final String STOP_RADIO = (Constant.MAIN_PACKAGE + "playRadio");
    public static final String SONG_PREPARE_FAIL = (Constant.MAIN_PACKAGE + "preparefail");
    public static final String SONG_END = (Constant.MAIN_PACKAGE + "endsong");
    public static final String SONG_ERROR = (Constant.MAIN_PACKAGE + "songerror");
    public static final String SONG_RADIO_RESUME = (Constant.MAIN_PACKAGE + "SONG_RADIO_RESUME");
    public static final String META_CHANGED = (Constant.MAIN_PACKAGE + "metachanged");
    public static final String QUEUE_CHANGED = (Constant.MAIN_PACKAGE + "queuechanged");
    public static final String SERVICECMD = (Constant.MAIN_PACKAGE + "musicservicecommand");
    public static final String SETUP_SONG = (Constant.MAIN_PACKAGE + "setupSong");
    public static final String REFRESH_PLAYLIST = (Constant.MAIN_PACKAGE + "refreshplaylsit");
    public static final String PLAYLIST_CHANGE = (Constant.MAIN_PACKAGE + "playlistchange");

    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String CMDPLAYNOW = "Play this song";
    public static final String CMDDONOTPLAY = "Do not play this song";

    public static final String NEXT_SHUFFLE_ACTION = Constant.MAIN_PACKAGE
            + "musicservicecommand.nextshuffle.repeate";
    public static final String NEXT_REPEATE_ACTION = Constant.MAIN_PACKAGE
            + "musicservicecommand.nextshuffle.repeate";
    public static final String TOGGLEPAUSE_ACTION = Constant.MAIN_PACKAGE
            + "musicservicecommand.togglepause";
    public static final String PLAY_PAUSE_ACTION = Constant.MAIN_PACKAGE
            + "musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = Constant.MAIN_PACKAGE
            + "musicservicecommand.previous";
    public static final String NEXT_ACTION = Constant.MAIN_PACKAGE
            + "musicservicecommand.next";
    public static final String PAUSE_ACTION = Constant.MAIN_PACKAGE
            + "musicservicecommand.pause";

    private static final int TRACK_ENDED = 1;
    private static final int RELEASE_WAKELOCK = 2;
    private static final int SERVER_DIED = 3;
    private static final int FOCUSCHANGE = 4;
    private static final int FADEDOWN = 5;
    private static final int FADEUP = 6;

    private PowerManager.WakeLock mWakeLock;

    private MultiPlayer mPlayer;
    private int mShuffleMode = SHUFFLE_OFF;
    private int mRepeatMode = REPEAT_NONE;
    private List<MusicObject> mListSongPlay;
    // private List<Song> mShufferList;
    private int indexCurrentSong = -1;
    private int indexRadioOriginal = 0;
    private int indexLastSong = indexCurrentSong;
    private List<MusicObject> listLastSongPlay;
    // private List<Song> listLastShufferPlay;
    private AudioManager mAudioManager;
    private int mServiceStartId = -1;
    private boolean mServiceInUse = false;
    // private boolean mHasStopMusic = false;

    private boolean isDestroy = false;
    private Random mRandom;
    private List<Integer> mListSongPlayed;
    private int mCurrentWifiSetting;
    private String WIFI_SLEEP_POLICY;
    private int WIFI_SLEEP_POLICY_NEVER;
    private long timeStartOffMusic; // Time set off music
    private long timeOff; // Time music off
    public MusicObject currentSinger;
    public MusicObject lastSongPlayed;
    private int totalCollectionSong;
    private boolean mShouldCorruptPlay = false;
    private String fromFragmentTag = "";
    private Handler mSongPreparingHandler = new Handler();
    private Runnable mSongPreparingRunnable;

    // private ImuzikWidgetProvider mAppWidgetProvider = ImuzikWidgetProvider
    // .getInstance();

    // used to track what type of audio focus loss caused the playback to pause
    private boolean mPausedByTransientLossOfFocus = false;

    private final IBinder mBinder = new ServiceStub(this);

    protected static final String LOGTAG = PlayerService.class.getName();

    private RemoteControlClientCompat mRemoteControlClientCompat;
    private ComponentName mMediaButtonReceiverComponent;

    private ArrayList<String> arrayListTag;
    private ComponentName serviceName;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            mMediaplayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0)
                    .sendToTarget();
        }
    };

    private Handler mMediaplayerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SERVER_DIED:
                    // TODO: do something here
                    break;
                case TRACK_ENDED:
                    if (!isPrepareSource)
                        next();
                    break;
                case RELEASE_WAKELOCK:
                    mWakeLock.release();
                    break;
                case FADEDOWN:
                    mPlayer.setVolume(0.2f);
                    break;
                case FADEUP:
                    mPlayer.setVolume(1.0f);
                    break;

                case FOCUSCHANGE:
                    // This code is here so we can better synchronize it with
                    // the code that
                    // handles fade-in
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            Log.e(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS");
                            if (isPlaying()) {
                                mPausedByTransientLossOfFocus = false;
                            }
                            pause();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            Log.e(LOGTAG,
                                    "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                            mMediaplayerHandler.removeMessages(FADEUP);
                            mMediaplayerHandler.sendEmptyMessage(FADEDOWN);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            Log.e(LOGTAG,
                                    "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
                            if (isPlaying()) {
                                mPausedByTransientLossOfFocus = true;
                            }
                            pause();
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            Log.e(LOGTAG, "AudioFocus: received AUDIOFOCUS_GAIN");
                            if (!isPlaying() && mPausedByTransientLossOfFocus) {
                                mPausedByTransientLossOfFocus = false;
                                play(); // also queues a fade-in
                            } else {
                                mMediaplayerHandler.removeMessages(FADEDOWN);
                                mMediaplayerHandler.sendEmptyMessage(FADEUP);
                            }
                            break;
                        default:
                            Log.e(LOGTAG, "Unknown audio focus change code");
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra(CMDNAME);
            // VegaLog.e(PlayerService.class.getName(),
            // "mIntentReceiver.onReceive " + action + " / " + cmd);
            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                next();
            } else if (CMDPREVIOUS.equals(cmd)
                    || PREVIOUS_ACTION.equals(action)) {
                prev();
            } else if (CMDTOGGLEPAUSE.equals(cmd)
                    || TOGGLEPAUSE_ACTION.equals(action)) {
                if (isPlaying()) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                } else {
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
                pause();
                mPausedByTransientLossOfFocus = false;
            } else if (CMDSTOP.equals(cmd)) {
                pause();
                mPausedByTransientLossOfFocus = false;
                seek(0);
            } else if (CMDPLAYNOW.equals(cmd)) {
                // if (playSongAsyncTask != null
                // && playSongAsyncTask.getStatus() == Status.PENDING) {
                // playSongAsyncTask.execute();
                // }
            } else if (CMDDONOTPLAY.equals(cmd)) {
                // ApplicationMusic
                // .cancelAllRequestWithTag(TAG_REQUEST_LINK_STREAMING);
                // if (playSongAsyncTask != null) {
                // playSongAsyncTask.cancel(true);
                // }
                // playSong = null;
                indexCurrentSong = indexLastSong;
                mListSongPlay = listLastSongPlay;
                // mShufferList = listLastShufferPlay;
            }
            // else if (ImuzikWidgetProvider.CMDAPPWIDGETUPDATE.equals(cmd)) {
            // int[] appWidgetIds = intent
            // .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            // mAppWidgetProvider.performUpdate(PlayerService.this,
            // appWidgetIds);
            // }
        }
    };

    private void addRequestTag(String tag) {
        if (arrayListTag == null) {
            arrayListTag = new ArrayList<String>();
        }
        arrayListTag.add(tag);
    }

    private void next() {
        // TODO: next song
        synchronized (this) {
            if (mListSongPlay == null || mPlayer == null) {
                return;
            }
            lastSongPlayed = getCurrentSong();
            if (mRepeatMode == REPEAT_CURRENT) {
                mPlayer.seek(0);
                mPlayer.start();
                // notifyStartPlaySong(null);
                notifyChange(PLAYSTATE_CHANGED);
                // Log lai thong tin nghe len server
                String url = "";
                MusicObject object = getCurrentSong();
                // if (object._type.equals("beat")) {
                // url = ClientServer.getUrlStreamBeat(object.id);
                // } else {
                // url = ClientServer.getUrlStreamRecord(object.id);
                // }
                // VegaRequest request = new VegaRequest(url, null, null);
                // MainApplication.get().getRequestQueue().add(request);
                return;
            }
            indexLastSong = indexCurrentSong;
            listLastSongPlay = mListSongPlay;
            if (mShuffleMode == SHUFFLE_OFF) {
                if (indexCurrentSong < mListSongPlay.size() - 1) {
                    indexCurrentSong += 1;
                    play(mListSongPlay.get(indexCurrentSong));
                } else if (mRepeatMode == REPEAT_ALL) {
                    indexCurrentSong = 0;
                    play(mListSongPlay.get(0));
                }
            } else {
                if (mListSongPlayed == null) {
                    mListSongPlayed = new ArrayList<Integer>();
                }
                if (mListSongPlayed.size() >= mListSongPlay.size()) {
                    // if (isNeedLoadMoreSongForPlay()) {
                    // return;
                    // } else {
                    mListSongPlayed.clear();
                    // }
                }
                if (mRandom == null) {
                    mRandom = new Random(System.currentTimeMillis());
                }
                do {
                    indexCurrentSong = mRandom.nextInt(mListSongPlay.size());
                } while (mListSongPlayed.contains(Integer
                        .valueOf(indexCurrentSong)));
                play(mListSongPlay.get(indexCurrentSong));
            }
            mListSongPlayed.add(Integer.valueOf(indexCurrentSong));
        }
    }

    // private boolean isNeedLoadMoreSongForPlay() {
    // if (currentCollectionPlay != null
    // && mListSongPlay.size() < totalCollectionSong) {
    // loadSongInRadioCollction(currentCollectionPlay,
    // mListSongPlay.size());
    // return true;
    // }
    // if (currentSinger != null
    // && currentSinger.song_count > mListSongPlay.size()) {
    // loadSongOfArtist(currentSinger, mListSongPlay.size());
    // return true;
    // }
    // return false;
    // }

    private void prev() {
        synchronized (this) {
            if (mListSongPlay == null || mPlayer == null) {
                return;
            }
            lastSongPlayed = getCurrentSong();
            if (mRepeatMode == REPEAT_CURRENT) {
                mPlayer.seek(0);
                mPlayer.start();
                // notifyStartPlaySong(null);
                notifyChange(PLAYSTATE_CHANGED);
                // Log lai thong tin nghe len server
                // String url = "";
                // MusicObject object = getCurrentSong();
                // if (object._type.equals("beat")) {
                // url = ClientServer.getUrlStreamBeat(object.id);
                // } else {
                // url = ClientServer.getUrlStreamRecord(object.id);
                // }
                // VegaRequest request = new VegaRequest(url, null, null);
                // MainApplication.get().getRequestQueue().add(request);

                return;
            }
            indexLastSong = indexCurrentSong;
            listLastSongPlay = mListSongPlay;
            if (mShuffleMode == SHUFFLE_OFF) {
                if (indexCurrentSong > 0) {
                    indexCurrentSong -= 1;
                    play(mListSongPlay.get(indexCurrentSong));
                } else {
                    if (mRepeatMode == REPEAT_ALL) {
                        indexCurrentSong = mListSongPlay.size() - 1;
                        play(mListSongPlay.get(indexCurrentSong));
                    }
                }

            } else {
                if (mListSongPlayed == null) {
                    mListSongPlayed = new ArrayList<Integer>();
                }
                if (mListSongPlayed.size() >= mListSongPlay.size()) {
                    mListSongPlayed.clear();
                }
                if (mRandom == null) {
                    mRandom = new Random(System.currentTimeMillis());
                }
                do {
                    indexCurrentSong = mRandom.nextInt(mListSongPlay.size());
                } while (mListSongPlayed.contains(Integer
                        .valueOf(indexCurrentSong)));
                play(mListSongPlay.get(indexCurrentSong));
            }
            mListSongPlayed.add(Integer.valueOf(indexCurrentSong));
        }
    }

    public boolean isPlaying() {
        synchronized (this) {
            if (mPlayer == null) {
                return false;
            }
            return mPlayer.isPlaying();
        }
    }

    private void gotoIdleState() {
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        stopForeground(true);
    }

    private void pause() {

        synchronized (this) {
            try {
                if (mPlayer != null && mPlayer.isInitialized()) {

                    mPlayer.pause();
                    // gotoIdleState();
                    notifyStartPlaySong();
//                    notifyChange(PLAYSTATE_CHANGED);
                    if (mRemoteControlClientCompat != null) {
                        mRemoteControlClientCompat
                                .setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            notifyStartPlaySong();
        }
    }

    @TargetApi(8)
    private void play() {
        synchronized (this) {
            if (Build.VERSION.SDK_INT >= 8) {
                mAudioManager
                        .requestAudioFocus(mAudioFocusListener,
                                AudioManager.STREAM_MUSIC,
                                AudioManager.AUDIOFOCUS_GAIN);
            }
            if (mPlayer != null && mPlayer.isInitialized()) {
                // if we are at the end of the song, go to the next song first
                long duration = mPlayer.duration();
                long currentPosition = mPlayer.position();
                if (mRepeatMode != REPEAT_CURRENT && duration > 0
                        && currentPosition >= (duration - 500)) {
                    if (indexCurrentSong == mListSongPlay.size() - 1) {
                        indexCurrentSong = -1;
                    }
                    next();
                } else {
                    mPlayer.start();
                }
                notifyStartPlaySong();
//                notifyChange(PLAYSTATE_CHANGED);
            }
        }
    }

    private void notifyGetSongUrlFail(String message, boolean showRegister) {
        if (isDestroy) {
            return;
        }
        if (TextUtils.isEmpty(message)) {
            return;
        }
        // Intent intent = new Intent(this, DialogForService.class);
        // intent.putExtra(DialogForService.MESSAGE, message);
        // intent.putExtra(DialogForService.SHOW_REGISTER, showRegister);
        //
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        // | Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);
    }

    @SuppressLint("NewApi")
    private void notifyStartPlaySong() {
        notifyChange(PLAYSTATE_CHANGED);
    }


    private String urlThumbShow = null;


    private void cancelNotify(String what) {
        Intent intent = new Intent(what);
        removeStickyBroadcast(intent);
    }

    private void notifyChange(String what) {

        Intent i = new Intent(what);
        if (PLAYSTATE_CHANGED.equalsIgnoreCase(what)) {
            removeStickyBroadcast(new Intent(SONG_PREPARING));
            removeStickyBroadcast(new Intent(SONG_PREPARE_FAIL));
            removeStickyBroadcast(new Intent(SONG_END));
        }

        if (SONG_PREPARING.equals(what)) {
            removeStickyBroadcast(new Intent(SONG_PREPARE_FAIL));
            removeStickyBroadcast(new Intent(PLAYSTATE_CHANGED));
        }
        if (SONG_END.equals(what)) {
            removeStickyBroadcast(new Intent(SONG_PREPARE_FAIL));
            removeStickyBroadcast(new Intent(PLAYSTATE_CHANGED));
            removeStickyBroadcast(new Intent(SONG_PREPARING));
        }

        sendStickyBroadcast(i);
        if (what.equals(QUEUE_CHANGED)) {
            // saveQueue(true);
        } else {
            // saveQueue(false);
        }

        // mAppWidgetProvider.notifyChange(this, what);
    }

    private void seek(long position) {
        synchronized (this) {
            if (mPlayer != null && mPlayer.isInitialized()) {
                mPlayer.seek(position);
            }
        }
    }

    private void openList(List<MusicObject> listSong, int position) {
        mShouldCorruptPlay = false;
        synchronized (this) {
            lastSongPlayed = getCurrentSong();
            mRemoteControlClientCompat = null;
            if (listSong == null || listSong.size() == 0 || position < 0
                    || position >= listSong.size()) {
                // Some thing wrong here
                return;
            }
            indexLastSong = -1;
            listLastSongPlay = mListSongPlay;
            // listLastShufferPlay = mShufferList;

            mListSongPlayed = new ArrayList<Integer>();
            if (this.mListSongPlay != null) {
                this.mListSongPlay.clear();

            } else {
                mListSongPlay = new ArrayList<MusicObject>();
            }
            this.mListSongPlay.addAll(listSong);
            MusicObject song = mListSongPlay.get(position);
            indexCurrentSong = position;
            // notifyChange(SONG_PREPARING);
            notifyChange(PLAYLIST_CHANGE);
            play(song);
        }
    }

    private void openListFromFragment(List<MusicObject> listSong, int position, String tag) {
        this.fromFragmentTag = tag;
        openList(listSong, position);

    }


    // private void openList(List<MusicObject> listSong, int position) {
    // openList(listSong, position, false);
    // }

    private void openSongAt(int position) {
        try {
            lastSongPlayed = getCurrentSong();
            MusicObject song = mListSongPlay.get(position);
            if (song != null) {
                play(song);
                indexLastSong = indexCurrentSong;
                indexCurrentSong = position;
                mListSongPlayed.add(Integer.valueOf(indexCurrentSong));
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // private PlaySong playSongAsyncTask;

    public static final String TAG_REQUEST_LINK_STREAMING = "request link streaming";

    private void play(final MusicObject song) {
        play(song, true);
    }

    private void play(final MusicObject song, final boolean autoPlay) {
        synchronized (PlayerService.class) {
            notifyChange(SONG_PREPARING);
            if (mSongPreparingRunnable != null) {
                mSongPreparingHandler
                        .removeCallbacks(mSongPreparingRunnable);
                mSongPreparingRunnable = null;
            }
            mSongPreparingRunnable = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
//                    prepareSourceInBg("https://archive.org/download/count_monte_cristo_0711_librivox/count_of_monte_cristo_002_dumas.mp3");
                    prepareSourceInBg(song.getMusicUrl());
                    mSongPreparingRunnable = null;

                }
            };
            mSongPreparingHandler.postDelayed(mSongPreparingRunnable, 500);

        }
    }


    private void prepareSourceInBg(final String streamUrl) {
        prepareSource(PlayerService.this, streamUrl);

    }

    private void prepareSourceInBg(final String streamUrl, boolean autoPlay) {
        prepareSource(PlayerService.this, streamUrl, autoPlay);

    }

    //
    // private void cancelPreparingSource() {
    // // if (prepareSource != null) {
    // // prepareSource.cancel(true);
    // // }
    // mShouldCorruptPlay = true;
    // Log.i("mShouldCorruptPlay", mShouldCorruptPlay + "");
    // }
    public void cancelPreparingSource() {
        mShouldCorruptPlay = true;
    }


    private String lastStreamNeedPlay = null;
    private boolean isPrepareSource = false;
    private PrepareSource prepareSource;

    private void prepareSource(PlayerService _service, String url, final boolean autoPlay) {
        WeakReference<PlayerService> serviceRfc;
        serviceRfc = new WeakReference<PlayerService>(_service);
        final PlayerService service = serviceRfc.get();
        service.isPrepareSource = true;
        if (service == null || service.isDestroy) {
            return;
        }
        // String songUrl = params[0];
        service.mPlayer.release();
        Log.e("PlayerService", "Mediaplayer release");
        service.mPlayer = service.new MultiPlayer();
        service.mPlayer.setDataSourceAsync(url, new OnPreparedListener() {

            @Override
            public void onPrepared() {
                // TODO Auto-generated method stub
                if (service.mPlayer.isInitialized()) {

                    if (autoPlay)
                        PlayerUtil.play();

                    isPrepareSource = false;
                }
                service.notifyChange(SETUP_SONG);
            }
        });
    }

    private void prepareSource(PlayerService _service, String url) {
        prepareSource(_service, url, true);
    }

    private static class PrepareSource extends AsyncTask<String, Void, Void> {
        private WeakReference<PlayerService> serviceRfc;

        public PrepareSource(PlayerService service) {
            serviceRfc = new WeakReference<PlayerService>(service);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Log.e("responseStream", "onPreExecute" + " prepare source");
        }

        @Override
        protected Void doInBackground(String... params) {
            PlayerService service = serviceRfc.get();
            service.isPrepareSource = true;
            if (service == null || service.isDestroy || isCancelled()) {
                return null;
            }
            String songUrl = params[0];
            service.mPlayer.release();
            service.mPlayer = service.new MultiPlayer();
            service.mPlayer.setDataSource(songUrl);
            if (service.mPlayer.isInitialized()) {
                if (TextUtils.isEmpty(service.lastStreamNeedPlay)) {
                    // Log.e("responseStream", "onEndBackground1"
                    // + " prepare source");
                    service.play();
                }
                if (service.mShouldCorruptPlay == true) {
                    service.pause();
                }
            }
            // Log.e("responseStream", "onEndBackground2" + " prepare source");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Log.e("responseStream", "onPostExecute" + " prepare source");
            PlayerService service = serviceRfc.get();
            service.isPrepareSource = false;
            service.mShouldCorruptPlay = false;
            if (!TextUtils.isEmpty(service.lastStreamNeedPlay)) {
                // Log.e("responseStream", "onPostExecute"
                // + "lastStreamNeedPlay != null ");
                service.mShouldCorruptPlay = true;
                service.prepareSourceInBg(service.lastStreamNeedPlay);
                service.lastStreamNeedPlay = null;
            }

            service.notifyChange(SETUP_SONG);
        }
    }

    private long getDuration() {
        synchronized (this) {
            try {
                if (mPlayer.isInitialized()) {
                    return mPlayer.duration();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            return 0;
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
                .getClass().getName());
        mWakeLock.setReferenceCounted(false);
        serviceName = new ComponentName(getApplicationContext(),
                PlayerService.class);
        mPlayer = new MultiPlayer();
        mPlayer.setHandler(mMediaplayerHandler);

        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(TOGGLEPAUSE_ACTION);
        commandFilter.addAction(PAUSE_ACTION);
        commandFilter.addAction(NEXT_ACTION);
        commandFilter.addAction(PREVIOUS_ACTION);
        commandFilter.addAction(CMDDONOTPLAY);
        commandFilter.addAction(CMDPLAYNOW);
        commandFilter.addAction(NEXT_SHUFFLE_ACTION);
        commandFilter.addAction(NEXT_REPEATE_ACTION);
        registerReceiver(mIntentReceiver, commandFilter);

        if (Build.VERSION.SDK_INT >= 17) {
            WIFI_SLEEP_POLICY = Settings.Global.WIFI_SLEEP_POLICY;
            WIFI_SLEEP_POLICY_NEVER = Settings.Global.WIFI_SLEEP_POLICY_NEVER;
        } else {
            WIFI_SLEEP_POLICY = Settings.System.WIFI_SLEEP_POLICY;
            WIFI_SLEEP_POLICY_NEVER = Settings.System.WIFI_SLEEP_POLICY_NEVER;
        }
        try {
            mCurrentWifiSetting = Settings.System.getInt(
                    getContentResolver(), WIFI_SLEEP_POLICY);
        } catch (Settings.SettingNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            mCurrentWifiSetting = -1;
        }
        if (!Settings.System.putInt(getContentResolver(),
                WIFI_SLEEP_POLICY, WIFI_SLEEP_POLICY_NEVER)) {
            mCurrentWifiSetting = -1;
        }
        // mRepeatMode = SharePreperentUtil.getIntValue(PLAYER_REPEATE_MODE,
        // this,
        // REPEAT_NONE);
        // mShuffleMode = SharePreperentUtil.getIntValue(PLAYER_SHUFFLE_MODE,
        // this, SHUFFLE_OFF);
        mMediaButtonReceiverComponent = new ComponentName(this,
                MediaRemoteReceiver.class);
        registerOtherIntenlistener();

    }

    private void registerOtherIntenlistener() {
        // IntentFilter filter = new IntentFilter(
        // Constant.INTENT_FILTER_LIKE_SONG_CHANGE);
        // registerReceiver(otherListener, filter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(otherListener, filter);


    }

    private BroadcastReceiver otherListener = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.d("Receiver", "LockscreenIntentReceiver: ACTION_SCREEN_OFF");
                    notifyStartPlaySong();
//                disableNotification();
                } else if (action.equals(Intent.ACTION_SCREEN_ON)) {

                } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
                    Log.d("Receiver", "LockscreenIntentReceiver: ACTION_USER_PRESENT");

                } else if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            if (isPlaying()) {
                                pause();
                            }
                            break;
                        case 1:
                            if (playerIsInit() && !isPlaying()) {
                                play();
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e("Receiver", "LockscreenIntentReceiver exception: " + e.toString());
            }
        }
    };

    /**
     * Provides a unified interface for dealing with midi files and other media
     * files.
     */
    private class MultiPlayer {
        private AudioPlayer mMediaPlayer = new AudioPlayer(PlayerService.this);
//        private AudioPlayer mMediaPlayer = new AudioPlayer(new NativeAudioPlayer(PlayerService.this));

        private Handler mHandler;

        private boolean mIsInitialized = false;

        public MultiPlayer() {
            mMediaPlayer.setWakeMode(PlayerService.this,
                    PowerManager.PARTIAL_WAKE_LOCK);
        }

        public void setDataSource(String path) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
//                    mMediaPlayer.setDataSource(PlayerService.this,
//                            Uri.parse(path));
                    mMediaPlayer.setDataSource(Uri.parse(path));
                } else {
//                    mMediaPlayer.setDataSource(path);
                    mMediaPlayer.setDataSource(Uri.parse(path));
                }
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepareAsync();

            }
//            catch (IOException ex) {
//                // TODO: notify the user why the file couldn't be opened
//                ex.printStackTrace();
//                cancelNotify(SONG_PREPARING);
//                mIsInitialized = false;
//                return;
//            }
            catch (IllegalArgumentException ex) {
                // TODO: notify the user why the file couldn't be opened
                ex.printStackTrace();
                cancelNotify(SONG_PREPARING);
                mIsInitialized = false;
                return;
            } catch (Exception e) {
                // TODO: handle exception
                cancelNotify(SONG_PREPARING);
                mIsInitialized = false;
                return;
            }
            mMediaPlayer.setOnCompletionListener(completeListener);
            mMediaPlayer.setOnErrorListener(errorListener);
            mIsInitialized = true;
        }

        public void setDataSourceAsync(String path, OnPreparedListener listener) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setOnPreparedListener(null);
//                path = "http://video.webmfiles.org/big-buck-bunny_trailer.webm";
//                path = "https://r20---sn-i3b7knes.googlevideo.com/videoplayback?pl=20&ei=VpzAWY7JMZDi8wSfpojoCQ&clen=1584657&itag=249&gir=yes&mime=audio%2Fwebm&sparams=clen,dur,ei,expire,gir,id,ip,ipbits,ipbypass,itag,keepalive,lmt,mime,mip,mm,mn,ms,mv,pl,requiressl,source&ipbits=0&requiressl=yes&keepalive=yes&id=o-AJHeUYehjagc2tYm0EVLCvM82JaUEQH_CqmD_Jnw1Qba&dur=246.601&lmt=1449644086350260&ip=158.69.194.209&key=cms1&expire=1505816758&source=youtube&signature=3C75B07AA4860232C57472A1C72AC82071D10CEE.0F97B2C6A8CEFCBE66115335AC42DA322CBCEA04&ratebypass=yes&req_id=d9e7d6b1e3f2a3ee&redirect_counter=2&fexp=23702512&cms_redirect=yes&ipbypass=yes&mip=116.107.2.71&mm=30&mn=sn-i3b7knes&ms=nxu&mt=1505795244&mv=m";
                if (path.startsWith("content://")) {
                    mMediaPlayer.setDataSource(Uri.parse(path));
                } else {
                    mMediaPlayer.setDataSource(Uri.parse(path));
                }
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(listener);

            }
//            catch (IOException ex) {
//                // TODO: notify the user why the file couldn't be opened
//                ex.printStackTrace();
//                cancelNotify(SONG_PREPARING);
//                mIsInitialized = false;
//                return;
//            }
            catch (IllegalArgumentException ex) {
                // TODO: notify the user why the file couldn't be opened
                ex.printStackTrace();
                cancelNotify(SONG_PREPARING);
                mIsInitialized = false;
                return;
            } catch (Exception e) {
                // TODO: handle exception
                cancelNotify(SONG_PREPARING);
                mIsInitialized = false;
                return;
            }
            mMediaPlayer.setOnCompletionListener(completeListener);
            mMediaPlayer.setOnErrorListener(errorListener);
            mIsInitialized = true;
        }

        public boolean isInitialized() {
            return mIsInitialized;

        }

        public boolean isPlaying() {
            if (isInitialized()) {
                return mMediaPlayer.isPlaying();
            }
            return false;
        }

        public void start() {
//			Log.i("Player Service", PlayerService.class.getName()
//					+ " MultiPlayer.start called");
            try {
                mMediaPlayer.start();
                // notifyStartPlaySong(null);
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("exception", e.getMessage());
            }
        }

        public void stop() {
            try {
                mMediaPlayer.reset();
                cancelNotify(SONG_PREPARING);

            } catch (Exception e) {
                // TODO: handle exception
            }

            mIsInitialized = false;
            stopForeground(true);
        }

        /**
         * You CANNOT use this player anymore after calling release()
         */
        public void release() {
            stop();
            mMediaPlayer.release();
            // mAudioManager.abandonAudioFocus(mAudioFocusListener);
        }

        public void pause() {
            try {
                mMediaPlayer.pause();
                // MainApplication.get()
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        OnCompletionListener completeListener = new OnCompletionListener() {
            public void onCompletion() {
                // Acquire a temporary wakelock, since when we return from
                // this callback the MediaPlayer will release its wakelock
                // and allow the device to go to sleep.
                // This temporary wakelock is released when the RELEASE_WAKELOCK
                // message is processed, but just in case, put a timeout on it.
                mWakeLock.acquire(30000);
                // notifyChange(SONG_END);
                // if (mHandler != null) {
                if (mShouldCorruptPlay == false)
                    mMediaplayerHandler.sendEmptyMessage(TRACK_ENDED);

                mMediaplayerHandler.sendEmptyMessage(RELEASE_WAKELOCK);
                mAudioManager.abandonAudioFocus(mAudioFocusListener);
                notifyChange(SONG_END);
                gotoIdleState();
                // }
//				Log.e("PlayerService", "Song end :" + getCurrentSong().name  + " - preparing source  = "  + isPrepareSource);
            }
        };
        OnErrorListener errorListener = new OnErrorListener() {
            @Override
            public boolean onError() {
                return false;
            }

//            public boolean onError(Exception e) {
////				Log.i("PLAYERSERVICE", "ON ERROR");
//                if (e instanceof NativeMediaPlaybackException)
////                try {
//                    switch (((NativeMediaPlaybackException) e).what) {
//                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//                            mIsInitialized = false;
//                            mMediaPlayer.release();
//                            mMediaPlayer = new AudioPlayer(PlayerService.this);
//                            mMediaPlayer.setWakeMode(PlayerService.this,
//                                    PowerManager.PARTIAL_WAKE_LOCK);
//                            mHandler.sendMessageDelayed(
//                                    mHandler.obtainMessage(SERVER_DIED), 2000);
//                            return true;
//                        default:
//                            Log.d("MultiPlayer", "Error: " + ((NativeMediaPlaybackException) e).what + "," + ((NativeMediaPlaybackException) e).extra);
//                            break;
//                    }
////                }
////                catch (Exception e) {
////                    // TODO: handle exception
////                }
//
//                return false;
//            }
        };

        public long duration() {
            return mMediaPlayer.getDuration();
        }

        public long position() {
            return mMediaPlayer.getCurrentPosition();
        }

        public long seek(long whereto) {
            try {
                mMediaPlayer.seekTo((int) whereto);
                return whereto;
            } catch (Exception e) {
                // TODO: handle exception
                return 0;
            }
        }

        public void setVolume(float vol) {
            try {
                mMediaPlayer.setVolume(vol, vol);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        // VegaLog.e(PlayerService.class.getName() + " on bind");
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceInUse = true;
        mAudioManager.registerMediaButtonEventReceiver(new ComponentName(this,
                MediaRemoteReceiver.class));
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceInUse = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mServiceInUse = false;
        if (isPlaying() || mPausedByTransientLossOfFocus) {
            // something is currently playing, or will be playing once
            // an in-progress action requesting audio focus ends, so don't stop
            // the service now.
            return true;
        }
        // If there is a playlist but playback is paused, then wait a while
        // before stopping the service, so that pause/resume isn't slow.

        // Also delay stopping the service if we're transitioning between
        // tracks.
        if (mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
            Message msg = mDelayedStopHandler.obtainMessage();
            mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
            return true;
        }

        // No active playlist, OK to stop the service right now
        stopSelf(mServiceStartId);
        return true;
    }

    static class ServiceStub extends IMediaPlaybackService.Stub {
        WeakReference<PlayerService> mService;

        public ServiceStub(PlayerService service) {
            mService = new WeakReference<PlayerService>(service);
        }

        @Override
        public void openList(List<MusicObject> listSong, int position)
                throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().openList(listSong, position);
            }

        }

        @Override
        public void openListFromFragment(List<MusicObject> listSong, int position, String fromFragment) throws RemoteException {
            if (mService.get() != null) {
                mService.get().openListFromFragment(listSong, position, fromFragment);
            }
        }

        @Override
        public String getTagFragment() throws RemoteException {
            if (mService.get() != null) {
                return mService.get().fromFragmentTag();
            }
            return "";
        }


        @Override
        public void openSongAt(int position) {
            if (mService.get() != null) {
                mService.get().openSongAt(position);
            }
        }


        @Override
        public long getDuration() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().getDuration();
            } else {
                return 0;
            }

        }

        @Override
        public long getPosition() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().getPosition();
            } else {
                return 0;
            }
        }

        @Override
        public MusicObject getCurrentSong() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().getCurrentSong();
            } else {
                return null;
            }
        }

        @Override
        public List<MusicObject> getCurrentListPlay() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().getCurrentListPlay();
            } else {
                return null;
            }
        }

        @Override
        public void setShufferMode(int shufferMode) throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().setShufferMode(shufferMode);
            }
        }

        @Override
        public void setRepeateMode(int repeateMode) throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().setRepeateMode(repeateMode);
            }
        }

        @Override
        public int getShufferMode() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().getShufferMode();
            } else {
                return SHUFFLE_OFF;
            }
        }

        @Override
        public int getRepeateMode() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().getRepeateMode();
            } else {
                return REPEAT_NONE;
            }
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().isPlaying();
            } else {
                return false;
            }
        }

        @Override
        public void stop() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().stop();
            }
        }

        @Override
        public void pause() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().pause();
            }
        }

        @Override
        public void play() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().play();
            }
        }

        @Override
        public void prev() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().prev();
            }
        }

        @Override
        public void next() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().next();
            }
        }

        @Override
        public void seek(long pos) throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().seek(pos);
            }
        }

        @Override
        public void addToPlaylist(MusicObject songAdd, int position)
                throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().addToPlaylist(songAdd, position);
            }
        }

        @Override
        public void removeFromPlayList(int position) throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().removeFromPlayList(position);
            }
        }

        @Override
        public void setListSong(List<MusicObject> listSong)
                throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().setListSong(listSong);
            }
        }

        @Override
        public int getIndexCurrentSong() {
            if (mService.get() != null) {
                return mService.get().getIndexCurrentSong();
            }
            return -1;
        }

        @Override
        public boolean insertToNowPlaying(MusicObject song)
                throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().insertToNowPlaying(song);
            }
            return false;
        }

        @Override
        public boolean removeFromNowPlaying(int position) throws RemoteException {
            if (mService.get() != null) {
                return mService.get().removeFromNowPlaying(position);
            }
            return false;
        }

        @Override
        public boolean playerIsInit() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().playerIsInit();
            }
            return false;
        }

        @Override
        public boolean insertListSongToNowPlaying(List<MusicObject> listSong)
                throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().insertListSongToNowPlaying(listSong);
            }
            return false;
        }

        @Override
        public void setTimeOffMusic(int time) throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                mService.get().setTimeOffMusic(time);
            }
        }

        @Override
        public int getTimeBeforeMusicOff() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService.get() != null) {
                return mService.get().getTimeBeforeMusicOff();
            }
            return 0;
        }


        @Override
        public int setAndGetNextRepeatMode() throws RemoteException {
            if (mService != null) {
                return mService.get().setAndGetNextRepeatMode();
            }
            return -1;
        }

        @Override
        public int setAndGetNextShufferMode() throws RemoteException {
            if (mService != null) {
                return mService.get().setAndGetNextShufferMode();
            }
            return -1;
        }

        @Override
        public void setInterruptPlay() throws RemoteException {
            // TODO Auto-generated method stub
            if (mService != null) {
                mService.get().mShouldCorruptPlay = true;
            }
        }


        @Override
        public MusicObject getLastSong() throws RemoteException {
            if (mService != null) {
                return mService.get().getLastSong();
            }
            return null;
        }

        @Override
        public boolean canNext() throws RemoteException {
            if (mService != null) {
                return mService.get().canNext();
            }
            return true;
        }

        @Override
        public boolean canBack() throws RemoteException {
            if (mService != null) {
                return mService.get().canBack();
            }
            return true;
        }

        @Override
        public void swap(int from, int to) throws RemoteException {
            if (mService != null) {
                mService.get().swap(from, to);
            }
        }


        // @Override
        // public void openArtistSongs(String artistId) throws RemoteException {
        // if (mService.get() != null) {
        // mService.get().openArtistSongs(artistId);
        // }
        // }

    }

    private boolean removeFromNowPlaying(int position) {
        // TODO Auto-generated method stub
        synchronized (this) {
            try {
                if (mListSongPlay == null) {
                    return false;
                } else {
//                        next();
//                    if (indexCurrentSong < mListSongPlay.size() - 1) {
//                        indexCurrentSong += 1;
//                        play(mListSongPlay.get(indexCurrentSong));
//                    } else {
//                        stop();
//                    }
//                    mListSongPlay.remove(position);
//                    if(position < indexCurrentSong){
//
//                    }

                    if (position == indexCurrentSong) {
//                        next();
                        if (indexCurrentSong < mListSongPlay.size() - 1) {
//                            indexCurrentSong += 1;
//                            play(mListSongPlay.get(indexCurrentSong));
                            openSongAt(indexCurrentSong + 1);
                        } else {
                            stop();
                        }
                    }
                    mListSongPlay.remove(position);
                    if (position < indexCurrentSong) {
                        indexCurrentSong--;

                    }
//                    mListSongPlay.remove(position);

//                    if(indexLastSong)
                    if (mListSongPlay.size() == 0) {
                        stop();
                    }

                }
                notifyChange(PLAYLIST_CHANGE);
                return true;
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
    }

    private void swap(int from, int to) {
        synchronized (this) {
            try {
                if (mListSongPlay != null) {
                    Collections.swap(mListSongPlay, from, to);
                    if (indexCurrentSong == from) {
                        indexCurrentSong = to;
                    } else if (indexCurrentSong == to) {
                        indexCurrentSong = from;
                    }
//                    indexCurrentSong = mListSongPlay.indexOf(getCurrentSong());
//                    Log.e("PlayerService" , indexCurrentSong + "");
//                    notifyChange(PLAYLIST_CHANGE);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private String fromFragmentTag() {
        return this.fromFragmentTag;
    }

    public int setAndGetNextRepeatMode() {
        if (mRepeatMode == REPEAT_ALL) {
            setRepeateMode(REPEAT_CURRENT);
        } else if (mRepeatMode == REPEAT_CURRENT) {
            setRepeateMode(REPEAT_NONE);
        } else if (mRepeatMode == REPEAT_NONE) {
            setRepeateMode(REPEAT_ALL);
        }
        return mRepeatMode;
    }


    public boolean canBack() {
        if (mShuffleMode == SHUFFLE_OFF && mRepeatMode == REPEAT_NONE
                && indexCurrentSong == 0) {
            return false;
        }
        return true;
    }

    public boolean canNext() {
        if (mShuffleMode == SHUFFLE_OFF && mRepeatMode == REPEAT_NONE
                && indexCurrentSong == mListSongPlay.size() - 1) {
            return false;
        }
        return true;
    }

    public MusicObject getLastSong() {
        synchronized (this) {
            return lastSongPlayed;
        }
    }

    public int setAndGetNextShufferMode() {
        if (mShuffleMode == SHUFFLE_OFF) {
            setShufferMode(SHUFFLE_ON);
        } else if (mShuffleMode == SHUFFLE_ON) {
            setShufferMode(SHUFFLE_OFF);
        }
        return mShuffleMode;
    }

    public boolean playerIsInit() {
        // TODO Auto-generated method stub
        synchronized (this) {
            if (mPlayer != null && mPlayer.isInitialized()) {
                return true;
            }
            return false;
        }
    }

    public int getTimeBeforeMusicOff() {
        // TODO Auto-generated method stub
        if (timeOff > 0) {
            return (int) (timeOff - (System.currentTimeMillis() - timeStartOffMusic) / 1000);
        } else {
            return 0;
        }
    }

    public void setTimeOffMusic(int time) {
        // // TODO Auto-generated method stub
        // timeStartOffMusic = System.currentTimeMillis();
        // timeOff = time;
        // if (timeOffHandler == null) {
        // timeOffHandler = new TimeOffHandler(this);
        // }
        // timeOffHandler.removeMessages(OFF_MUSIC);
        // if (time > 0) {
        // timeOffHandler.sendEmptyMessageDelayed(OFF_MUSIC, time * 1000);
        // }
    }

    private static final int OFF_MUSIC = 980;
    private TimeOffHandler timeOffHandler;

    static class TimeOffHandler extends Handler {
        // WeakReference<PlayerService> playerServiceRfc;
        //
        // public TimeOffHandler(PlayerService playerService) {
        // playerServiceRfc = new WeakReference<PlayerService>(playerService);
        // }
        //
        // @Override
        // public void handleMessage(Message msg) {
        // // TODO Auto-generated method stub
        // PlayerService playerService = playerServiceRfc.get();
        // if (playerService == null || playerService.isDestroy) {
        // return;
        // }
        // switch (msg.what) {
        // case OFF_MUSIC:
        // try {
        // Util.trackEvent(playerService, Const.setTimeOff,
        // "Time set:" + playerService.timeOff + "(s)",
        // (long) 1);
        // playerService.stop();
        // JsonBase.clearServerSessionInvalidListener();
        // DownloadUtil.stopDownload();
        // ContentDownloader.finishSession();
        // playerService.sendStickyBroadcast(new Intent(
        // ActivityBase.LOGOUT));
        // playerService.stopSelf();
        // } catch (Exception e) {
        // // TODO: handle exception
        // }
        // break;
        //
        // default:
        // break;
        // }
        // }

    }

    public void setListSong(List<MusicObject> listSong) {
        // TODO Auto-generated method stub

    }

    public boolean insertToNowPlaying(MusicObject song) {
        // TODO Auto-generated method stub
        synchronized (this) {
            try {
                if (mListSongPlay == null) {
                    List<MusicObject> listSong = new ArrayList<MusicObject>();
                    listSong.add(song);
                    if (mShuffleMode == SHUFFLE_ON) {

                    }
                    openList(listSong, 0);
                } else {
                    mListSongPlay.add(indexCurrentSong + 1, song);
                }
                notifyChange(PLAYLIST_CHANGE);
                return true;
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
    }

    public boolean insertListSongToNowPlaying(List<MusicObject> listSong) {

        synchronized (this) {
            try {
                if (mListSongPlay == null) {
                    openList(listSong, 0);
                } else {
                    mListSongPlay.addAll(indexCurrentSong + 1, listSong);
                }
                return true;
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
    }

    private int getIndexCurrentSong() {
        synchronized (this) {
            return indexCurrentSong;
        }
    }


    public long getPosition() {
        // TODO Auto-generated method stub
        synchronized (this) {
            try {
                if (mPlayer.isInitialized()) {
                    return mPlayer.position();
                } else {
                    return 0;
                }
            } catch (Exception e) {
                // TODO: handle exception
                return 0;
            }
        }
    }

    public MusicObject getCurrentSong() {
        // TODO Auto-generated method stub
        synchronized (this) {
            if (mListSongPlay != null && indexCurrentSong != -1
                    && indexCurrentSong < mListSongPlay.size()
                    && indexCurrentSong >= 0) {
                return mListSongPlay.get(indexCurrentSong);
            }
            return null;
        }
    }

    public List<MusicObject> getCurrentListPlay() {
        // TODO Auto-generated method stub
        return mListSongPlay;
    }

    public void setShufferMode(int shufferMode) {
        // TODO Auto-generated method stub
    }

    public void setRepeateMode(int repeateMode) {
        // TODO Auto-generated method stub
    }

    public int getShufferMode() {
        // TODO Auto-generated method stub
        return mShuffleMode;
    }

    public int getRepeateMode() {
        // TODO Auto-generated method stub
        return mRepeatMode;
    }

    public void stop() {
        // TODO Auto-generated method stub
        // mShouldCorruptPlay = true;
        // Log.i("responseStream", "setCorrupt");
        synchronized (this) {
            if (mPlayer != null && mPlayer.isInitialized()) {
                mPlayer.stop();
                notifyChange(PLAYSTATE_CHANGED);
                if (mRemoteControlClientCompat != null) {
                    mRemoteControlClientCompat
                            .setPlaybackState(RemoteControlClient.PLAYSTATE_BUFFERING);
                }

            } else {
                // mHasStopMusic = true;
            }
            // MainApplication.get().getRequestQueue()
            // .cancelAll(Constant.TAG_REQUEST_STREAM_SONG);
            // cancelPreparingSource();
        }
    }

    public void addToPlaylist(MusicObject songAdd, int position) {
        // TODO Auto-generated method stub

    }

    public void removeFromPlayList(int position) {
        // TODO Auto-generated method stub

    }

    // interval after which we stop the service when idle
    private static final int IDLE_DELAY = 120000;

    private Handler mDelayedStopHandler = new PlayerServiceHandler(this);

    private static class PlayerServiceHandler extends Handler {
        private WeakReference<PlayerService> playerServiceReference;

        public PlayerServiceHandler(PlayerService playerService) {
            playerServiceReference = new WeakReference<PlayerService>(
                    playerService);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayerService playerService = playerServiceReference.get();
            if (playerService == null || playerService.isDestroy) {
                removeCallbacksAndMessages(null);
                return;
            }
            // Check again to make sure nothing is playing right now
            if (playerService.isPlaying()
                    || playerService.mPausedByTransientLossOfFocus
                    || playerService.mServiceInUse
                    || playerService.mMediaplayerHandler
                    .hasMessages(TRACK_ENDED)) {
                return;
            }
            // save the queue again, because it might have changed
            // since the user exited the music app (because of
            // party-shuffle or because the play-position changed)
            // saveQueue(true);
            playerService.stopSelf(playerService.mServiceStartId);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        // VegaLog.e(PlayerService.class.getName() + " on onstart command");
        mServiceStartId = startId;

        if (intent != null) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            // VegaLog.e("onStartCommand " + action + " / " + cmd);

            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                next();
            } else if (CMDPREVIOUS.equals(cmd)
                    || PREVIOUS_ACTION.equals(action)) {
                prev();
            } else if (CMDTOGGLEPAUSE.equals(cmd)
                    || TOGGLEPAUSE_ACTION.equals(action)) {
                if (isPlaying()) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                } else {
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
                pause();
                mPausedByTransientLossOfFocus = false;
            } else if (CMDSTOP.equals(cmd)) {
                pause();
                mPausedByTransientLossOfFocus = false;
                seek(0);
            } else if (NEXT_REPEATE_ACTION.equals(action)) {
                setAndGetNextRepeatMode();
            } else if (NEXT_SHUFFLE_ACTION.equals(action)) {
                setAndGetNextShufferMode();
            }
        }

        // make sure the service will shut down on its own if it was
        // just started but not bound to and nothing is playing
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (prepareSource != null
                && prepareSource.getStatus() == AsyncTask.Status.RUNNING) {
            prepareSource.cancel(true);
        }
        lastStreamNeedPlay = null;
        if (mRemoteControlClientCompat != null) {
            mRemoteControlClientCompat
                    .setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
        }
        mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(
                PlayerService.this, MediaRemoteReceiver.class));
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        cancelNotify(PLAYSTATE_CHANGED);
        cancelNotify(SONG_PREPARING);
        cancelNotify(PLAY_RADIO);
        if (mCurrentWifiSetting != -1) {
            Settings.System.putInt(getContentResolver(),
                    WIFI_SLEEP_POLICY, mCurrentWifiSetting);
        }
        // ApplicationMusic.cancelAllRequestWithTag(TAG_REQUEST_LINK_STREAMING);
        isDestroy = true;
        stopForeground(true);
        unregisterReceiver(mIntentReceiver);
        if (mMediaplayerHandler != null) {
            mMediaplayerHandler.removeCallbacksAndMessages(null);
            mDelayedStopHandler.removeCallbacksAndMessages(null);
        }
        if (timeOffHandler != null) {
            timeOffHandler.removeMessages(OFF_MUSIC);
            timeOffHandler.removeCallbacksAndMessages(null);
        }
        if (mPlayer != null) {
            try {
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        mWakeLock.release();
        try {
            unregisterReceiver(otherListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int randInt(int min, int max) {
        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min)) + min;

        return randomNum;
    }

}