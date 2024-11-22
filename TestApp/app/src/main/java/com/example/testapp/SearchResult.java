package com.example.testapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_results")
public class SearchResult {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imageUrl;
    private int height;
    private String searchKeyword;

    public SearchResult(String imageUrl, int height, String searchKeyword) {
        this.imageUrl = imageUrl;
        this.height = height;
        this.searchKeyword = searchKeyword;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }
}
