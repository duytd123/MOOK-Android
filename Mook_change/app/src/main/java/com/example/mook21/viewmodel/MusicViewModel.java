package com.example.mook21.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mook21.model.MusicSoundGroup;
import com.example.mook21.API.RetrofitInstance;
import com.example.mook21.API.SoundApi;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicViewModel extends ViewModel {

    private final MutableLiveData<List<MusicSoundGroup>> musicGroups = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isMusicPlaying = new MutableLiveData<>();

    public LiveData<Boolean> getIsMusicPlaying() {
        return isMusicPlaying;
    }

    public void setMusicPlaying(boolean isPlaying) {
        isMusicPlaying.setValue(isPlaying);
    }

    public LiveData<List<MusicSoundGroup>> getMusicGroups() {
        if (musicGroups.getValue() == null) {
            loadMusicGroups();
        }
        return musicGroups;
    }



    private void loadMusicGroups() {
        SoundApi api = RetrofitInstance.getApi();
        api.getMusicGroups().enqueue(new Callback<List<MusicSoundGroup>>() {
            @Override
            public void onResponse(Call<List<MusicSoundGroup>> call, Response<List<MusicSoundGroup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    musicGroups.postValue(response.body()); // Post the full list of groups
                } else {
                    logError(response);
                }
            }

            @Override
            public void onFailure(Call<List<MusicSoundGroup>> call, Throwable t) {
                Log.e("MusicViewModel", "Failed to load data", t);
            }
        });
    }

    private void logError(Response<List<MusicSoundGroup>> response) {
        try {
            if (response.errorBody() != null) {
                Log.e("MusicViewModel", "API Error: " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e("MusicViewModel", "Error parsing error body", e);
        }
    }


}
