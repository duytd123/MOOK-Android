package com.example.testapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FavoritesRepository {

    private final FavoriteGifDao favoriteGifDao;
    private final LiveData<List<FavoritesGif>> allFavorites;

    public FavoritesRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        favoriteGifDao = db.favoriteGifDao();
        allFavorites = favoriteGifDao.getAllFavorites();
    }

    public LiveData<List<FavoritesGif>> getAllFavorites() {
        return allFavorites;
    }

    public void insertIfNotExist(FavoritesGif favoriteGif) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            FavoritesGif existingGif = favoriteGifDao.getFavoriteByImageUrl(favoriteGif.getImageUrl());
            if (existingGif == null) {
                favoriteGifDao.insert(favoriteGif);
            }
        });
    }
    public void delete(FavoritesGif favoriteGif) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            favoriteGifDao.delete(favoriteGif);
        });
    }


}
