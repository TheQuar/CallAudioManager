package com.quar.turnoffsilentmode.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import com.quar.turnoffsilentmode.MainActivity;

import java.io.IOException;

public class ServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            serviceIntent.putExtra("inputExtra", "Dastur ish xolatida");
            ContextCompat.startForegroundService(context, serviceIntent);
        } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //bu yerda qo'g'iroq bo'lganda bajariladi
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                if (number != null && MainActivity.numberViewModel.isNumber(number.replace("+", "")) != null) {
                    control_audio_manager(context, CallType.EXTRA_STATE_RINGING);
                }

            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //bu yerda  qo'g'iroq  qilgan odam qizil tugmani bosganda bajariladi
                control_audio_manager(context, CallType.EXTRA_STATE_IDLE);


            } else if (state.equals(TelephonyManager.CALL_STATE_IDLE)) {
                //bu yerda qo'g'iroqni o'zi tugatganda bajariladi
                control_audio_manager(context, CallType.EXTRA_STATE_IDLE);


            }
        }
    }


    private void control_audio_manager(Context context, CallType callType) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int currentMode = audioManager.getRingerMode();

        if (MainActivity.SILENT_ON) {
            if (callType == CallType.EXTRA_STATE_RINGING) {
//                if (currentMode == AudioManager.RINGER_MODE_SILENT || currentMode == AudioManager.RINGER_MODE_VIBRATE) {
//                    MainActivity.ringtone.play();
//                    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    MainActivity.init(context);
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                }

            }else if (callType == CallType.EXTRA_STATE_IDLE) {
//                if (currentMode == AudioManager.RINGER_MODE_NORMAL) {
//                    MainActivity.ringtone.stop();
//                    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMinVolume(AudioManager.STREAM_RING), 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                    if (MainActivity.mediaPlayer.isPlaying()) MainActivity.mediaPlayer.stop();
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                }

            }
        }
    }


    enum CallType {EXTRA_STATE_RINGING, EXTRA_STATE_IDLE}


}
