package com.example.testapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_results")
public class SearchResult {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imageUrl;
    private int height;

    public SearchResult(String imageUrl, int height) {
        this.imageUrl = imageUrl;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
