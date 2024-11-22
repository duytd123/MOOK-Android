package com.example.testapp;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trending_gifs")
public class TrendingGif {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String url;

    private int height;

    // Constructor
    public TrendingGif(String url, int height) {
        this.url = url;
        this.height = height;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

