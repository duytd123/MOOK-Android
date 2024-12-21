package com.example.mook21.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.mook21.R;
import com.example.mook21.adapter.OnboardingAdapter;
import com.example.mook21.activity.MainActivity;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        OnboardingAdapter adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });
    }

    private void finishOnboarding() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
