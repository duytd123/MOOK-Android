package com.example.testapp;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DataModel> modelList;
    private OnItemClickListener listener;
    private FavoritesViewModel favoritesViewModel;
    private List<String> favoriteUrls = new ArrayList<>();

    public DataAdapter(Context context, ArrayList<DataModel> modelList, FavoritesViewModel favoritesViewModel) {
        this.context = context;
        this.modelList = modelList;
        this.favoritesViewModel = favoritesViewModel;

        favoritesViewModel.getAllFavorites().observeForever(favorites -> {
            favoriteUrls.clear();
            for (DataModel model : favorites) {
                favoriteUrls.add(model.getImageUrl());
            }
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel model = modelList.get(position);
        holder.bind(model, listener);

        String imageUrl = model.getImageUrl();
        if (imageUrl.startsWith("/")) {
            Glide.with(context)
                    .asGif()
                    .load(new File(imageUrl))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(model.getHeight())
                    .into(holder.gifImageView);
        } else {

            Glide.with(context)
                    .asGif()
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(model.getHeight())
                    .into(holder.gifImageView);
        }

        if (favoriteUrls.contains(imageUrl)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardview_light_background));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        ViewGroup.LayoutParams params = holder.gifImageView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        holder.gifImageView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private boolean isFavorite(String imageUrl) {
        return favoriteUrls.contains(imageUrl);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gifImageView;
        DataAdapter adapter;

        public ViewHolder(View itemView, DataAdapter adapter) {
            super(itemView);
            gifImageView = itemView.findViewById(R.id.gifImageView);
            this.adapter = adapter;
        }

        public void bind(DataModel model, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DataModel selectedModel = adapter.modelList.get(position);

                    if (adapter.isFavorite(selectedModel.getImageUrl())) {
                        Toast.makeText(context, "Already in favorites", Toast.LENGTH_SHORT).show();
                        FavoritesGif favoriteGif = new FavoritesGif(selectedModel.getName(), selectedModel.getImageUrl(), selectedModel.getHeight(), true);
                        favoritesViewModel.remove(favoriteGif);
                    } else {
                        FavoritesGif favoriteGif = new FavoritesGif(selectedModel.getName(), selectedModel.getImageUrl(), selectedModel.getHeight(), true);
                        favoritesViewModel.insert(favoriteGif);
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
