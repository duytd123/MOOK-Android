package com.example.mook21.fragment_main_activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mook21.R;
import com.example.mook21.databinding.ActivityDetailMusicBinding;
import com.example.mook21.model.MusicSoundItem;
import com.example.mook21.viewmodel.MusicViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailMusicActivity extends AppCompatActivity {
    private ActivityDetailMusicBinding binding;

    private ImageView imageView, playIcon;
    private Button playButton;
    private TextView titleView, startTime, endTime;
    private SeekBar seekBar;

    private ExoPlayer player;
    private MusicViewModel musicPlayerViewModel;
    private NotificationManager notificationManager;
    private String musicTitle, musicUrl, musicImage;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailMusicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ViewModel
        musicPlayerViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        // Khởi tạo NotificationManager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Initialize views
        titleView = findViewById(R.id.tv_detail_music_title);
        imageView = findViewById(R.id.iv_detail_music_image);
        playButton = findViewById(R.id.btn_play_pause);
        playIcon = findViewById(R.id.iv_play_icon);
        seekBar = findViewById(R.id.seek_bar);
        startTime = findViewById(R.id.tv_start_time);
        endTime = findViewById(R.id.tv_end_time);
        ImageView backButton = findViewById(R.id.iv_back);

        // Get intent data
        musicTitle = getIntent().getStringExtra("MUSIC_TITLE");
        musicImage = getIntent().getStringExtra("MUSIC_IMAGE");
        musicUrl = getIntent().getStringExtra("MUSIC_URL");

        // Display title and image
        titleView.setText(musicTitle);
        Glide.with(this)
                .load(musicImage)
                .apply(new RequestOptions()
                        .transform(new CenterCrop(), new RoundedCornersTransformation(16, 0)))
                .into(imageView);

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();

        // Đặt lắng nghe sự thay đổi của SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Nếu là người dùng kéo thanh seekbar, cập nhật thời gian
                if (fromUser) {
                    int newPosition = progress * 1000; // Chuyển đổi sang milliseconds
                    player.seekTo(newPosition); // Tua tới vị trí mới
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Có thể để logic khác khi bắt đầu kéo, như tạm dừng cập nhật
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Có thể thực hiện hành động sau khi dừng kéo
            }
        });

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(musicUrl));
        player.setMediaItem(mediaItem);
        player.prepare();

        // Listen for player state changes (to update SeekBar and time)
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    int duration = (int) player.getDuration() / 1000;
                    endTime.setText(formatTime(duration));
                    seekBar.setMax(duration);
                    musicPlayerViewModel.setMusicPlaying(true);
                } else {
                    // Nhạc dừng, cập nhật LiveData
                    musicPlayerViewModel.setMusicPlaying(false);
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                int currentPosition = (int) player.getCurrentPosition() / 1000;
                seekBar.setProgress(currentPosition);
                startTime.setText(formatTime(currentPosition));
            }
        });


        backButton.setOnClickListener(v -> {
            // Nhạc dừng, cập nhật LiveData
            musicPlayerViewModel.setMusicPlaying(false);
            finish();
        });

        // Handle play/pause button click
        playButton.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                playIcon.setImageResource(R.drawable.ic_play); // Change to play icon

                // Nhạc dừng, cập nhật LiveData
                musicPlayerViewModel.setMusicPlaying(false);

            } else {
                player.play();
                playIcon.setImageResource(R.drawable.ic_pause); // Change to pause icon
                saveToRecentlyPlayed(musicTitle, musicUrl, musicImage); // Save to Recently Played
                //nhạc play cập nhật LiveData
                musicPlayerViewModel.setMusicPlaying(true);
            }
        });

        // Quan sát LiveData
        musicPlayerViewModel.getIsMusicPlaying().observe(this, isPlaying -> {
            if (isPlaying) {
                // Hiển thị thông báo khi nhạc đang chạy
                showMusicPlayingNotification();
            } else {
                // Hủy thông báo khi nhạc dừng
                cancelMusicPlayingNotification();
            }
        });

        // Update the seek bar while playing
        handler.postDelayed(updateSeekBar, 1000); // Update every second
    }

    // Format time to display as mm:ss
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    // Runnable to update SeekBar every second
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                int currentPosition = (int) player.getCurrentPosition() / 1000; // Convert to seconds
                seekBar.setProgress(currentPosition);
                startTime.setText(formatTime(currentPosition));
            }
            handler.postDelayed(this, 1000); // Update every second
        }
    };

    // Save to Recently Played
    private void saveToRecentlyPlayed(String title, String url, String thumbnail) {
        SharedPreferences sharedPreferences = getSharedPreferences("recently_played", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the current list of recently played
        String recentlyPlayedJson = sharedPreferences.getString("recently_played_list", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<MusicSoundItem>>() {
        }.getType();
        List<MusicSoundItem> recentlyPlayed = gson.fromJson(recentlyPlayedJson, type);

        // Remove the item if it already exists
        recentlyPlayed.removeIf(item -> item.getTitle().equals(title));

        // Add the new item to the top of the list
        recentlyPlayed.add(0, new MusicSoundItem(title, url, thumbnail));

        // Limit the list to 10 items
        if (recentlyPlayed.size() > 10) {
            recentlyPlayed = recentlyPlayed.subList(0, 10);
        }

        // Save the updated list
        String updatedJson = gson.toJson(recentlyPlayed);
        editor.putString("recently_played_list", updatedJson);
        editor.apply();
    }

    private void showMusicPlayingNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "music_channel";
            NotificationChannel channel = new NotificationChannel(channelId, "Music Notifications", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, "music_channel")
                .setContentTitle("Nhạc đang chạy")
                .setContentText(musicTitle)
                .setSmallIcon(R.drawable.ic_music)
                .build();

        notificationManager.notify(1, notification);
    }

    private void cancelMusicPlayingNotification() {
        notificationManager.cancel(1); // Hủy thông báo có ID là 1
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            handler.removeCallbacks(updateSeekBar); // Remove update handler
            player.release();
            player = null;
        }
    }
}
