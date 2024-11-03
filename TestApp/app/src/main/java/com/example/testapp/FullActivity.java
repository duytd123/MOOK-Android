package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

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


    }
}