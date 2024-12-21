package com.example.mook21.fragment_main_activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mook21.adapter.GenreAdapter;
import com.example.mook21.adapter.MusicAdapter;
import com.example.mook21.adapter.RecentAdapter;
import com.example.mook21.databinding.FragmentMusicBinding;

import com.example.mook21.model.MusicSoundGroup;
import com.example.mook21.model.MusicSoundItem;
import com.example.mook21.viewmodel.MusicViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends Fragment {

    private RecyclerView recyclerViewMusic, recyclerViewRecentlyPlayed, recyclerViewGenres;
    private MusicAdapter musicAdapter;
    private RecentAdapter recentlyPlayedAdapter;
    private GenreAdapter genreAdapter;
    private MusicViewModel musicViewModel;
    private ProgressBar progressBar;
    private FragmentMusicBinding binding;


    private List<MusicSoundItem> fullMusicList = new ArrayList<>();
    private List<String> genres = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);

        // Initialize RecyclerViews
        recyclerViewMusic = binding.recyclerViewMusic;
        recyclerViewMusic.setLayoutManager(new GridLayoutManager(getContext(), 2));
        musicAdapter = new MusicAdapter(getContext(), new ArrayList<>());
        recyclerViewMusic.setAdapter(musicAdapter);

        recyclerViewRecentlyPlayed = binding.recyclerViewRecentlyPlayed;
        recyclerViewRecentlyPlayed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recentlyPlayedAdapter = new RecentAdapter(getContext(), new ArrayList<>());
        recyclerViewRecentlyPlayed.setAdapter(recentlyPlayedAdapter);

        recyclerViewGenres = binding.recyclerViewGenres;
        recyclerViewGenres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        genreAdapter = new GenreAdapter(new ArrayList<>(), this::filterByGenre);
        recyclerViewGenres.setAdapter(genreAdapter);

        progressBar = binding.progressBar;

        // Load Recently Played
        loadRecentlyPlayed();

        // Load Music Data
        musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);
        observeMusicData();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentlyPlayed(); // Tải lại danh sách "Recently Played" khi quay lại tab "Music"
    }

    private void loadRecentlyPlayed() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("recently_played", Context.MODE_PRIVATE);
        String recentlyPlayedJson = sharedPreferences.getString("recently_played_list", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<MusicSoundItem>>() {
        }.getType();
        List<MusicSoundItem> recentlyPlayed = gson.fromJson(recentlyPlayedJson, type);

        if (!recentlyPlayed.isEmpty()) {
            recentlyPlayedAdapter.updateData(recentlyPlayed);
            recyclerViewRecentlyPlayed.setVisibility(View.VISIBLE);
        } else {
            recyclerViewRecentlyPlayed.setVisibility(View.GONE);
        }
    }

    private void observeMusicData() {
        musicViewModel.getMusicGroups().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null && !groups.isEmpty()) {
                fullMusicList.clear();
                genres.clear();
                genres.add("All");

                // Use explicit type MusicSoundGroup for the loop
                for (MusicSoundGroup group : groups) {
                    fullMusicList.addAll(group.getItems()); // group.getItems() returns List<MusicSoundItem>
                    genres.add(group.getGroup()); // group.getGroup() returns String
                }

                genreAdapter.updateGenres(genres);
                musicAdapter.updateData(fullMusicList);



                // Sau khi load xong bài hát
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void filterByGenre(String genre) {
        if (genre.equals("All")) {
            musicAdapter.updateData(fullMusicList);
        } else {
            List<MusicSoundItem> filteredList = new ArrayList<>();
            for (MusicSoundItem item : fullMusicList) {
                if (item.getGroup().equals(genre)) {
                    filteredList.add(item);
                }
            }
            musicAdapter.updateData(filteredList);
        }
    }
}
