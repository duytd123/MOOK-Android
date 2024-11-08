package com.example.testapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full);

        fullImageView = findViewById(R.id.fullImage);

        Intent receiver = getIntent();
        String sourceUrl = receiver.getStringExtra("imageUrl");
        Glide.with(this).load(sourceUrl).into(fullImageView);
        fullImageView.setOnLongClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();
            Button btnShare = dialogView.findViewById(R.id.btnShare);
            Button btnSave = dialogView.findViewById(R.id.btnSave);
            btnShare.setOnClickListener(view -> {
                shareGif(sourceUrl);
                dialog.dismiss();
            });
            btnSave.setOnClickListener(view -> {
                Toast.makeText(this, "Saving", Toast.LENGTH_SHORT).show();
                downloadGif(sourceUrl);
                dialog.dismiss();
            });
            dialog.show();
            return true;
        });
    }

    private void shareGif(String url){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "View this gif image: " + url);
        startActivity(Intent.createChooser(shareIntent, "Share GIF image through"));
        Toast.makeText(this, "Sharing", Toast.LENGTH_SHORT).show();
    }

    private void downloadGif(String imageUrl) {
        Uri images = Uri.parse(imageUrl);
        System.out.println(images);
        ContentResolver contentResolver = getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            images = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".gif");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");  // Chỉnh sửa từ "images/gif" thành "image/gif"
        Uri uri = contentResolver.insert(images, contentValues);
        System.out.println(uri);
        if (uri == null) {
            Toast.makeText(this, "Error: Uri is null", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Glide.with(this)
                    .asGif()
                    .load(imageUrl)
                    .into(new CustomTarget<GifDrawable>() {
                        @Override
                        public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                            try {
                                OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
                                ByteBuffer byteBuffer = resource.getBuffer();
                                byte[] bytes = new byte[byteBuffer.capacity()];
                                byteBuffer.get(bytes);
                                outputStream.write(bytes);
                                Objects.requireNonNull(outputStream).close();
                                Toast.makeText(FullActivity.this, "GIF was saved successfully", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                System.err.println(e);
                                Toast.makeText(FullActivity.this, "GIF is not saved: " + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // No-op
                        }
                    });
        } catch (Exception e) {
            System.err.println(e);
            Toast.makeText(FullActivity.this, "GIF is not saved: " + e, Toast.LENGTH_SHORT).show();
        }
    }
}