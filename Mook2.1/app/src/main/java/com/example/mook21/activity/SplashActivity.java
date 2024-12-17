package com.example.mook21.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.view.WindowManager;

import com.example.mook21.R;
import com.example.mook21.shared_preferences.MySharedPreferences;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIMEOUT = 2000;
    private MySharedPreferences mySharedPreferences = new MySharedPreferences(SplashActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        boolean afterFirstRun = mySharedPreferences.getBooleanValue("afterFirstRun");
        if (afterFirstRun){
            new Handler().postDelayed(() ->{
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, SPLASH_TIMEOUT);
        }else{
            mySharedPreferences.putBooleanValue("afterFirstRun", true);
            new Handler().postDelayed(() ->{
                Intent intent = new Intent(SplashActivity.this, FirstStartActivity.class);
                startActivity(intent);
                finish();
            }, SPLASH_TIMEOUT);
        }

    }
}
