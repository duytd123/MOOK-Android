package com.example.mook21.fragment_main_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mook21.R;
import com.example.mook21.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.shareThisApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAppInfo(); // Gọi phương thức shareAppInfo
            }
        });
    }

    // Phương thức để chia sẻ thông tin ứng dụng
    private void shareAppInfo() {
        String appInfo = "Check out my new app! It's called 'SleepSound', and it's designed to help you sleep better with relaxing ambient sounds.\n\n" +
                "Key Features:\n" +
                "1. Play soothing sounds\n" +
                "2. Set sleep timer\n" +
                "3. Customize sound settings\n\n" +
                "You can download it from Google Play: https://play.google.com/store/apps/details?id=com.sleepjar.android&hl=vi";

        // Tạo Intent để chia sẻ thông tin ứng dụng
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, appInfo);

        // Chia sẻ thông tin ứng dụng qua các ứng dụng hỗ trợ chia sẻ
        startActivity(Intent.createChooser(shareIntent, "Share App Info"));
    }
}