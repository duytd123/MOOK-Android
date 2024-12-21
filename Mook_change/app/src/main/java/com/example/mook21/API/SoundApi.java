package com.example.mook21.API;


import com.example.mook21.model.MusicSoundGroup;
import com.example.mook21.model.Sound;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SoundApi {
    @GET("ios/sleep_music.json")
    Call<List<Sound>> getSounds();

    @GET("ios/sleep_music.json")
    Call<List<MusicSoundGroup>> getMusicGroups();
}
