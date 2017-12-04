package com.bluesky.exoplayerexample.PlayerService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.bluesky.exoplayerexample.Model.MusicObject;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class PlayerUtil {

    private Context mContext;

    public PlayerUtil(Context context) {
        mContext = context;
    }

    public static boolean checkStringIsemptyOrNull(String str) {
        if (str == null) {
            return true;
        }
        if (str.trim().equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    public static void goToHome(Context ctx) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(homeIntent);
    }

    public static IMediaPlaybackService sService = null;

    private static HashMap<Context, ServiceBinder> sConnectionMap = new HashMap<Context, ServiceBinder>();

    public static class ServiceToken {
        ContextWrapper mWrappedContext;

        ServiceToken(ContextWrapper context) {
            mWrappedContext = context;
        }
    }

    public static ServiceToken bindToService(Activity context) {
        return bindToService(context, null);
    }

    public static ServiceToken bindToService(Activity context,
                                             ServiceConnection callback) {
        Activity realActivity = context.getParent();
        if (realActivity == null) {
            realActivity = context;
        }
        ContextWrapper cw = new ContextWrapper(realActivity);
//        cw.startService(new Intent(cw, PlayerService.class));
        ServiceBinder sb = new ServiceBinder(callback);
        if (cw.bindService((new Intent()).setClass(cw, PlayerService.class),
                sb, Context.BIND_AUTO_CREATE)) {
            sConnectionMap.put(cw, sb);
            return new ServiceToken(cw);
        }
        Log.e("Music", "Failed to bind to service");
        return null;
    }

    public static void unbindFromService(ServiceToken token) {
        if (token == null) {
            Log.e("MusicUtils", "Trying to unbind with null token");
            return;
        }
        ContextWrapper cw = token.mWrappedContext;
        ServiceBinder sb = sConnectionMap.remove(cw);
        if (sb == null) {
            Log.e("MusicUtils", "Trying to unbind for unknown Context");
            return;
        }
        cw.unbindService(sb);
        if (sConnectionMap.isEmpty()) {
            // presumably there is nobody interested in the service at this
            // point,
            // so don't hang on to the ServiceConnection
            sService = null;
        }
    }

    private static class ServiceBinder implements ServiceConnection {
        ServiceConnection mCallback;

        ServiceBinder(ServiceConnection callback) {
            mCallback = callback;
        }

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // Log.e(TAG, "before set sService");
            sService = IMediaPlaybackService.Stub.asInterface(service);
            if (mCallback != null) {
                mCallback.onServiceConnected(className, service);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }
            sService = null;
        }
    }

    public static void openListSong(final List<MusicObject> listSong,
                                    final int position, Activity act) {
        openListSong(listSong, position, act, "");
    }

    public static void openListSong(final List<MusicObject> listSong,
                                    final int position, Activity act, final String tag) {
        if (sService == null) {
            ServiceConnection connection = new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                    // TODO Auto-generated method stub
                    // openListSong(listSong, position);
                    if (sService != null) {
                        try {
                            if (TextUtils.isEmpty(tag))
                                sService.openList(listSong, position);
                            else
                                sService.openListFromFragment(listSong, position,tag);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        // try to play music with service had stoped
                    }
                }

            };
            bindToService(act, connection);

        } else {
            // openListSong(listSong, position);
            if (sService != null) {
                try {
                    if (TextUtils.isEmpty(tag))
                        sService.openList(listSong, position);
                    else
                        sService.openListFromFragment(listSong, position,tag);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                // try to play music with service had stoped
            }
        }
    }


    public static MusicObject getCurrentSongPlay() {
        if (sService != null) {
            try {
                return sService.getCurrentSong();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static MusicObject getLastSongPlay() {
        if (sService != null) {
            try {
                return sService.getLastSong();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int getCurrentTime() {
        if (sService != null) {
            try {
                return (int) (sService.getPosition());
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static int getTotalTime() {
        if (sService != null) {
            try {
                return (int) (sService.getDuration());
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }
    public static String getFragmentTag(){
        if (sService != null) {
            try {
                return sService.getTagFragment();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }

    // public static int getRepeateMode() {
    // return PlayerService.REPEAT_NONE;
    // }
    //
    // public static int getShufferMode() {
    // return PlayerService.SHUFFLE_OFF;
    // }

    public static boolean isPlaying() {
        if (sService == null) {
            return false;
        }
        try {
            return sService.isPlaying();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static void setInterrupt() {
        if (sService != null) {
            try {
                sService.setInterruptPlay();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public static boolean playerIsInit() {
        if (sService != null) {
            try {
                return sService.playerIsInit();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return false;
    }

    public static void next() {
        // TODO Auto-generated method stub
        if (sService != null) {
            try {
                sService.next();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void back() {
        if (sService != null) {
            try {
                sService.prev();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void pause() {
        // TODO Auto-generated method stub
        if (sService != null) {
            try {
                sService.pause();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void play() {
        if (sService != null) {
            try {
                sService.play();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static boolean canNext() {
        if (sService != null) {
            try {
                return sService.canNext();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean canBack() {
        if (sService != null) {
            try {
                return sService.canBack();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return true;
    }


    public static void setTimeOffMusic(int seconds) {
        if (sService != null) {
            try {
                sService.setTimeOffMusic(seconds);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    public static void stop() {
        if (sService != null) {
            try {
                sService.stop();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void seek(long time) {
        if (sService != null) {
            try {
                sService.seek(time);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static List<MusicObject> getListSongPlay() {
        if (sService != null) {
            try {
                return sService.getCurrentListPlay();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setShuffleMode(int shuffleMode) {
        if (sService != null) {
            try {
                sService.setShufferMode(shuffleMode);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void setRepeateMode(int setRepeateMode) {
        if (sService != null) {
            try {
                sService.setRepeateMode(setRepeateMode);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static int setAndGetNextRepeatMode() {
        if (sService != null) {
            try {
                return sService.setAndGetNextRepeatMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return PlayerService.REPEAT_ALL;
    }

    public static int setAndGetNextShufferMode() {
        if (sService != null) {
            try {
                return sService.setAndGetNextShufferMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return PlayerService.SHUFFLE_OFF;
    }

    public static int getCurrentRepeatMode() {
        if (sService != null) {
            try {
                return sService.getRepeateMode();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return PlayerService.REPEAT_ALL;
    }

    public static int getCurrentShufferMode() {
        if (sService != null) {
            try {
                return sService.getShufferMode();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return PlayerService.SHUFFLE_OFF;
    }

    public static void openSongAtPosition(int position) {
        if (sService != null) {
            try {
                sService.openSongAt(position);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static int getIndexCurrentSongPlay() {
        if (sService != null) {
            try {
                return sService.getIndexCurrentSong();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return 0;
    }


    public static void setTimeOff(int time) {
        if (sService != null) {
            try {
                sService.setTimeOffMusic(time);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static int getTimeOff() {
        if (sService != null) {
            try {
                return sService.getTimeBeforeMusicOff();
            } catch (Exception e) {
                // TODO: handle exception
                return 0;
            }
        }
        return 0;
    }

    public static boolean insertSongToNowPlaying(final MusicObject song,
                                                 Activity act) {
        if (sService == null) {
            ServiceConnection connection = new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                    // TODO Auto-generated method stub
                    insertSongToNowPlaying(song);
                }
            };
            bindToService(act, connection);
        } else {
            return insertSongToNowPlaying(song);
        }
        return true;
    }

    public static boolean insertListSongToNowPlaying(
            final List<MusicObject> songs, Activity act) {
        if (sService == null) {
            ServiceConnection connection = new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                    // TODO Auto-generated method stub
                    insertListSongToNowPlaying(songs);
                }

            };
            bindToService(act, connection);
        } else {
            return insertListSongToNowPlaying(songs);
        }
        return true;
    }

    private static boolean insertListSongToNowPlaying(List<MusicObject> ListSong) {
        if (sService != null) {
            try {
                return sService.insertListSongToNowPlaying(ListSong);
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
        return false;
    }

    private static boolean insertSongToNowPlaying(MusicObject song) {
        if (sService != null) {
            try {
                return sService.insertToNowPlaying(song);
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
        return false;
    }
    public static boolean removeFromNowPlaying(int position){
        if (sService != null) {
            try {
                return sService.removeFromNowPlaying(position);
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
        return false;
    }
    public static void swap(int from ,int to){
        if (sService != null) {
            try {
                sService.swap(from, to);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void unbindAll() {
        try {
            Set<Context> listContext = sConnectionMap.keySet();
            for (Context ctx : listContext) {
                ServiceBinder serviceBinder = sConnectionMap.remove(ctx);
                if (serviceBinder != null) {
                    ctx.unbindService(serviceBinder);
                }
            }
            sConnectionMap.clear();
            sService = null;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    // --------------------------------//
}
