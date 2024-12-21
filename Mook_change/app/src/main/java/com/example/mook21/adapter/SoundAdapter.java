package com.example.mook21.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mook21.R;
import com.example.mook21.databinding.ItemAmbienceBinding;
import com.example.mook21.model.Sound;

import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder> {
    private Context context;
    private List<Sound> soundList;
    private OnItemClickListener onItemClickListener;

    public SoundAdapter(Context context, List<Sound> soundList) {
        this.context = context;
        this.soundList = soundList;
    }

    public Context getContext() {
        return context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setContext(Context context) {
        this.context = context;
        notifyDataSetChanged();
    }

    public List<Sound> getSoundList() {
        return soundList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSoundList(List<Sound> soundList) {
        this.soundList = soundList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateSoundList(List<Sound> newSoundList) {
        this.soundList = newSoundList;
        notifyDataSetChanged();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAmbienceBinding binding = ItemAmbienceBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SoundViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        Sound sound = soundList.get(position);
        holder.bind(sound, onItemClickListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return soundList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        private final ItemAmbienceBinding itemAmbienceBinding;

        public SoundViewHolder(@NonNull ItemAmbienceBinding itemAmbienceBinding) {
            super(itemAmbienceBinding.getRoot());
            this.itemAmbienceBinding = itemAmbienceBinding;
        }

        public void bind(Sound sound, OnItemClickListener onItemClickListener) {
            try {
                // Set icon image
                String resourceName = sound.getIcon().replace(".svg", "");
                int resId = itemAmbienceBinding.getRoot().getContext().getResources()
                        .getIdentifier(resourceName, "drawable", itemAmbienceBinding.getRoot().getContext().getPackageName());
                if (resId != 0) {
                    itemAmbienceBinding.imageBgAmbience.setImageResource(resId);
                } else {
                    itemAmbienceBinding.imageBgAmbience.setImageResource(R.drawable.ic_launcher_foreground);
                }

                if (sound.isPlaying()) {
                    itemAmbienceBinding.imageBgAmbience.setBackgroundResource(R.color.purple);
                } else {
                    itemAmbienceBinding.imageBgAmbience.setBackgroundResource(R.color.blue_dark);
                }

                // Set sound name
                itemAmbienceBinding.textSoundName.setText(sound.getName());

                // Set click listener
                itemAmbienceBinding.getRoot().setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(itemAmbienceBinding.getRoot().getContext(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                System.err.println(e);
            }
        }
    }
}