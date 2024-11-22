package com.example.testapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TrendingDao {
    @Insert
    void insertGif(TrendingGif gifEntity);

    @Query("SELECT * FROM trending_gifs")
    List<TrendingGif> getAllGifs();

    @Query("DELETE FROM trending_gifs")
    void clearGifs();
}

