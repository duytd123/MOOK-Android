package com.example.testapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteGifDao {
    @Insert
    void insert(FavoritesGif favoritesGif);

    @Insert
    void insertAll(List<FavoritesGif> favoritesGifs);

    @Query("SELECT * FROM favorite_gifs WHERE image_Url = :imageUrl LIMIT 1")
    FavoritesGif getFavoriteByImageUrl(String imageUrl);  // Get favorite by URL

    @Query("SELECT * FROM favorite_gifs")
    LiveData<List<FavoritesGif>> getAllFavorites();

    @Query("SELECT * FROM favorite_gifs WHERE is_favorite = 1")
    LiveData<List<FavoritesGif>> getFavoriteGIFs();

    @Update
    void update(FavoritesGif favoritesGif);

    @Delete
    void delete(FavoritesGif favoritesGif);
}