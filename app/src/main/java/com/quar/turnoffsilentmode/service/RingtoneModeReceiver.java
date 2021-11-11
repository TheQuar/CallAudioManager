package com.quar.turnoffsilentmode.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.quar.turnoffsilentmode.MainActivity;

public class RingtoneModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentMode = audioManager.getRingerMode();

        if (currentMode == AudioManager.RINGER_MODE_SILENT ||
                currentMode == AudioManager.RINGER_MODE_VIBRATE)
            MainActivity.SILENT_ON = true;
        else
            MainActivity.SILENT_ON = false;
    }
}
