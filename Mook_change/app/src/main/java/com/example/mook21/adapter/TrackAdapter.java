package com.example.mook21.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mook21.R;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private final List<String> tracks;
    private final OnVolumeChangeListener listener;

    public TrackAdapter(List<String> tracks, OnVolumeChangeListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        String track = tracks.get(position);
        holder.trackName.setText(track);

        holder.volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    listener.onVolumeChange(track, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView trackName;
        SeekBar volumeSeekBar;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.tv_track_name);
            volumeSeekBar = itemView.findViewById(R.id.seek_bar_volume);
        }
    }

    public interface OnVolumeChangeListener {
        void onVolumeChange(String track, int volume);
    }
}
