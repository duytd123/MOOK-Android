package com.example.mook21.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AmbienceService extends Service {
    private static final String CHANNEL_ID = "AmbienceServiceChannel";
    private static MediaPlayer myMusic;
    private Handler handler = new Handler();
    private Runnable runnable;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
