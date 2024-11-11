package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DataAdapter.OnItemClickListener {

    public static final String API_KEY = "OgyK6RaYJRt81BKJDvHu36TQfMrjYUi2";//OgyK6RaYJRt81BKJDvHu36TQfMrjYUi2
    public static final String BASE_URL = "https://api.giphy.com/v1/gifs/trending?api_key=";

    private RecyclerView recyclerView;
    private final ArrayList<DataModel> ModelList = new ArrayList<>();
    private DataAdapter trendingAdapter;
    private int trendingOffset = 0;
    private final int LIMIT = 20;

    private FavoritesRepository favoritesRepository;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeRecyclerView();
        initializeViewPagerWithTabs();
        loadGifs(BASE_URL + API_KEY + "&limit=" + LIMIT);
    }
    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItem(5));
        recyclerView.setHasFixedSize(true);
        FavoritesViewModel favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        trendingAdapter = new DataAdapter(MainActivity.this, ModelList,favoritesViewModel);
        recyclerView.setAdapter(trendingAdapter);
        trendingAdapter.setOnItemClickListener(this::onItemClick);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    loadMoreGifs();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void displaySearchResults(ArrayList<DataModel> searchResults) {
        ModelList.clear();
        ModelList.addAll(searchResults);
        trendingAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void initializeViewPagerWithTabs() {
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    @SuppressLint("InflateParams") View customTabView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                    ImageView tabIcon = customTabView.findViewById(R.id.tab_icon);
                    TextView tabText = customTabView.findViewById(R.id.tab_text);

                    switch (position) {
                        case 0:
                            tabIcon.setImageResource(R.drawable.ic_search);
                            tabText.setText("Search");
                            break;
                        case 1:
                            tabIcon.setImageResource(R.drawable.ic_trending);
                            tabText.setText("Trending");
                            break;
                        case 2:
                            tabIcon.setImageResource(R.drawable.ic_favorites);
                            tabText.setText("Favorites");
                            break;
                    }
                    tab.setCustomView(customTabView);
                }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (recyclerView != null) {
                    recyclerView.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                }

                ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
                if (position == 0) {
                    layoutParams.height = 200;
                } else {
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                viewPager.setLayoutParams(layoutParams);
            }
        });
    }

    private void loadGifs(String url) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                JSONObject imagesObj = obj.getJSONObject("images");
                                JSONObject downsizedMedium = imagesObj.getJSONObject("downsized_medium");
                                String imageUrl = downsizedMedium.getString("url");
                                int height = downsizedMedium.getInt("height");

                                ModelList.add(new DataModel(imageUrl, height, "",false));
                            }
                            trendingAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    private void loadMoreGifs() {
        String url = BASE_URL + API_KEY + "&limit=" + LIMIT + "&offset=" + trendingOffset;
        trendingOffset += LIMIT;
        loadGifs(url);
    }

    @Override
    public void onItemClick(int pos) {
        Intent fullView = new Intent(this, FullActivity.class);
        DataModel clickedItem = ModelList.get(pos);
        fullView.putExtra("imageUrl", clickedItem.getImageUrl());
        startActivity(fullView);
    }
}
