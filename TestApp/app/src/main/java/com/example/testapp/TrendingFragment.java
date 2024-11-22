package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TrendingFragment extends Fragment implements DataAdapter.OnItemClickListener {

    private RecyclerView trendingRecyclerView;
    private DataAdapter trendingAdapter;
    private ArrayList<DataModel> trendingModelList = new ArrayList<>();
    private TextView emptyText;
    private ProgressBar loadingSpinner;
    private boolean isLoading = false;
    private final Handler handler = new Handler();
    private AppDatabase appDatabase;

    private int trendingOffset = 0;
    private static final int LIMIT = 20;
    private static final long REFRESH_INTERVAL = 3600000; // same 1 hours

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        trendingRecyclerView = view.findViewById(R.id.trending_recycler_view);
        emptyText = view.findViewById(R.id.trending_empty_text);
        loadingSpinner = view.findViewById(R.id.loading_spinner);
        appDatabase = AppDatabase.getDatabase(requireContext());
        initializeRecyclerView();

        if (MainActivity.isInternetAvailable(getContext())) {
            loadTrendingData();
            scheduleDataRefresh();
        } else {
            loadCachedGifs();
        }

        return view;
        // return t
    }

    private void initializeRecyclerView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        trendingRecyclerView.setLayoutManager(layoutManager);
        trendingRecyclerView.setHasFixedSize(true);
        trendingRecyclerView.addItemDecoration(new SpaceItem(5));
        FavoritesViewModel favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        trendingAdapter = new DataAdapter(getContext(), trendingModelList, favoritesViewModel);
        trendingAdapter.setOnItemClickListener(this);
        trendingRecyclerView.setAdapter(trendingAdapter);

        trendingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    loadMoreGifs();
                }
            }
        });
    }

    private void loadTrendingData() {
        if (MainActivity.isInternetAvailable(getContext())) {
            isLoading = true;
            loadingSpinner.setVisibility(View.VISIBLE);

            String url = MainActivity.BASE_URL + MainActivity.API_KEY + "&limit=" + LIMIT + "&offset=" + trendingOffset;

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        loadingSpinner.setVisibility(View.GONE);
                        saveGifsToDatabase(response);
                        parseResponseData(response);
                        isLoading = false;
                    },
                    error -> {
                        loadingSpinner.setVisibility(View.GONE);
                        loadCachedGifs();
                        isLoading = false;
                    });

            MySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
        }else{
            loadCachedGifs();
        }
    }

    private void loadMoreGifs() {
        trendingOffset += LIMIT;
        loadTrendingData();
    }

    private void saveGifsToDatabase(JSONObject response){
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                JSONArray dataArray = response.getJSONArray("data");
                appDatabase.trendingDao().clearGifs();

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("downsized_medium");
                    String url = obj.getString("url");
                    int height = obj.getInt("height");

                    TrendingGif gif = new TrendingGif(url, height);
                    appDatabase.trendingDao().insertGif(gif);
                }
            } catch (JSONException e) {
                Log.e("TrendingFragment", "Error saving GIFs to database: " + e.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void parseResponseData(JSONObject response) {
        try {
            JSONArray dataArray = response.getJSONArray("data");

            if (dataArray.length() == 0) {
                showEmptyState();
                return;
            } else {
                hideEmptyState();
            }

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("downsized_medium");
                String imageUrl = obj.getString("url");
                int height = obj.getInt("height");

                trendingModelList.add(new DataModel(imageUrl, height, "", false));
            }
            trendingAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("TrendingFragment", "Error parsing response: " + e.getMessage());
        }
    }

    private void showEmptyState() {
        trendingRecyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        trendingRecyclerView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
    }

    private void scheduleDataRefresh() {
        handler.postDelayed(() -> {
            if (MainActivity.isInternetAvailable(requireContext())) {
                loadTrendingData();
            }
            scheduleDataRefresh();
        }, REFRESH_INTERVAL);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCachedGifs() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<TrendingGif> cachedGifs = appDatabase.trendingDao().getAllGifs();
            if (cachedGifs != null && !cachedGifs.isEmpty()) {
                trendingModelList.clear();
                for (TrendingGif gif : cachedGifs) {
                    trendingModelList.add(new DataModel(gif.getUrl(), gif.getHeight(), "", false));
                }
                requireActivity().runOnUiThread(() -> trendingAdapter.notifyDataSetChanged());
            } else {
                requireActivity().runOnUiThread(() -> {
                    showEmptyState();
                    Toast.makeText(requireContext(), "No cached GIFs available.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onItemClick(int pos) {
        Intent fullView = new Intent(getActivity(), FullActivity.class);
        fullView.putExtra("imageUrl", trendingModelList.get(pos).getImageUrl());
        startActivity(fullView);
    }
}


