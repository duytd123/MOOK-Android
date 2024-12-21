package com.example.mook21.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mook21.R;
import com.example.mook21.fragment_main_activity.DetailMusicActivity;
import com.example.mook21.model.MusicSoundItem;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MusicViewHolder> {

    private final Context context;
    private final List<MusicSoundItem> musicList;

    public RecentAdapter(Context context, List<MusicSoundItem> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    public void updateData(List<MusicSoundItem> newMusicList) {
        musicList.clear();
        musicList.addAll(newMusicList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicSoundItem sound = musicList.get(position);
        holder.title.setText(sound.getTitle());

        // Bo tròn góc 16dp
        int cornerRadiusPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                context.getResources().getDisplayMetrics()
        );

        Glide.with(context)
                .load(sound.getThumbnail())
                .apply(new RequestOptions()
                        .transform(new CenterCrop(), new RoundedCornersTransformation(cornerRadiusPx, 0)))
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMusicActivity.class);
            intent.putExtra("MUSIC_TITLE", sound.getTitle());
            intent.putExtra("MUSIC_IMAGE", sound.getThumbnail());
            intent.putExtra("MUSIC_URL", sound.getUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_music_title);
            image = itemView.findViewById(R.id.iv_music_thumbnail);
        }
    }
}

