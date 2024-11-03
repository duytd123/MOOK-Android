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

public class TrendingFragment extends Fragment {

    private RecyclerView trendingRecyclerView;
    private DataAdapter trendingAdapter;
    private ArrayList<DataModel> trendingModelList = new ArrayList<>();
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        trendingRecyclerView = view.findViewById(R.id.trending_recycler_view);
        emptyText = view.findViewById(R.id.trending_empty_text);

        trendingAdapter = new DataAdapter(getContext(), trendingModelList);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trendingRecyclerView.setAdapter(trendingAdapter);

        // Load trending data (you should implement this method)
        loadTrendingData();

        return view;
    }

    public void loadTrendingData() {
        // Add your logic to load trending GIFs here
        // On success, update visibility based on data:
        if (trendingModelList.isEmpty()) {
            trendingRecyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            trendingRecyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }
}
