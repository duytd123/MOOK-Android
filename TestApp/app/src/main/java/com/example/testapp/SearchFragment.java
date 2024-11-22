package com.example.testapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private DataAdapter searchAdapter;
    private ArrayList<DataModel> searchResults = new ArrayList<>();
    private AppDatabase db;
    private ProgressBar loadingSpinner;
    private TextView noResultsMessage;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SearchPrefs";
    private static final String LAST_SEARCH_RESULTS = "lastSearchResults";
    private Handler debounceHandler = new Handler();
    private Runnable debounceRunnable;

    private String lastSearchKeyword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initializeViews(view);
        setUpSearchInput(view);
        loadLastSearchResults();
        return view;
    }

    private void initializeViews(View view) {
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        noResultsMessage = view.findViewById(R.id.noResultsMessage);
        RecyclerView searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        db = AppDatabase.getDatabase(getActivity());
        searchAdapter = new DataAdapter(getActivity(), searchResults, new ViewModelProvider(this).get(FavoritesViewModel.class));
        searchRecyclerView.setAdapter(searchAdapter);
    }

    private void setUpSearchInput(View view) {
        EditText searchInput = view.findViewById(R.id.searchInput);
        lastSearchKeyword = sharedPreferences.getString(LAST_SEARCH_RESULTS, "");
        searchInput.setText(lastSearchKeyword); // Load last search query
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                sharedPreferences.edit().putString(LAST_SEARCH_RESULTS, query).apply(); // Save text immediately
                performSearch(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadLastSearchResults() {
        lastSearchKeyword = sharedPreferences.getString(LAST_SEARCH_RESULTS, "");
        if (!lastSearchKeyword.isEmpty()) {
            searchGifs(lastSearchKeyword);
        } else {
            loadTrendingGifs();
        }
    }

    private void performSearch(String query) {
        if (debounceRunnable != null) {
            debounceHandler.removeCallbacks(debounceRunnable);
        }
        debounceRunnable = () -> {
            if (!query.isEmpty()) {
                searchGifs(query);
            } else {
                loadTrendingGifs();
            }
        };
        debounceHandler.postDelayed(debounceRunnable, 500); // 500ms debounce delay
    }

    private void saveLastSearchQuery(String query) {
        sharedPreferences.edit().putString(LAST_SEARCH_RESULTS, query).apply();
    }

    private void loadTrendingGifs() {
        fetchGifs("https://api.giphy.com/v1/gifs/trending?api_key=" + MainActivity.API_KEY + "&limit=20", false);
    }

    private void searchGifs(String query) {
        fetchGifs("https://api.giphy.com/v1/gifs/search?api_key=" + MainActivity.API_KEY + "&q=" + query + "&limit=20", true);
    }

    private void fetchGifs(String url, boolean isSearch) {
        loadingSpinner.setVisibility(View.VISIBLE);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> handleGifResponse(response, isSearch),
                error -> handleError());
        MySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
    }

    private void handleGifResponse(JSONObject response, boolean isSearch) {
        loadingSpinner.setVisibility(View.GONE);
        try {
            JSONArray dataArray = response.getJSONArray("data");
            searchResults.clear();
            if (dataArray.length() == 0) {
                noResultsMessage.setVisibility(View.VISIBLE);
                return;
            } else {
                noResultsMessage.setVisibility(View.GONE);
            }
            parseGifs(dataArray);
            searchAdapter.notifyDataSetChanged();
            if (isSearch) {
                saveSearchResultsToDatabase();
            }
            ((MainActivity) getActivity()).displaySearchResults(searchResults);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseGifs(JSONArray dataArray) throws JSONException {
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            JSONObject imagesObj = obj.getJSONObject("images");
            JSONObject downsizedMedium = imagesObj.getJSONObject("downsized_medium");
            String imageUrl = downsizedMedium.getString("url");
            int height = downsizedMedium.getInt("height");
            searchResults.add(new DataModel(imageUrl, height, "", false));
        }
    }

    private void handleError() {
        loadingSpinner.setVisibility(View.GONE);
        loadCachedSearchResults(lastSearchKeyword);
    }

    private void saveSearchResultsToDatabase() {
        new Thread(() -> {
            db.searchResultDao().deleteResultsForKeyword(lastSearchKeyword);
            List<SearchResult> searchResultsToCache = new ArrayList<>();
            for (DataModel data : searchResults) {
                searchResultsToCache.add(new SearchResult(data.getImageUrl(), data.getHeight(), lastSearchKeyword));
            }
            db.searchResultDao().insertResults(searchResultsToCache);
        }).start();
    }

    private void loadCachedSearchResults(String keyword) {
        if (keyword.isEmpty()) return;

        new Thread(() -> {
            List<SearchResult> cachedResults = db.searchResultDao().getResultsByKeyword(keyword);
            if (cachedResults != null && !cachedResults.isEmpty()) {
                searchResults.clear();
                for (SearchResult result : cachedResults) {
                    searchResults.add(new DataModel(result.getImageUrl(), result.getHeight(), "", false));
                }
                getActivity().runOnUiThread(() -> {
                    searchAdapter.notifyDataSetChanged();
                    noResultsMessage.setVisibility(searchResults.isEmpty() ? View.VISIBLE : View.GONE);
                });
            } else {
                getActivity().runOnUiThread(() -> noResultsMessage.setVisibility(View.VISIBLE));
            }
        }).start();
    }
}
