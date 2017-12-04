package com.bluesky.exoplayerexample.PlayerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;


public class MediaRemoteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Toast.makeText(context, "Tai nghe không kết nối.", Toast.LENGTH_SHORT).show();
            // send an intent to our MusicService to telling it to pause the audio
//            PlayerUtil.pause();

        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                	//Play/pause
//        			if (PlayerUtil.isPlaying()) {
//        				PlayerUtil.pause();
//        			} else {
//        				PlayerUtil.play();
//        			}
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_PLAY:
//                    PlayerUtil.play();
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_PAUSE:
//                    PlayerUtil.pause();
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_STOP:
//                    PlayerUtil.stop();
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_NEXT:
//                    PlayerUtil.next();
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                    // TODO: ensure that doing this in rapid succession actually plays the
//                    // previous song
//                    PlayerUtil.back();
//                    break;
            }
        }
	}

}
