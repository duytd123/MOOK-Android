package com.example.testapp;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class FullActivity extends AppCompatActivity {

    private ImageView fullImageView;
    private DataModel imgFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full);

        fullImageView = findViewById(R.id.fullImage);

        Intent receiver = getIntent();
        imgFull = (DataModel) receiver.getSerializableExtra("imageFull");
        Glide.with(this).load(imgFull.getImageUrl()).into(fullImageView);
        fullImageView.setOnLongClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();
            Button btnShare = dialogView.findViewById(R.id.btnShare);
            Button btnSave = dialogView.findViewById(R.id.btnSave);
            Button btnFavourite = dialogView.findViewById(R.id.btnFavourite);
            btnShare.setOnClickListener(view -> {
                shareGif(imgFull.getImageUrl());
                dialog.dismiss();
            });
            btnSave.setOnClickListener(view -> {
                Toast.makeText(this, "Saving", Toast.LENGTH_SHORT).show();
                downloadGif(imgFull.getImageUrl());
                dialog.dismiss();
            });
            btnFavourite.setOnClickListener(view -> {
                dialog.dismiss();
            });
            dialog.show();
            return true;
        });
    }

    private void shareGif(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "View this gif image: " + url);
        startActivity(Intent.createChooser(shareIntent, "Share GIF image through"));
        Toast.makeText(this, "Sharing", Toast.LENGTH_SHORT).show();
    }

    private void downloadGif(String imageUrl) {
        Uri uri = Uri.parse(imageUrl);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, imgFull.getTitle() + ".gif");
        downloadManager.enqueue(request);
        Toast.makeText(this, "Downloaded sucessfully !", Toast.LENGTH_SHORT).show();
    }
}