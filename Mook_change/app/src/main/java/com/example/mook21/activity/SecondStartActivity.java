package com.example.mook21.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mook21.databinding.ActivitySecondStartBinding;

public class SecondStartActivity extends AppCompatActivity {
    private ActivitySecondStartBinding activitySecondStartBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activitySecondStartBinding = ActivitySecondStartBinding.inflate(getLayoutInflater());
        setContentView(activitySecondStartBinding.getRoot());
        activitySecondStartBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondStartActivity.this, ThirdStartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
