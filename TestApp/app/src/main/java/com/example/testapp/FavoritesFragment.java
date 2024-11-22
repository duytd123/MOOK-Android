package com.example.testapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private DataAdapter favoritesAdapter;
    private ArrayList<DataModel> favoritesModelList = new ArrayList<>();
    private FavoritesViewModel favoritesViewModel;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view);
        TextView emptyText = view.findViewById(R.id.favorites_empty_text);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        favoritesViewModel = new ViewModelProvider(getActivity()).get(FavoritesViewModel.class); // Use ViewModel from parent activity
        favoritesAdapter = new DataAdapter(getContext(), favoritesModelList, favoritesViewModel);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        favoritesViewModel.getAllFavorites().observe(getViewLifecycleOwner(), dataModels -> {
            favoritesModelList.clear();
            favoritesModelList.addAll(dataModels);
            favoritesAdapter.notifyDataSetChanged();
            checkEmptyList();
        });

        favoritesAdapter.setOnItemClickListener(position -> {
            DataModel clickedItem = favoritesModelList.get(position);
            showGifConfirmationDialog(clickedItem, position);
        });

        return view;
    }

    private void showGifConfirmationDialog(DataModel clickedItem, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_remove_gif, null);
        ImageView enlargedGifImageView = dialogView.findViewById(R.id.enlargedGifImageView);
        Button btnRemove = dialogView.findViewById(R.id.btnRemove);
        Button btnDownload = dialogView.findViewById(R.id.btnDownload);
        Button btnShare = dialogView.findViewById(R.id.btnShare);

        Glide.with(getContext()).asGif().load(clickedItem.getImageUrl()).into(enlargedGifImageView);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnRemove.setOnClickListener(v -> {
            removeFavoriteFromDatabase(clickedItem, position);
            dialog.dismiss();
        });

        btnDownload.setOnClickListener(v -> {
            new GifHandler(getContext()).downloadGifToGallery(clickedItem.getImageUrl());
            dialog.dismiss();
        });

        btnShare.setOnClickListener(v -> {
            new GifHandler(getContext()).shareGif(clickedItem.getImageUrl());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void removeFavoriteFromDatabase(DataModel clickedItem, int position) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(getContext());
            FavoritesGif favoriteGif = db.favoriteGifDao().getFavoriteByImageUrl(clickedItem.getImageUrl());
            if (favoriteGif != null) {
                db.favoriteGifDao().delete(favoriteGif);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "GIF removed from favorites", Toast.LENGTH_SHORT).show();
                    favoritesViewModel.remove(favoriteGif);
                    favoritesModelList.remove(position);
                    favoritesAdapter.notifyItemRemoved(position);
                    checkEmptyList();
                });
            } else {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to remove GIF", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void checkEmptyList() {
        View emptyLayout = getView().findViewById(R.id.favorites_empty_layout);
        if (favoritesModelList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            favoritesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            favoritesRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
