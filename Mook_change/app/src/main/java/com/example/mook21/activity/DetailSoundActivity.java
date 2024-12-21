package com.example.mook21.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.mook21.R;
import com.example.mook21.model.Sound;
import com.example.mook21.viewmodel.JsonHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailSoundActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private ImageView playPauseIcon;
    private TextView tvStartTime, tvEndTime, tvSelectedCount;
    private boolean isPlaying = false;
    private CountDownTimer countDownTimer;
    private List<Sound> selectedPlaylist = new ArrayList<>();
    private int currentSongIndex = 0;
    private TextView tvEdit;
    private Set<Integer> selectedSoundIds = new HashSet<>(); // Set to store selected sound IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sound);

        initializeUI();
        handleIntentData();
    }

    private void initializeUI() {
        ImageView backButton = findViewById(R.id.iv_back);
        seekBar = findViewById(R.id.seek_bar);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        playPauseIcon = findViewById(R.id.iv_play_icon);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        tvEdit = findViewById(R.id.tv_edit_label);

        backButton.setOnClickListener(v -> finish());
        playPauseIcon.setOnClickListener(v -> togglePlayPause());

        findViewById(R.id.btn_edit).setOnClickListener(v -> showTimerDialog());
        findViewById(R.id.btn_selected).setOnClickListener(v -> showPlaylistDialog(JsonHelper.loadSounds(this)));
    }

    private void handleIntentData() {
        String soundName = getIntent().getStringExtra("TITLE");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        int soundId = getIntent().getIntExtra("SOUND_ID", -1);

        TextView soundTitle = findViewById(R.id.tv_sound_title);
        ImageView soundImage = findViewById(R.id.iv_sound_image);

        soundTitle.setText(soundName);
        Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(52)))
                .into(soundImage);

        List<Sound> soundList = JsonHelper.loadSounds(this);
        Sound selectedSound = findSoundById(soundList, soundId);

        if (selectedSound != null) {
            initializeMediaPlayer(selectedSound.getFileName());
        }
    }

    private Sound findSoundById(List<Sound> sounds, int id) {
        if (sounds != null) {
            for (Sound sound : sounds) {
                if (sound.getId() == id) {
                    return sound;
                }
            }
        }
        return null;
    }

    private void initializeMediaPlayer(String baseFileName) {
        releaseMediaPlayer();
        try {
            String[] extensions = {".wav", ".aac"};
            for (String ext : extensions) {
                String fullFileName = "sounds/" + baseFileName + ext;
                try (AssetFileDescriptor afd = getAssets().openFd(fullFileName)) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

                    mediaPlayer.setOnPreparedListener(mp -> {
                        tvEndTime.setText(formatTime(mp.getDuration()));
                        seekBar.setMax(mp.getDuration());
                        updateSeekBar();
                        mp.start(); // Tự động phát
                        playPauseIcon.setImageResource(R.drawable.ic_pause); // Cập nhật biểu tượng nút phát
                        isPlaying = true; // Đánh dấu trạng thái đang phát
                    });

                    mediaPlayer.setOnCompletionListener(mp -> {
                        playNextSound(); // Tự động chuyển bài
                    });

                    mediaPlayer.prepareAsync();
                    return;
                } catch (IOException ignored) {
                }
            }
            Log.e("DetailSoundActivity", "No valid sound file found for " + baseFileName);
        } catch (Exception e) {
            Log.e("DetailSoundActivity", "Error initializing MediaPlayer", e);
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                playPauseIcon.setImageResource(R.drawable.ic_play);
                isPlaying = false;
            } else {
                mediaPlayer.start();
                playPauseIcon.setImageResource(R.drawable.ic_pause);
                isPlaying = true;
                updateSeekBar();
            }
        }
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    tvStartTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showTimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_time2, null);

        NumberPicker npHours = dialogView.findViewById(R.id.np_hours);
        NumberPicker npMinutes = dialogView.findViewById(R.id.np_minutes);
        NumberPicker npSeconds = dialogView.findViewById(R.id.np_seconds);

        npHours.setMinValue(0);
        npHours.setMaxValue(23);

        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(59);

        npSeconds.setMinValue(0);
        npSeconds.setMaxValue(59);

        builder.setView(dialogView)
                .setPositiveButton("Set Timer", (dialog, which) -> {
                    int totalMillis = (npHours.getValue() * 3600 + npMinutes.getValue() * 60 + npSeconds.getValue()) * 1000;
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    countDownTimer = new CountDownTimer(totalMillis, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvEdit.setText(formatTime((int) millisUntilFinished));
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onFinish() {
                            tvEdit.setText("Edit");
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                playPauseIcon.setImageResource(R.drawable.ic_play);
                                isPlaying = false;
                            }
                        }
                    };
                    countDownTimer.start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPlaylistDialog(List<Sound> soundList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_songs, null);
        ListView listView = dialogView.findViewById(R.id.lv_songs);

        List<String> soundNames = new ArrayList<>();
        for (Sound sound : soundList) {
            soundNames.add(sound.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, soundNames);
        listView.setAdapter(adapter);

        // Restore previously selected sounds
        for (int i = 0; i < soundList.size(); i++) {
            if (selectedSoundIds.contains(soundList.get(i).getId())) {
                listView.setItemChecked(i, true);
            }
        }

        builder.setView(dialogView)
                .setPositiveButton("Done", (dialog, which) -> updateSelectedCount())
                .show();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Sound selectedSound = soundList.get(position);
            if (listView.isItemChecked(position)) {
                selectedPlaylist.add(selectedSound);
                selectedSoundIds.add(selectedSound.getId()); // Save the sound ID
            } else {
                selectedPlaylist.remove(selectedSound);
                selectedSoundIds.remove(selectedSound.getId()); // Remove the sound ID
            }
            updateSelectedCount();
        });
    }

    private void playNextSound() {
        if (selectedPlaylist.isEmpty()) {
            Log.w("DetailSoundActivity", "Playlist is empty, cannot play next song.");
            return;
        }
        currentSongIndex = (currentSongIndex + 1) % selectedPlaylist.size();
        Sound nextSound = selectedPlaylist.get(currentSongIndex);
        updateSoundDetails(nextSound);
        initializeMediaPlayer(nextSound.getFileName()); // Tự động phát bài tiếp theo
    }

    private void updateSoundDetails(Sound nextSound) {
        TextView soundTitle = findViewById(R.id.tv_sound_title);
        ImageView soundImage = findViewById(R.id.iv_sound_image);

        soundTitle.setText(nextSound.getName());
//        Glide.with(this)
//                .load(nextSound.getThumbnail())
//                .apply(RequestOptions.bitmapTransform(new RoundedCorners(52)))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(soundImage);
    }

    private void updateSelectedCount() {
        int count = selectedPlaylist.size();
        if (count > 0) {
            tvSelectedCount.setText(String.valueOf(count));
            tvSelectedCount.setVisibility(View.VISIBLE);
        } else {
            tvSelectedCount.setVisibility(View.GONE);
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
        resetPlayer();
    }

    private void resetPlayer() {
        playPauseIcon.setImageResource(R.drawable.ic_play);
        isPlaying = false;
        seekBar.setProgress(0);
        tvStartTime.setText(formatTime(0));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
