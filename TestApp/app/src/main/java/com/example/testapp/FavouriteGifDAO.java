package com.example.testapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavouriteGifDAO {
    @Insert
    void insertFavouriteGif(FavouriteGif favouriteGif);

    @Delete
    void deleteFavouriteGif(FavouriteGif favouriteGif);

    @Query("SELECT * FROM favourite_gif")
    List<FavouriteGif> getAll();

    @Query("SELECT * FROM favourite_gif WHERE imageUrl = :imageUrl LIMIT 1")
    FavouriteGif getFavouriteGifByUrl(String imageUrl);

    @Update
    void updateFavouriteGif(FavouriteGif favouriteGif);
}
