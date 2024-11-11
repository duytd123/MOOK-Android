package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrendingFragment extends Fragment implements DataAdapter.OnItemClickListener {

    private RecyclerView trendingRecyclerView;
    private DataAdapter trendingAdapter;
    private ArrayList<DataModel> trendingModelList = new ArrayList<>();
    private TextView emptyText;
    private ProgressBar loadingSpinner;
    private boolean isLoading = false;
    private final Handler handler = new Handler();
    private int trendingOffset = 0;
    private static final int LIMIT = 20;
    private static final long REFRESH_INTERVAL = 3600000;
    private FavoritesRepository favoritesRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        // Initialize views and adapter
        trendingRecyclerView = view.findViewById(R.id.trending_recycler_view);
        emptyText = view.findViewById(R.id.trending_empty_text);
        loadingSpinner = view.findViewById(R.id.loading_spinner);
        initializeRecyclerView();

        if (isNetworkAvailable()) {
            loadTrendingData();
            scheduleDataRefresh();
        } else {
            loadCachedData();
        }

        return view;
    }

    private void initializeRecyclerView() {
        // Set up layout manager and adapter
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        trendingRecyclerView.setLayoutManager(layoutManager);
        trendingRecyclerView.setHasFixedSize(true);
        trendingRecyclerView.addItemDecoration(new SpaceItem(5));
        FavoritesViewModel favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        trendingAdapter = new DataAdapter(getContext(), trendingModelList,favoritesViewModel);
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

    public void loadTrendingData() {
        if (isNetworkAvailable()) {
            isLoading = true;
            loadingSpinner.setVisibility(View.VISIBLE);

            String url = MainActivity.BASE_URL + MainActivity.API_KEY + "&limit=" + LIMIT + "&offset=" + trendingOffset;

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        loadingSpinner.setVisibility(View.GONE);
                        parseResponseData(response);
                        isLoading = false;
                    },
                    error -> {
                        loadingSpinner.setVisibility(View.GONE);
                        loadCachedData();
                        isLoading = false;
                    });

            MySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
        }
    }

    private void loadMoreGifs() {
        trendingOffset += LIMIT;
        loadTrendingData();
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
                trendingModelList.add(new DataModel(imageUrl, height, "",false));
            }
            trendingAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
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
            if (isNetworkAvailable()) loadTrendingData();
            scheduleDataRefresh();
        }, REFRESH_INTERVAL);
    }
    @Override
    public void onResume() {
        super.onResume();
        trendingAdapter.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void loadCachedData() {
        ArrayList<DataModel> cachedData = getCachedData();
        if (cachedData != null && !cachedData.isEmpty()) {
            trendingModelList.clear();
            trendingModelList.addAll(cachedData);
            trendingAdapter.notifyDataSetChanged();
            hideEmptyState();
        } else {
            showEmptyState();
        }
    }

    private ArrayList<DataModel> getCachedData() {
        return new ArrayList<>();
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


