package com.example.testapp;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {

    private final FavoritesRepository favoritesRepository;
    private final LiveData<List<DataModel>> allFavorites;

    public FavoritesViewModel(Application application) {
        super(application);
        favoritesRepository = new FavoritesRepository(application);
        allFavorites = Transformations.map(favoritesRepository.getAllFavorites(), favoritesGifs -> {
            List<DataModel> dataModels = new ArrayList<>();
            for (FavoritesGif gif : favoritesGifs) {
                dataModels.add(new DataModel(
                        gif.getImageUrl(),
                        gif.getHeight(),
                        gif.getName(),
                        gif.isFavorite()
                ));
            }
            return dataModels;
        });
    }

    public LiveData<List<DataModel>> getAllFavorites() {
        return allFavorites;
    }

    public void insert(FavoritesGif favoriteGif) {
        favoritesRepository.insertIfNotExist(favoriteGif);
    }
}
