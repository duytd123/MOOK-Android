package com.example.mook21.fragment_main_activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mook21.R;
import com.example.mook21.adapter.SoundAdapter;
import com.example.mook21.adapter.SoundPlayingAdapter;
import com.example.mook21.databinding.DialogPlayingSoundListBinding;
import com.example.mook21.databinding.DialogTimePickerBinding;
import com.example.mook21.databinding.FragmentAmbienceBinding;
import com.example.mook21.model.Sound;
import com.example.mook21.service.AmbienceService;
import com.example.mook21.shared_preferences.MySharedPreferences;
import com.example.mook21.viewmodel.AmbienceFragmentViewModel;
import com.example.mook21.viewpager.ViewPagerAdapter;
import com.example.mook21.viewpager.ViewPagerAmbienceAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AmbienceFragment extends Fragment implements
        SoundAdapter.OnItemClickListener,
        SoundPlayingAdapter.OnItemCancelClickListener,
        SoundPlayingAdapter.OnSeekBarChangeListener {
    private SoundAdapter soundAdapter;
    private SoundPlayingAdapter soundPlayingAdapter;
    private FragmentAmbienceBinding fragmentAmbienceBinding;
    private AmbienceFragmentViewModel ambienceFragmentViewModel;
    private MySharedPreferences mySharedPreferences;

    private int currentPage = 0;
    private int itemsPerPage = 9;
    private List<List<Sound>> paginatedSoundList;

    private CountDownTimer countDownTimer;
    private long timeInMillis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mySharedPreferences = new MySharedPreferences(requireActivity());
        fragmentAmbienceBinding = FragmentAmbienceBinding.inflate(inflater, container, false);
        initialize();
        clicking();
        return fragmentAmbienceBinding.getRoot();
    }

    private void initialize() {
        try {
            ambienceFragmentViewModel = new ViewModelProvider(requireActivity()).get(AmbienceFragmentViewModel.class);

            ambienceFragmentViewModel.getLiveSoundList().observe(requireActivity(), new Observer<List<Sound>>() {
                @Override
                public void onChanged(List<Sound> sounds) {
                    soundAdapter = new SoundAdapter(requireContext(), sounds);
                    soundAdapter.setOnItemClickListener(AmbienceFragment.this);
                    if (sounds != null && !sounds.isEmpty()) {
                        initializePagination(sounds); // Truyền danh sách âm thanh thực sự vào đây
                    } else {
                        Toast.makeText(requireActivity(), "No sounds available!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ambienceFragmentViewModel.getLiveNumberPlaying().observe(requireActivity(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if (integer > 0) {
                        fragmentAmbienceBinding.tabBadgeCount.setVisibility(View.VISIBLE);
                        fragmentAmbienceBinding.tabBadgeCount.setText(String.valueOf(integer));
                    } else {
                        fragmentAmbienceBinding.tabBadgeCount.setVisibility(View.GONE);
                    }
                }
            });

            ambienceFragmentViewModel.getLiveHavingPlayingSound().observe(requireActivity(), isPlaying -> {
                int iconRes = isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
                fragmentAmbienceBinding.btnPlay.setIconResource(iconRes);
            });
        } catch (Exception e) {
            Toast.makeText(requireActivity(), "Something went wrong, please try again later !", Toast.LENGTH_SHORT).show();
            System.err.println(e);
        }
    }

    private List<List<Sound>> paginateList(List<Sound> originalList, int itemsPerPage) {
        List<List<Sound>> paginatedList = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i += itemsPerPage) {
            int end = Math.min(originalList.size(), i + itemsPerPage);
            paginatedList.add(originalList.subList(i, end));
        }
        return paginatedList;
    }


    private void initializePagination(List<Sound> sounds) {
        int itemsPerPage = 9; // 3x3
        paginatedSoundList = paginateList(sounds, itemsPerPage); // Chuyển đổi danh sách âm thanh thành phân trang
        initializeViewPager(paginatedSoundList); // Gán vào ViewPager
    }

    private void initializeViewPager(List<List<Sound>> paginatedSoundList) {
        ViewPager2 viewPager = fragmentAmbienceBinding.viewPager;
        TabLayout tabLayout = fragmentAmbienceBinding.tabLayout;

        // Gán adapter cho ViewPager2
        viewPager.setAdapter(new ViewPagerAmbienceAdapter(requireActivity(), paginatedSoundList, ambienceFragmentViewModel, soundAdapter));

        // Kết nối TabLayout và ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Tùy chỉnh hiển thị chấm cho mỗi tab
            tab.setIcon(R.drawable.ic_dot_inactive);
        }).attach();

        // Đăng ký sự kiện khi người dùng vuốt qua các trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("PageSelected", "Page: " + position);
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tabItem = tabLayout.getTabAt(i);
                    if (tabItem != null) {
                        tabItem.setIcon(i == position ? R.drawable.ic_dot_active : R.drawable.active_dot);
                    }
                }
            }
        });
    }

    private void clicking() {
        fragmentAmbienceBinding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                ambienceFragmentViewModel.stopAllSounds(requireActivity());
                soundAdapter.notifyDataSetChanged();
            }
        });

        fragmentAmbienceBinding.timerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        fragmentAmbienceBinding.selectedLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoundPlayingList();
            }
        });
    }



    @SuppressLint("DefaultLocale")
    private void showTimePickerDialog() {
        BottomSheetDialog bottomTimerDialog = new BottomSheetDialog(requireActivity());
        DialogTimePickerBinding dialogTimePickerBinding = DialogTimePickerBinding.inflate(getLayoutInflater());
        bottomTimerDialog.setContentView(dialogTimePickerBinding.getRoot());

        setupNumberPicker(dialogTimePickerBinding.hourPicker, 23);
        setupNumberPicker(dialogTimePickerBinding.minutePicker, 59);
        setupNumberPicker(dialogTimePickerBinding.secondPicker, 59);

        dialogTimePickerBinding.hourPicker.setValue(mySharedPreferences.getIntValue("hourStopSound"));
        dialogTimePickerBinding.minutePicker.setValue(mySharedPreferences.getIntValue("minuteStopSound"));
        dialogTimePickerBinding.secondPicker.setValue(mySharedPreferences.getIntValue("secondStopSound"));

        dialogTimePickerBinding.btnDone.setOnClickListener(v -> {
            int hour = dialogTimePickerBinding.hourPicker.getValue();
            int minute = dialogTimePickerBinding.minutePicker.getValue();
            int second = dialogTimePickerBinding.secondPicker.getValue();
            timeInMillis = (hour * 3600 + minute * 60 + second) * 1000; // Chuyển đổi thành mili giây

            // Cập nhật UI hiển thị thời gian đã chọn
            fragmentAmbienceBinding.tabTextTimer.setText(String.format("%02d:%02d:%02d", hour, minute, second));

            // Bắt đầu bộ đếm ngược
            startCountDownTimer();

            // Lưu thời gian vào SharedPreferences
            dialogTimePickerBinding.hourPicker.setValue(mySharedPreferences.getIntValue("hourStopSound"));
            dialogTimePickerBinding.minutePicker.setValue(mySharedPreferences.getIntValue("minuteStopSound"));
            dialogTimePickerBinding.secondPicker.setValue(mySharedPreferences.getIntValue("secondStopSound"));

            bottomTimerDialog.dismiss();
        });

        bottomTimerDialog.show();
    }

    private void startCountDownTimer() {
        if( countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeInMillis, 1000) { // 1000ms = 1s
            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật UI hiển thị thời gian còn lại
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                fragmentAmbienceBinding.tabTextTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                // Khi thời gian hết, dừng tất cả âm thanh và cập nhật UI
                fragmentAmbienceBinding.tabTextTimer.setText("00:00:00");
                stopAllSounds();
            }
        };

        // Bắt đầu đếm ngược
        countDownTimer.start();
    }

    private void stopAllSounds() {
        try {
            // Dừng tất cả các âm thanh đã được bật
            ambienceFragmentViewModel.stopAllSounds(requireActivity());
        } catch (Exception e) {
            Toast.makeText(requireActivity(), "Something went wrong while stopping sounds", Toast.LENGTH_SHORT).show();
            System.err.println(e.getMessage());
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }


    @SuppressLint("DefaultLocale")
    private void setupNumberPicker(NumberPicker numberPicker, int maxValue) {
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setFormatter(i -> String.format("%02d", i));
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            System.out.println("NumberPicker updated: " + newVal);
        });
        setNumberPickerTextColor(numberPicker);
    }

    private void setNumberPickerTextColor(NumberPicker numberPicker) {
        try {
            // Duyệt qua tất cả các field của NumberPicker
            Field[] fields = NumberPicker.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("mSelectorWheelPaint")) {
                    field.setAccessible(true);
                    Paint paint = (Paint) field.get(numberPicker);
                    assert paint != null;
                    paint.setColor(Color.WHITE);
                    numberPicker.invalidate();
                }
            }

            // Duyệt qua các TextView con bên trong NumberPicker
            for (int i = 0; i < numberPicker.getChildCount(); i++) {
                View child = numberPicker.getChildAt(i);
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(Color.WHITE);
                    child.invalidate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSoundPlayingList() {
        BottomSheetDialog bottomListPlayingDialog = new BottomSheetDialog(requireActivity());
        DialogPlayingSoundListBinding dialogPlayingSoundListBinding = DialogPlayingSoundListBinding.inflate(getLayoutInflater());
        bottomListPlayingDialog.setContentView(dialogPlayingSoundListBinding.getRoot());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        dialogPlayingSoundListBinding.ambiencePlayingRecycleView.setLayoutManager(layoutManager);
        if (soundPlayingAdapter == null) {
            soundPlayingAdapter = new SoundPlayingAdapter(requireActivity(), new ArrayList<>());
            soundPlayingAdapter.setOnItemCancelClickListener(this);
            soundPlayingAdapter.setOnSeekBarChangeListener(this);

        }
        dialogPlayingSoundListBinding.ambiencePlayingRecycleView.setAdapter(soundPlayingAdapter);

        ambienceFragmentViewModel.getLivePlayingSoundList().observe(requireActivity(), playingSounds -> {
            if (playingSounds != null && !playingSounds.isEmpty()) {
                soundPlayingAdapter.setSoundPlayingList(playingSounds);
                dialogPlayingSoundListBinding.ambiencePlayingRecycleView.setVisibility(View.VISIBLE);
                dialogPlayingSoundListBinding.noSoundTextView.setVisibility(View.GONE);
            } else {
                dialogPlayingSoundListBinding.ambiencePlayingRecycleView.setVisibility(View.GONE);
                dialogPlayingSoundListBinding.noSoundTextView.setVisibility(View.VISIBLE);
            }
        });


        dialogPlayingSoundListBinding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomListPlayingDialog.dismiss();
            }
        });
        bottomListPlayingDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClick(int pos) {

        try {
            Sound clickedSound = soundAdapter.getSoundList().get(pos);
            String soundFileName = clickedSound.getFileName();
            String soundName = clickedSound.getName();
            int resId = ambienceFragmentViewModel.getResIdSong(soundFileName, requireActivity());

            // Cập nhật Intent với dữ liệu bài nhạc
            Intent intent = ambienceFragmentViewModel.createPlaySoundIntent(resId);
            intent.putExtra("sound_name", soundName); // Gửi tên bài nhạc đến Service
            intent.setClass(requireActivity(), AmbienceService.class);
            requireContext().startService(intent);

            soundAdapter.notifyDataSetChanged();
            ambienceFragmentViewModel.toggleSound(clickedSound, requireActivity());
        } catch (Exception e) {
            Toast.makeText(requireActivity(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
            System.err.println(e.getMessage());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCancelClick(int pos) {
        try {
            Sound clickedSound = soundPlayingAdapter.getSoundPlayingList().get(pos);
            String soundFileName = clickedSound.getFileName();
            int resId = ambienceFragmentViewModel.getResIdSong(soundFileName, requireActivity());
            soundPlayingAdapter.notifyDataSetChanged();
            soundAdapter.notifyDataSetChanged();
            ambienceFragmentViewModel.toggleSound(clickedSound, requireActivity());
            Intent intent = ambienceFragmentViewModel.createPlaySoundIntent(resId);
            intent.setClass(requireActivity(), AmbienceService.class);
            requireContext().startService(intent);
        } catch (Exception e) {
            Toast.makeText(requireActivity(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
            System.err.println(e.getMessage());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSeekBarChange(int pos) {
        try {
            Sound clickedSound = soundPlayingAdapter.getSoundPlayingList().get(pos);
            String soundFileName = clickedSound.getFileName();
            int resId = ambienceFragmentViewModel.getResIdSong(soundFileName, requireActivity());
            Intent intent = ambienceFragmentViewModel.createChangeVolumeSoundIntent(resId, clickedSound.getVolume());
            intent.setClass(requireActivity(), AmbienceService.class);
            requireContext().startService(intent);
        } catch (Exception e) {
            Toast.makeText(requireActivity(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
            System.err.println(e.getMessage());
        }
    }
}