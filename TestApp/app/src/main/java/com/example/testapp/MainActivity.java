package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DataAdapter.OnItemClickListener {

    public static final String API_KEY = "OgyK6RaYJRt81BKJDvHu36TQfMrjYUi2";
    public static final String BASE_URL = "https://api.giphy.com/v1/gifs/trending?api_key=";
    public static final String CACHE_PREFS = "GifCache";

    private RecyclerView recyclerView;
    private final ArrayList<DataModel> modelList = new ArrayList<>();
    private DataAdapter trendingAdapter;
    private int trendingOffset = 0;
    private boolean isLoading = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeRecyclerView();
        initializeViewPagerWithTabs();

        if (isInternetAvailable(this)) {
            Toast.makeText(this, "Loading GIFs online", Toast.LENGTH_SHORT).show();
            loadGifs(BASE_URL + API_KEY + "&limit=20");
        } else {
            Toast.makeText(this, "No internet connection. Loading cached GIFs.", Toast.LENGTH_SHORT).show();
            loadCachedGifs();
        }
    }

    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItem(5));
        recyclerView.setHasFixedSize(true);

        FavoritesViewModel favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        trendingAdapter = new DataAdapter(MainActivity.this, modelList, favoritesViewModel);
        recyclerView.setAdapter(trendingAdapter);
        trendingAdapter.setOnItemClickListener(this::onItemClick);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions);

                    if (lastVisibleItem + 5 >= modelList.size()) {
                        Log.d("LoadMore", "Loading more GIFs...");
                        loadMoreGifs();
                    }
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void displaySearchResults(ArrayList<DataModel> searchResults) {
        modelList.clear();
        modelList.addAll(searchResults);
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

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0 || lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    private void fetchGifs(String url, boolean isLoadMore) {
        if (isLoading) return;

        isLoading = true;
        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        ArrayList<DataModel> newItems = new ArrayList<>();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            JSONObject imagesObj = obj.getJSONObject("images");
                            JSONObject downsizedMedium = imagesObj.getJSONObject("downsized_medium");
                            String imageUrl = downsizedMedium.getString("url");
                            int height = downsizedMedium.getInt("height");

                            newItems.add(new DataModel(imageUrl, height, "", false));
                        }

                        downloadAndCacheGifs(newItems);
                        modelList.addAll(newItems);
                        if (isLoadMore) {
                            trendingOffset += newItems.size();
                        }

                        trendingAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("FetchGIFs", "JSON Parsing error: " + e.getMessage());
                    } finally {
                        isLoading = false;
                    }
                },
                error -> {
                    Log.e("FetchGIFs", "Error fetching data: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "Error loading GIFs", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });

        MySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    private void loadGifs(String url) {
        fetchGifs(url, false);
    }

    private void loadMoreGifs() {
        if (trendingOffset >= 100) return;
        String url = BASE_URL + API_KEY + "&limit=10&offset=" + trendingOffset;
        fetchGifs(url, true);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCachedGifs() {
        File cacheDir = new File(getApplicationContext().getFilesDir(), "gif_cache");
        File[] gifFiles = cacheDir.listFiles();
        SharedPreferences prefs = getSharedPreferences(CACHE_PREFS, MODE_PRIVATE);

        if (gifFiles != null && gifFiles.length > 0) {
            modelList.clear();
            for (File gifFile : gifFiles) {
                String metadata = prefs.getString(gifFile.getAbsolutePath(), null);
                if (metadata != null) {
                    String[] parts = metadata.split(",");
                    String name = parts[0];
                    int height = Integer.parseInt(parts[1]);
                    modelList.add(new DataModel(gifFile.getAbsolutePath(), height, name, false));
                } else {
                    modelList.add(new DataModel(gifFile.getAbsolutePath(), 0, "", false));
                }
            }
            trendingAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No cached GIFs available.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private void downloadAndCacheGifs(ArrayList<DataModel> gifs) {
        File cacheDir = new File(getApplicationContext().getFilesDir(), "gif_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        SharedPreferences prefs = getSharedPreferences(CACHE_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (DataModel gif : gifs) {
            String imageUrl = gif.getImageUrl();
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            File gifFile = new File(cacheDir, fileName);

            if (gifFile.exists()) continue;

            Glide.with(this)
                    .downloadOnly()
                    .load(imageUrl)
                    .into(new CustomTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @NonNull com.bumptech.glide.request.transition.Transition<? super File> transition) {
                            try {
                                try (FileInputStream inputStream = new FileInputStream(resource);
                                     FileOutputStream outputStream = new FileOutputStream(gifFile)) {
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = inputStream.read(buffer)) > 0) {
                                        outputStream.write(buffer, 0, length);
                                    }
                                    String metadata = gif.getName() + "," + gif.getHeight();
                                    editor.putString(gifFile.getAbsolutePath(), metadata);
                                    editor.apply();
                                }

                                Log.d("CacheGIF", "GIF saved: " + gifFile.getAbsolutePath());
                            } catch (Exception e) {
                                Log.e("CacheGIF", "Error caching GIF: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent fullView = new Intent(this, FullActivity.class);
        DataModel clickedItem = modelList.get(pos);
        fullView.putExtra("imageUrl", clickedItem.getImageUrl());
        startActivity(fullView);
    }
}
