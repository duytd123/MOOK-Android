package com.example.mook21.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mook21.R;
import com.example.mook21.databinding.ItemAmbienceBinding;
import com.example.mook21.databinding.ItemAmbiencePlayingBinding;
import com.example.mook21.model.Sound;

import java.util.List;

public class SoundPlayingAdapter extends RecyclerView.Adapter<SoundPlayingAdapter.SoundPlayingViewHolder> {
    private Context context;
    private List<Sound> soundPlayingList;
    private OnItemCancelClickListener onItemCancelClickListener;
    private OnSeekBarChangeListener onSeekBarChangeListener;

    public SoundPlayingAdapter(Context context, List<Sound> soundPlayingList) {
        this.context = context;
        this.soundPlayingList = soundPlayingList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        notifyDataSetChanged();
    }

    public List<Sound> getSoundPlayingList() {
        return soundPlayingList;
    }

    public void setSoundPlayingList(List<Sound> soundPlayingList) {
        this.soundPlayingList = soundPlayingList;
        notifyDataSetChanged();
    }

    public interface OnItemCancelClickListener {
        void onCancelClick(int position);
    }

    public void setOnItemCancelClickListener(OnItemCancelClickListener onItemCancelClickListener) {
        this.onItemCancelClickListener = onItemCancelClickListener;
    }

    public interface OnSeekBarChangeListener {
        void onSeekBarChange(int position);
    }

    public OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return onSeekBarChangeListener;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    @NonNull
    @Override
    public SoundPlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAmbiencePlayingBinding itemAmbiencePlayingBinding = ItemAmbiencePlayingBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SoundPlayingViewHolder(itemAmbiencePlayingBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundPlayingViewHolder holder, int position) {
        Sound sound = soundPlayingList.get(position);
        holder.bind(sound, position, onItemCancelClickListener, onSeekBarChangeListener);
    }

    @Override
    public int getItemCount() {
        return soundPlayingList.size();
    }

    public static class SoundPlayingViewHolder extends RecyclerView.ViewHolder {
        private final ItemAmbiencePlayingBinding itemAmbiencePlayingBinding;

        public SoundPlayingViewHolder(@NonNull ItemAmbiencePlayingBinding itemAmbiencePlayingBinding) {
            super(itemAmbiencePlayingBinding.getRoot());
            this.itemAmbiencePlayingBinding = itemAmbiencePlayingBinding;
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(Sound sound, int position, OnItemCancelClickListener onItemCancelClickListener, OnSeekBarChangeListener onSeekBarChangeListener) {
            try {
                String resourceName = sound.getIcon().replace(".svg", "");
                int resId = itemAmbiencePlayingBinding.getRoot().getContext().getResources()
                        .getIdentifier(resourceName, "drawable", itemAmbiencePlayingBinding.getRoot().getContext().getPackageName());
                if (resId != 0) {
                    itemAmbiencePlayingBinding.imageBgAmbience.setImageResource(resId);
                } else {
                    itemAmbiencePlayingBinding.imageBgAmbience.setImageResource(R.drawable.ic_launcher_foreground);
                }
                itemAmbiencePlayingBinding.textViewNameSound.setText(sound.getName());
                itemAmbiencePlayingBinding.seekBarVolume.setProgress(sound.getVolume());

                itemAmbiencePlayingBinding.cancelBg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemCancelClickListener != null) {
                            onItemCancelClickListener.onCancelClick(position);
                        }
                    }
                });

                itemAmbiencePlayingBinding.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && onSeekBarChangeListener != null) {
                            onSeekBarChangeListener.onSeekBarChange(position);
                            sound.setVolume(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (onSeekBarChangeListener != null) {
                            onSeekBarChangeListener.onSeekBarChange(position);
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(itemAmbiencePlayingBinding.getRoot().getContext(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                System.err.println(e);
            }
        }
    }
}
