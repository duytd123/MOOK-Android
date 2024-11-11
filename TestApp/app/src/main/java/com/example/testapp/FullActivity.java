package com.example.testapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;

public class FullActivity extends ComponentActivity {

    private ImageView fullImageView;
    private Button favoriteButton;

    private String sourceUrl;
    private FavoritesViewModel favoritesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        fullImageView = findViewById(R.id.fullImage);
        favoriteButton = findViewById(R.id.favoriteButton);

        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        Intent receiver = getIntent();
        sourceUrl = receiver.getStringExtra("imageUrl");

        // Load GIF using Glide
        Glide.with(this).load(sourceUrl).into(fullImageView);

        // Set up save button click listener to save GIF to favorites
        favoriteButton.setOnClickListener(view -> saveGifToFavorites());

        // Set long click listener to allow share or download when GIF is open in FullActivity
        fullImageView.setOnLongClickListener(v -> {
            showShareOrDownloadDialog();
            return true;
        });
    }

    private void showShareOrDownloadDialog() {
        // Create a GifHandler instance
        GifHandler gifHandler = new GifHandler(this);

        // Display a dialog for choosing between sharing or downloading
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

    private void saveGifToFavorites() {
        FavoritesGif favoriteGif = new FavoritesGif("Favorite GIF", sourceUrl, 100, true);

        // Insert the favorite GIF into the ViewModel
        favoritesViewModel.insert(favoriteGif);

        // Provide feedback to the user
        runOnUiThread(() -> {
            Toast.makeText(this, "GIF saved to Favorites", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity
        });
    }
}