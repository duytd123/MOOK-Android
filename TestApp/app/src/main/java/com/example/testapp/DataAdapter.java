package com.example.testapp;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DataModel> modelList;
    private OnItemClickListener listener;
    private FavoritesViewModel favoritesViewModel;
    private List<String> favoriteUrls = new ArrayList<>(); // Stores URLs of favorite GIFs
    private int selectedPosition = -1;  // Keep this existing functionality

    public DataAdapter(Context context, ArrayList<DataModel> modelList, FavoritesViewModel favoritesViewModel) {
        this.context = context;
        this.modelList = modelList;
        this.favoritesViewModel = favoritesViewModel;

        // Observe changes in favorites and update favoriteUrls list
        favoritesViewModel.getAllFavorites().observeForever(new Observer<List<DataModel>>() {
            @Override
            public void onChanged(List<DataModel> favorites) {
                favoriteUrls.clear();
                for (DataModel model : favorites) {
                    favoriteUrls.add(model.getImageUrl());
                }
                notifyDataSetChanged(); // Update UI when favorites list changes
            }
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

        Glide.with(context)
                .asGif()
                .load(model.getImageUrl())
                .override(model.getHeight())
                .into(holder.gifImageView);

        // Highlight if the GIF is in favorites
        if (favoriteUrls.contains(model.getImageUrl())) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(androidx.cardview.R.color.cardview_light_background));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        // Apply selected position highlighting (original functionality)
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected_item_color));
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

    // New helper method to check if a GIF is already a favorite
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

            // Long click to open GIF in FullActivity or add to favorites if not already added
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DataModel selectedModel = adapter.modelList.get(position);

                    // Check if GIF is already a favorite
                    if (adapter.isFavorite(selectedModel.getImageUrl())) {
                        Toast.makeText(context, "Already in favorites", Toast.LENGTH_SHORT).show();
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