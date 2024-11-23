package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private int trendingOffset = 0;
    private final int LIMIT = 20;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        trendingRecyclerView = view.findViewById(R.id.trending_recycler_view);
        emptyText = view.findViewById(R.id.trending_empty_text);

        loadGifs(API.BASE_TRENDING_URL + API.API_KEY + "&limit=" + LIMIT);
        initializeRecyclerView();

        trendingAdapter.setOnItemClickListener(this::onItemClick);
        return view;
    }

    private void initializeRecyclerView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        trendingRecyclerView.setLayoutManager(layoutManager);
        trendingRecyclerView.addItemDecoration(new SpaceItem(4));
        trendingRecyclerView.setHasFixedSize(true);

        trendingAdapter = new DataAdapter(getContext(), trendingModelList);
        trendingRecyclerView.setAdapter(trendingAdapter);

        // Load more Trending GIFs on scroll
        trendingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Kiểm tra nếu không đang tải
                if (!isLoading) {
                    // Lấy layout manager hiện tại
                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) trendingRecyclerView.getLayoutManager();

                    if (layoutManager != null) {
                        // Lấy vị trí các item cuối đang hiển thị
                        int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                        int lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);

                        // Nếu vị trí item cuối >= tổng số item - 2 (để buffer), bắt đầu load thêm
                        if (lastVisibleItemPosition >= trendingModelList.size() - 2) {
                            loadMoreGifs();
                        }
                    }
                }
            }
        });
    }

    // Lấy vị trí lớn nhất trong mảng
    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0 || lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
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

    private void loadGifs(String url) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        isLoading = false;
                        try {
                            JSONArray dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                JSONObject imagesObj = obj.getJSONObject("images");
                                JSONObject downsizedMedium = imagesObj.getJSONObject("downsized_medium");
                                String imageUrl = downsizedMedium.getString("url");
                                int height = downsizedMedium.getInt("height");
                                String title = obj.getString("title");
                                trendingModelList.add(new DataModel(imageUrl, height, title));
                            }
                            for (DataModel dm : trendingModelList){
                                System.out.println(dm);
                            }
                            if (!trendingModelList.isEmpty()) {
                                trendingRecyclerView.setVisibility(View.VISIBLE);
                                emptyText.setVisibility(View.GONE);
                            } else {
                                trendingRecyclerView.setVisibility(View.GONE);
                                emptyText.setVisibility(View.VISIBLE);
                            }

                            trendingAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            isLoading = false;
                            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(getContext()).addToRequestQueue(objectRequest);
    }

    private void loadMoreGifs() {
        if (!isLoading){
            isLoading = true;
            String url = API.BASE_TRENDING_URL + API.API_KEY + "&limit=" + LIMIT + "&offset=" + trendingOffset;
            trendingOffset += LIMIT;
            loadGifs(url);
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent fullView = new Intent(getContext(), FullActivity.class);
        DataModel clickedItem = trendingModelList.get(pos);
        fullView.putExtra("imageFull", clickedItem);
        startActivity(fullView);
    }
}
