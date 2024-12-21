package com.example.mook21.fragment_main_activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mook21.API.SoundApi;
import com.example.mook21.API.RetrofitInstance;
import com.example.mook21.R;
import com.example.mook21.adapter.MixAdapter;
import com.example.mook21.adapter.SoundAdapter;
import com.example.mook21.model.Mix;
import com.example.mook21.model.Sound;
import com.example.mook21.viewmodel.GridSpacingItemDecoration;
import com.example.mook21.viewmodel.JsonHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class RelaxFragment extends Fragment {

    private RecyclerView recyclerView;
    private MixAdapter adapter;
    private List<Mix> mixes;
    private Button selectedButton = null;

    // Define category constants
    public static final int CATEGORY_PIANO_RELAX = 4;  // Category 4
    public static final int CATEGORY_RAIN = 3;  // Category 3
    public static final int CATEGORY_CITY = 2;  // Category 2
    public static final int CATEGORY_MEDITATION = 5;  // Category 5
    public static final int CATEGORY_FOCUS = 6;  // Category 6
    public static final int CATEGORY_ALL = 0;  // All categories

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relax, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_mixes);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mixes = JsonHelper.loadMixes(requireContext());

        // Set up adapter with all mixes initially
        adapter = new MixAdapter(mixes);
        recyclerView.setAdapter(adapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spacingInPixels));


        // Filter buttons
        Button btnAll = view.findViewById(R.id.btn_all);
        Button btnPianoRelax = view.findViewById(R.id.btn_piano_relax);
        Button btnRain = view.findViewById(R.id.btn_rain);
        Button btnCity = view.findViewById(R.id.btn_city);
        Button btnMeditation = view.findViewById(R.id.btn_meditation);
        Button btnFocus = view.findViewById(R.id.btn_focus);

        btnAll.setOnClickListener(v -> handleButtonSelection(btnAll, CATEGORY_ALL));
        btnPianoRelax.setOnClickListener(v -> handleButtonSelection(btnPianoRelax, CATEGORY_PIANO_RELAX));
        btnRain.setOnClickListener(v -> handleButtonSelection(btnRain, CATEGORY_RAIN));
        btnCity.setOnClickListener(v -> handleButtonSelection(btnCity, CATEGORY_CITY));
        btnMeditation.setOnClickListener(v -> handleButtonSelection(btnMeditation, CATEGORY_MEDITATION));
        btnFocus.setOnClickListener(v -> handleButtonSelection(btnFocus, CATEGORY_FOCUS));

        return view;
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void handleButtonSelection(Button button, int category) {
        if (selectedButton != null) {
            // Khôi phục màu sắc mặc định của nút trước đó
            selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.button_background));
        }

        // Cập nhật trạng thái nút được chọn
        selectedButton = button;
        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.button_selected)); // Màu sáng hơn

        // Gọi phương thức lọc
        filterMixes(category);
    }

    // Filter mixes by category
    private void filterMixes(int category) {
        List<Mix> filteredMixes = new ArrayList<>();
        for (Mix mix : mixes) {
            if (category == CATEGORY_ALL || mix.getCategory() == category) {
                filteredMixes.add(mix);
            }
        }
        adapter.updateMixList(filteredMixes);
    }
}