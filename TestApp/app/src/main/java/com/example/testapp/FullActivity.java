package com.example.testapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

public class FullActivity extends ComponentActivity {

    private ImageView favoriteHeart;

    private String sourceUrl;
    private FavoritesViewModel favoritesViewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        ImageView fullImageView = findViewById(R.id.fullImage);
        favoriteHeart = findViewById(R.id.favoriteHeart);

        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        sharedPreferences = getSharedPreferences("Favorites", MODE_PRIVATE);

        Intent receiver = getIntent();
        sourceUrl = receiver.getStringExtra("imageUrl");
        Glide.with(this).load(sourceUrl).into(fullImageView);
        boolean isFavorite = sharedPreferences.getBoolean(sourceUrl, false);
        updateFavoriteHeart(isFavorite);
        favoritesViewModel.getAllFavorites().observe(this, favorites -> {
            boolean isInFavorites = false;
            for (DataModel dataModel : favorites) {
                if (dataModel.getImageUrl().equals(sourceUrl)) {
                    isInFavorites = true;
                    break;
                }
            }
            updateFavoriteHeart(isInFavorites);
        });
        favoriteHeart.setOnClickListener(view -> toggleFavoriteStatus());
        fullImageView.setOnLongClickListener(v -> {
            showShareOrDownloadDialog();
            return true;
        });
    }

    private void toggleFavoriteStatus() {
        boolean isFavorite = sharedPreferences.getBoolean(sourceUrl, false);
        if (isFavorite) {
            sharedPreferences.edit().putBoolean(sourceUrl, false).apply();
            favoriteHeart.setImageResource(R.drawable.favorite_24px);
            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();

            FavoritesGif favoriteGif = new FavoritesGif("Favorite GIF", sourceUrl, 100, true);
            favoritesViewModel.remove(favoriteGif);
        } else {
            sharedPreferences.edit().putBoolean(sourceUrl, true).apply();
            favoriteHeart.setImageResource(R.drawable.ic_tim);
            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();

            FavoritesGif favoriteGif = new FavoritesGif("Favorite GIF", sourceUrl, 100, true);
            favoritesViewModel.insert(favoriteGif);
        }
    }

    private void updateFavoriteHeart(boolean isFavorite) {
        if (isFavorite) {
            favoriteHeart.setImageResource(R.drawable.ic_tim);
        } else {
            favoriteHeart.setImageResource(R.drawable.favorite_24px);
        }
    }

    private void showShareOrDownloadDialog() {
        GifHandler gifHandler = new GifHandler(this);
        new AlertDialog.Builder(this)
                .setTitle("Choose an action")
                .setItems(new CharSequence[]{"Share GIF", "Download GIF"}, (dialog, which) -> {
                    if (which == 0) {
                        gifHandler.shareGif(sourceUrl);
                    } else if (which == 1) {
                        gifHandler.downloadGifToGallery(sourceUrl);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}