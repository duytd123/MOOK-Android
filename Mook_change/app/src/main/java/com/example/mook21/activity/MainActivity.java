package com.example.mook21.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mook21.R;
import com.example.mook21.databinding.ActivityMainBinding;
import com.example.mook21.databinding.CustomTabBinding;
import com.example.mook21.viewpager.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        activityMainBinding.viewPager2.setAdapter(adapter);
        new TabLayoutMediator(activityMainBinding.tabLayout, activityMainBinding.viewPager2,
                (tab, pos) -> {
                    CustomTabBinding customTabBinding = CustomTabBinding.inflate(getLayoutInflater());
                    switch (pos) {
                        case 0:
                            customTabBinding.tabIcon.setImageResource(R.drawable.ic_moon_sound);
                            customTabBinding.tabText.setText("Relax Sound");
                            break;
                        case 1:
                            customTabBinding.tabIcon.setImageResource(R.drawable.ic_ambience);
                            customTabBinding.tabText.setText("Ambience");
                            break;
                        case 2:
                            customTabBinding.tabIcon.setImageResource(R.drawable.ic_music);
                            customTabBinding.tabText.setText("Music");
                            break;
                        case 3:
                            customTabBinding.tabIcon.setImageResource(R.drawable.ic_tab_setting);
                            customTabBinding.tabText.setText("Settings");
                            break;
                    }
                    tab.setCustomView(customTabBinding.getRoot());
                }).attach();

        activityMainBinding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    }
}