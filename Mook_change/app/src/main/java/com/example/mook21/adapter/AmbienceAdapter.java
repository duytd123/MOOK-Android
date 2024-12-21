package com.example.mook21.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mook21.R;
import com.example.mook21.model.Sound;

import java.util.List;

public class AmbienceAdapter extends RecyclerView.Adapter<AmbienceAdapter.ViewHolder> {

    private List<Sound> soundList;

    public AmbienceAdapter(List<Sound> soundList) {
        this.soundList = soundList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Sound> newData) {
        this.soundList.clear();
        this.soundList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ambience, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sound sound = soundList.get(position);
        holder.textSoundName.setText(sound.getName());
        // Set other data like image here
    }

    @Override
    public int getItemCount() {
        return soundList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textSoundName;
        ImageView imageBgAmbience;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSoundName = itemView.findViewById(R.id.text_sound_name);
            imageBgAmbience = itemView.findViewById(R.id.image_bg_ambience);
        }
    }
}
