package com.quar.turnoffsilentmode.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.quar.turnoffsilentmode.MainActivity;

public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (MainActivity.SILENT_ON && MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying())
            MainActivity.mediaPlayer.stop();
    }
}
