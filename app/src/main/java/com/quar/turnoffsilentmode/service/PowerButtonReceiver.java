package com.quar.turnoffsilentmode.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.quar.turnoffsilentmode.MainActivity;

public class PowerButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //ekran o'chganda
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //ekran yonganda
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            //ekranda tasvir paydo bo'lganda
        }

        if (MainActivity.SILENT_ON && MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying())
            MainActivity.mediaPlayer.stop();

    }
}
