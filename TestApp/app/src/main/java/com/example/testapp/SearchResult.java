package com.example.testapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "search_result")
public class SearchResult {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "height")
    private int height;

    @ColumnInfo(name = "title")
    private String title;

    public SearchResult(String imageUrl, int height, String title) {
        this.imageUrl = imageUrl;
        this.height = height;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", height=" + height +
                ", title='" + title + '\'' +
                '}';
    }
}
