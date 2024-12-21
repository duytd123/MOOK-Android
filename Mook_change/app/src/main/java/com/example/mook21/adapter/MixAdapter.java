package com.example.mook21.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mook21.R;
import com.example.mook21.model.Mix;
import com.example.mook21.model.Mix.SoundDetail;
import com.example.mook21.activity.DetailSoundActivity;

import java.util.List;

public class MixAdapter extends RecyclerView.Adapter<MixAdapter.MixViewHolder> {

    private  List<Mix> mixList;

    public MixAdapter(List<Mix> mixList) {
        this.mixList = mixList;

    }

    public void updateMixList(List<Mix> newMixList) {
        this.mixList = newMixList;
        notifyDataSetChanged(); // Refresh the adapter with new data
    }


    @NonNull
    @Override
    public MixViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mix, parent, false);
        return new MixViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MixViewHolder holder, int position) {
        Mix mix = mixList.get(position);
        holder.mixName.setText(mix.getName());

        String imageUrl = "file:///android_asset/images/" + mix.getCover().getThumbnail() + ".webp";

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.mixImage);

        List<SoundDetail> soundDetails = mix.getSounds();
        int soundId = soundDetails != null && !soundDetails.isEmpty() ? soundDetails.get(0).getId() : -1;

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailSoundActivity.class);
            intent.putExtra("IMAGE_URL", imageUrl);
            intent.putExtra("TITLE", mix.getName());
            intent.putExtra("SOUND_ID", soundId);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mixList != null ? mixList.size() : 0;
    }

    static class MixViewHolder extends RecyclerView.ViewHolder {
        TextView mixName;
        ImageView mixImage;

        public MixViewHolder(@NonNull View itemView) {
            super(itemView);
            mixName = itemView.findViewById(R.id.tv_mix_name);
            mixImage = itemView.findViewById(R.id.iv_mix_image);
        }
    }
}
