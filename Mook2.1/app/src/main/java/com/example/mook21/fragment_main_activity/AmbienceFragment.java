package com.example.mook21.fragment_main_activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mook21.R;
import com.example.mook21.adapter.AmbienceAdapter;
import com.example.mook21.model.Sound;
import com.example.mook21.viewmodel.AmbienceFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class AmbienceFragment extends Fragment {

    private RecyclerView recyclerView;
    private AmbienceAdapter adapter;
    private AmbienceFragmentViewModel viewModel;

    private List<List<Sound>> paginatedSoundList = new ArrayList<>();
    private int currentPage = 0;

    private ViewGroup paginationContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ambience, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.ambience_recycle_view);
        paginationContainer = view.findViewById(R.id.pagination_container); // Your container for dots

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new AmbienceAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AmbienceFragmentViewModel.class);
        viewModel.getLiveSoundList().observe(getViewLifecycleOwner(), this::paginateSounds);
    }

    private void paginateSounds(List<Sound> sounds) {
        if (sounds == null || sounds.isEmpty()) {
            Log.e("AmbienceFragment", "Sound list is empty or null!");
            return;
        }

        paginatedSoundList.clear();
        int pageSize = 9;

        // Split the sound list into pages of 9 items
        for (int i = 0; i < sounds.size(); i += pageSize) {
            paginatedSoundList.add(sounds.subList(i, Math.min(i + pageSize, sounds.size())));
        }

        currentPage = 0; // Reset to first page
        updateRecyclerView();
        updatePaginationDots();
    }

    private void updateRecyclerView() {
        adapter.updateData(paginatedSoundList.get(currentPage));
    }

    private void updatePaginationDots() {
        if (paginationContainer == null) {
            Log.e("AmbienceFragment", "paginationContainer is NULL in updatePaginationDots!");
            return;
        }
        paginationContainer.removeAllViews();

        for (int i = 0; i < paginatedSoundList.size(); i++) {
            View dot = LayoutInflater.from(getContext()).inflate(R.layout.pagination_dot, paginationContainer, false);
            dot.setSelected(i == currentPage);

            int finalI = i;
            dot.setOnClickListener(v -> {
                currentPage = finalI;
                updateRecyclerView();
                updatePaginationDots();
            });

            paginationContainer.addView(dot);
        }
    }
}
