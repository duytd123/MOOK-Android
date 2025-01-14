package com.example.testapp;

public class DataModel {
    private String imageUrl;
    private int height;
    private String name;
    private boolean isFavorite;

    public DataModel(String imageUrl, int height, String name, boolean isFavorite) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.height = height;
        this.isFavorite = isFavorite;
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