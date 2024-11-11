package com.example.testapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private DataAdapter favoritesAdapter;
    private ArrayList<DataModel> favoritesModelList = new ArrayList<>();
    private TextView emptyText;
    private FavoritesViewModel favoritesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view);
        emptyText = view.findViewById(R.id.favorites_empty_text);

        // Set GridLayoutManager with 2 columns for displaying GIFs
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));  // 2 GIFs per row

        // Initialize ViewModel here
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        // Initialize the adapter with ViewModel
        favoritesAdapter = new DataAdapter(getContext(), favoritesModelList, favoritesViewModel);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Observe the LiveData for changes in favorites
        favoritesViewModel.getAllFavorites().observe(getViewLifecycleOwner(), new Observer<List<DataModel>>() {
            @Override
            public void onChanged(List<DataModel> dataModels) {
                favoritesModelList.clear();
                favoritesModelList.addAll(dataModels);
                favoritesAdapter.notifyDataSetChanged();
                checkEmptyList();
            }
        });

        // Set item click listener to show GIF enlarged and ask for confirmation
        favoritesAdapter.setOnItemClickListener(position -> {
            DataModel clickedItem = favoritesModelList.get(position);
            showGifConfirmationDialog(clickedItem, position);
        });

        return view;
    }

    // Show enlarged GIF with confirmation dialog for removal
    private void showGifConfirmationDialog(DataModel clickedItem, int position) {
        // Create a dialog to confirm removal
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_remove_gif, null);
        ImageView enlargedGifImageView = dialogView.findViewById(R.id.enlargedGifImageView);
        Button btnRemove = dialogView.findViewById(R.id.btnRemove);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Load the GIF into the ImageView
        Glide.with(getContext())
                .asGif()
                .load(clickedItem.getImageUrl())
                .into(enlargedGifImageView);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Handle the removal of the GIF
        btnRemove.setOnClickListener(v -> {
            removeFavoriteFromDatabase(clickedItem);
            dialog.dismiss();
        });

        // Handle canceling the removal
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Remove the clicked item from the database
    private void removeFavoriteFromDatabase(DataModel clickedItem) {
        if (getContext() == null || getActivity() == null) {
            return;  // Prevent any actions if the fragment is not attached to the context
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(getContext());
            // Find the corresponding FavoritesGif object using the image URL
            FavoritesGif favoriteGif = db.favoriteGifDao().getFavoriteByImageUrl(clickedItem.getImageUrl());
            if (favoriteGif != null) {
                db.favoriteGifDao().delete(favoriteGif);  // Delete the GIF from the database
                getActivity().runOnUiThread(() -> {
                    // Show a success Toast
                    Toast.makeText(getContext(), "GIF removed from favorites", Toast.LENGTH_SHORT).show();

                    // Optionally, update the RecyclerView after removal
                    favoritesModelList.remove(clickedItem);
                    favoritesAdapter.notifyDataSetChanged();
                    checkEmptyList();
                });
            } else {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to remove GIF", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // Check if the list is empty and show appropriate text
    private void checkEmptyList() {
        if (favoritesModelList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            favoritesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            favoritesRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
