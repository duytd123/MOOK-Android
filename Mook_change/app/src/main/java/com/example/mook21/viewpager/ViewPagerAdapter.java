package com.example.mook21.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mook21.fragment_main_activity.AmbienceFragment;
import com.example.mook21.fragment_main_activity.MusicFragment;
import com.example.mook21.fragment_main_activity.RelaxFragment;
import com.example.mook21.fragment_main_activity.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new RelaxFragment();
            case 1: return new AmbienceFragment();
            case 2: return new MusicFragment();
            case 3: return new SettingFragment();
            default: return new RelaxFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
