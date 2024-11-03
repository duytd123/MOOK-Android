package com.example.testapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private DataAdapter favoritesAdapter;
    private ArrayList<DataModel> favoritesModelList = new ArrayList<>();
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view);
        emptyText = view.findViewById(R.id.favorites_empty_text);

        favoritesAdapter = new DataAdapter(getContext(), favoritesModelList);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Load favorites data (you should implement this method)
        loadFavoritesData();

        return view;
    }

    public void loadFavoritesData() {
        // Add your logic to load favorite GIFs here
        // On success, update visibility based on data:
        if (favoritesModelList.isEmpty()) {
            favoritesRecyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            favoritesRecyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }
}
