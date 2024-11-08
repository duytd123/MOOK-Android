package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private final ArrayList<DataModel> ModelList = new ArrayList<>();
    private DataAdapter trendingAdapter;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeViewPagerWithTabs();

    }

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
            }
        });
    }

}
