package com.example.mook21.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mook21.databinding.ActivityThirdStartBinding;

public class ThirdStartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        com.example.mook21.databinding.ActivityThirdStartBinding activityThirdStartBinding = ActivityThirdStartBinding.inflate(getLayoutInflater());
        setContentView(activityThirdStartBinding.getRoot());
        activityThirdStartBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThirdStartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
