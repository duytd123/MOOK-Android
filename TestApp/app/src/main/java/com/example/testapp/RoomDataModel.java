package com.example.testapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_gifs")
public class RoomDataModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imageUrl;
    private int height;
    private String name;
    private boolean isFavorite;

    public RoomDataModel(String imageUrl, int height, String name, boolean isFavorite) {
        this.imageUrl = imageUrl;
        this.height = height;
        this.name = name;
        this.isFavorite = isFavorite;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
