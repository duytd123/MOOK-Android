package com.example.mook21.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mook21.R;
import com.example.mook21.model.Sound;
import com.example.mook21.service.AmbienceService;
import com.example.mook21.shared_preferences.MySharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AmbienceFragmentViewModel extends ViewModel {
    private List<Sound> sounds;
    private List<Sound> playingSounds;

    private MutableLiveData<List<Sound>> liveSoundList;
    private MutableLiveData<List<Sound>> livePlayingSoundList;
    private MutableLiveData<Integer> liveNumberPlaying;
    private MutableLiveData<Boolean> liveHavingPlayingSound;



    public AmbienceFragmentViewModel() {
        sounds = new ArrayList<>();
        playingSounds = new ArrayList<>();
        this.liveSoundList = new MutableLiveData<>();
        this.livePlayingSoundList = new MutableLiveData<>();
        this.liveNumberPlaying = new MutableLiveData<>();
        this.liveHavingPlayingSound = new MutableLiveData<>();
        loadSounds();
        getPlayingSounds();
    }

    private void loadSounds() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Sound>>() {
        }.getType();
        try (InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/sounds.json"))) {
            sounds = gson.fromJson(reader, listType);
            liveSoundList.setValue(sounds);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public Intent createPlaySoundIntent(int resId) {
        Intent intent = new Intent();
        intent.setAction(AmbienceService.PLAY_SOUND_ACTION);
        intent.putExtra("resId", resId);
        return intent;
    }

    public Intent createChangeVolumeSoundIntent(int resId, int volume) {
        Intent intent = new Intent();
        intent.setAction(AmbienceService.UPDATE_VOLUME_ACTION);
        intent.putExtra("resId", resId);
        intent.putExtra("volume", volume);
        return intent;
    }

    public void toggleSound(Sound sound, Context context) {
        if (sound.isPlaying()) {
            Toast.makeText(context, "Stopping: " + sound.getName(), Toast.LENGTH_SHORT).show();
            sound.setPlaying(false);
        } else {
            Toast.makeText(context, "Playing: " + sound.getName(), Toast.LENGTH_SHORT).show();
            sound.setPlaying(true);
        }
        updatePlayingStatus();
        getPlayingSounds();
    }

    private void updatePlayingStatus() {
        if (liveSoundList.getValue() != null) {
            long count = liveSoundList.getValue().stream().filter(Sound::isPlaying).count();
            liveNumberPlaying.setValue((int) count);
            liveHavingPlayingSound.setValue(count > 0);
        }
    }

    public void stopAllSounds(Context context) {
        Intent stopAllSoundsIntent = new Intent(context, AmbienceService.class);
        stopAllSoundsIntent.setAction(AmbienceService.STOP_ALL_SOUNDS_ACTION);
        context.startService(stopAllSoundsIntent);
        for (Sound sound : sounds) {
            if (sound.isPlaying()) {
                sound.setPlaying(false);
            }
        }
        liveSoundList.setValue(sounds);
        liveNumberPlaying.setValue(0);
        liveHavingPlayingSound.setValue(false);
        getPlayingSounds();
    }

    public int getResIdSong(String fileName, Context context) {
        return context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
    }

    public void startTimer(int hours, int minutes, int seconds, Context context) {
        LocalDateTime timeNow = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timeNow = LocalDateTime.now();
        }
        int hourNow = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hourNow = timeNow.getHour();
        }
        int minuteNow = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            minuteNow = timeNow.getMinute();
        }
        int secondNow = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            secondNow = timeNow.getSecond();
        }
        System.out.println("Start timer with: hour=" + hourNow + ", minute=" + minuteNow + ", second=" + secondNow);
        int millisTimeSet = (hours * 3600 + minutes * 60 + seconds) * 1000;
        int millisTimeNow = (hourNow * 3600 + minuteNow * 60 + secondNow) * 1000;
        int totalMillis = millisTimeSet - millisTimeNow;
        System.out.println("totalMillis: " + totalMillis);
        new CountDownTimer(totalMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                stopAllSounds(context);
                Toast.makeText(context, "Stop all sounds", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    public void setTimeStopSound(int hour, int minute, int second, MySharedPreferences mySharedPreferences) {
        mySharedPreferences.putIntValue("hourStopSound", hour);
        mySharedPreferences.putIntValue("minuteStopSound", minute);
        mySharedPreferences.putIntValue("secondStopSound", second);
    }

    public void getPlayingSounds() {
        playingSounds.clear();
        for (Sound sound : sounds) {
            if (sound.isPlaying()) { // Giả sử có cờ "isPlaying" trong model Sound
                playingSounds.add(sound);
                System.out.println(sound);
            }
        }
        livePlayingSoundList.setValue(playingSounds);
    }






    public MutableLiveData<List<Sound>> getLiveSoundList() {
        return liveSoundList;
    }

    public void setLiveSoundList(MutableLiveData<List<Sound>> liveSoundList) {
        this.liveSoundList = liveSoundList;
    }

    public MutableLiveData<Integer> getLiveNumberPlaying() {
        return liveNumberPlaying;
    }

    public void setLiveNumberPlaying(MutableLiveData<Integer> liveNumberPlaying) {
        this.liveNumberPlaying = liveNumberPlaying;
    }

    public MutableLiveData<Boolean> getLiveHavingPlayingSound() {
        return liveHavingPlayingSound;
    }

    public void setLiveHavingPlayingSound(MutableLiveData<Boolean> liveHavingPlayingSound) {
        this.liveHavingPlayingSound = liveHavingPlayingSound;
    }

    public MutableLiveData<List<Sound>> getLivePlayingSoundList() {
        return livePlayingSoundList;
    }

    public void setLivePlayingSoundList(MutableLiveData<List<Sound>> livePlayingSoundList) {
        this.livePlayingSoundList = livePlayingSoundList;
    }
}
