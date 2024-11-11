package com.example.testapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    private EditText searchInput;
    private Button searchButton;
    private RecyclerView searchRecyclerView;
    private DataAdapter searchAdapter;
    private ArrayList<DataModel> searchResults;
    private AppDatabase db;

    private ProgressBar loadingSpinner;
    private TextView noResultsMessage;

    // SharedPreferences to store last search results
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SearchPrefs";
    private static final String LAST_SEARCH_RESULTS = "lastSearchResults";
    private FavoritesRepository favoritesRepository;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchButton = view.findViewById(R.id.searchButton);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        noResultsMessage = view.findViewById(R.id.noResultsMessage);
        db = AppDatabase.getDatabase(getActivity());

        FavoritesViewModel favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        searchResults = new ArrayList<>();
        searchAdapter = new DataAdapter(getActivity(), searchResults,favoritesViewModel);
        searchRecyclerView.setAdapter(searchAdapter);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

// Load last search keyword when the fragment is created
        String lastSearchKeyword = sharedPreferences.getString(LAST_SEARCH_RESULTS, "");
        searchInput.setText(lastSearchKeyword);

        // Set click listener on the search button
        searchButton.setOnClickListener(v -> performSearch());

        // Load trending data initially
        loadTrendingGifs();

        // Load cached search results if offline
        loadCachedSearchResults();

        return view;
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        if (!query.isEmpty()) {
            // Save the last search keyword
            sharedPreferences.edit().putString(LAST_SEARCH_RESULTS, query).apply();
            searchGifs(query);
        } else {
            loadTrendingGifs();
        }
    }

    private void loadTrendingGifs() {
        loadingSpinner.setVisibility(View.VISIBLE); // Show loading spinner
        String url = "https://api.giphy.com/v1/gifs/trending?api_key=" + MainActivity.API_KEY + "&limit=20";

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingSpinner.setVisibility(View.GONE); // Hide loading spinner
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            searchResults.clear(); // Clear previous results

                            if (dataArray.length() == 0) {
                                noResultsMessage.setVisibility(View.VISIBLE); // Show message if no results
                                return;
                            } else {
                                noResultsMessage.setVisibility(View.GONE); // Hide message if results found
                            }

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                JSONObject imagesObj = obj.getJSONObject("images");
                                JSONObject downsizedMedium = imagesObj.getJSONObject("downsized_medium");
                                String imageUrl = downsizedMedium.getString("url");
                                int height = downsizedMedium.getInt("height");

                                searchResults.add(new DataModel(imageUrl, height, "",false));
                            }

                            searchAdapter.notifyDataSetChanged();

                            // Call method to update MainActivity with new search results
                            ((MainActivity) getActivity()).displaySearchResults(searchResults);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingSpinner.setVisibility(View.GONE); // Hide loading spinner
                        // Handle error and try to load cached results
                        loadCachedSearchResults();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
    }


    private void searchGifs(String query) {
        loadingSpinner.setVisibility(View.VISIBLE); // Show loading spinner
        String url = "https://api.giphy.com/v1/gifs/search?api_key=" + MainActivity.API_KEY + "&q=" + query + "&limit=20";

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingSpinner.setVisibility(View.GONE);
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            searchResults.clear(); // Clear previous results

                            if (dataArray.length() == 0) {
                                noResultsMessage.setVisibility(View.VISIBLE);
                                return;
                            } else {
                                noResultsMessage.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                JSONObject imagesObj = obj.getJSONObject("images");
                                JSONObject downsizedMedium = imagesObj.getJSONObject("downsized_medium");
                                String imageUrl = downsizedMedium.getString("url");
                                int height = downsizedMedium.getInt("height");

                                searchResults.add(new DataModel(imageUrl, height, "",false));
                            }

                            searchAdapter.notifyDataSetChanged();

                            // Call the correct method to save search results to the Room database
                            saveSearchResultsToDatabase(searchResults);

                            // Call method to update MainActivity with new search results
                            ((MainActivity) getActivity()).displaySearchResults(searchResults);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingSpinner.setVisibility(View.GONE);
                        // Load cached results from Room database if offline
                        loadCachedSearchResults();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
    }

    private void saveSearchResultsToDatabase(ArrayList<DataModel> results) {
        // Convert DataModel to SearchResult and insert into the database
        List<SearchResult> searchResultsToCache = new ArrayList<>();
        for (DataModel data : results) {
            searchResultsToCache.add(new SearchResult(data.getImageUrl(), data.getHeight()));
        }
        new Thread(() -> db.searchResultDao().insertResults(searchResultsToCache)).start();
    }

    private void loadCachedSearchResults() {
        new Thread(() -> {
            List<SearchResult> cachedResults = db.searchResultDao().getAllResults();
            if (cachedResults != null && !cachedResults.isEmpty()) {
                searchResults.clear();
                for (SearchResult result : cachedResults) {
                    searchResults.add(new DataModel(result.getImageUrl(), result.getHeight(), "",false));
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
