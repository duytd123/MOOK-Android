package com.example.mook21.viewpager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mook21.R;
import com.example.mook21.adapter.SoundAdapter;
import com.example.mook21.databinding.ItemViewPagerBinding;
import com.example.mook21.model.Sound;
import com.example.mook21.service.AmbienceService;
import com.example.mook21.viewmodel.AmbienceFragmentViewModel;

import java.util.List;

public class ViewPagerAmbienceAdapter extends RecyclerView.Adapter<ViewPagerAmbienceAdapter.PageViewHolder> {
    private final List<List<Sound>> paginatedSoundList;
    private final Context context;
    private AmbienceFragmentViewModel ambienceFragmentViewModel;
    private SoundAdapter soundAdapter;

    // Constructor with all dependencies
    public ViewPagerAmbienceAdapter(Context context, List<List<Sound>> paginatedSoundList, AmbienceFragmentViewModel ambienceFragmentViewModel, SoundAdapter soundAdapter) {
        this.context = context;
        this.paginatedSoundList = paginatedSoundList;
        this.ambienceFragmentViewModel = ambienceFragmentViewModel;
        this.soundAdapter = soundAdapter;
    }

    // Constructor without ViewModel and SoundAdapter
    public ViewPagerAmbienceAdapter(Context context, List<List<Sound>> paginatedSoundList) {
        this.context = context;
        this.paginatedSoundList = paginatedSoundList;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewPagerBinding binding = ItemViewPagerBinding.inflate(LayoutInflater.from(context), parent, false);
        return new PageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        List<Sound> sounds = paginatedSoundList.get(position);

        // Set up RecyclerView for each page in ViewPager
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        holder.binding.recyclerView.setLayoutManager(layoutManager);

        if (soundAdapter == null) {
            soundAdapter = new SoundAdapter(context, sounds);
        } else {
            soundAdapter.updateSoundList(sounds);
        }

        holder.binding.recyclerView.setAdapter(soundAdapter);

        // Optional: Add item click listener for SoundAdapter
        if (ambienceFragmentViewModel != null) {
            soundAdapter.setOnItemClickListener(pos -> {
                try {
                    Sound clickedSound = soundAdapter.getSoundList().get(pos);
                    String soundFileName = clickedSound.getFileName();
                    int resId = ambienceFragmentViewModel.getResIdSong(soundFileName, context);
                    soundAdapter.notifyDataSetChanged();
                    ambienceFragmentViewModel.toggleSound(clickedSound, context);
                    Intent intent = ambienceFragmentViewModel.createPlaySoundIntent(resId);
                    intent.setClass(context, AmbienceService.class);
                    context.startService(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return paginatedSoundList.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewPagerBinding binding;

        public PageViewHolder(@NonNull ItemViewPagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(List<Sound> sounds) {
            SoundAdapter soundAdapter = new SoundAdapter(itemView.getContext(), sounds);
            binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            binding.recyclerView.setAdapter(soundAdapter);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }

        public void bind(List<Sound> sounds) {
            SoundAdapter soundAdapter = new SoundAdapter(itemView.getContext(), sounds);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(soundAdapter);
        }
    }
}
