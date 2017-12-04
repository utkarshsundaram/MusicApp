package com.bluesky.exoplayerexample.PlayerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mac on 7/17/17.
 */

public class LockscreenIntentReceiver extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d("Receiver", "LockscreenIntentReceiver: ACTION_SCREEN_OFF");
//                disableNotification();
            }else if (action.equals(Intent.ACTION_SCREEN_ON)){

            }
            else if (action.equals(Intent.ACTION_USER_PRESENT)){
                Log.d("Receiver", "LockscreenIntentReceiver: ACTION_USER_PRESENT");

            }
        } catch (Exception e) {
            Log.e("Receiver", "LockscreenIntentReceiver exception: " + e.toString());
        }
    }
}
