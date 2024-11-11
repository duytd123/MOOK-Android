package com.example.testapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_gifs")
public class FavoritesGif {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "height")
    private int height;

    public FavoritesGif(String name, String imageUrl, int height, boolean isFavorite) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.height = height;
        this.isFavorite = isFavorite;
    }

    @Ignore
    public FavoritesGif(int id, String name, String imageUrl, int height, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.height = height;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
