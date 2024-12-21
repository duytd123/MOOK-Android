package com.example.mook21.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.io.IOException;

public class SoundPlaybackService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String soundFileName = intent.getStringExtra("SOUND_FILE_NAME"); // Nhận tên file từ Intent

        if (soundFileName != null) {
            playSoundFromAssets(soundFileName); // Phát âm thanh từ tên file
        }
        return START_STICKY; // Dịch vụ sẽ khởi động lại nếu bị hủy
    }

    private void playSoundFromAssets(String soundFileName) {
        // Dừng MediaPlayer trước đó nếu có
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        try {
            // Tạo MediaPlayer từ file trong assets
            AssetFileDescriptor afd = getAssets().openFd("sounds/" + soundFileName); // Đường dẫn file trong assets
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.setLooping(true); // Lặp lại âm thanh
            mediaPlayer.prepare();
            mediaPlayer.start(); // Bắt đầu phát nhạc
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Không ràng buộc dịch vụ với client
    }
}
