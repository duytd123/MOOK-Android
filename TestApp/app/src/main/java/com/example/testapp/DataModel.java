package com.example.testapp;

public class DataModel {
    private String imageUrl;
    private int height;
    private String title;

    public DataModel(String imageUrl, int height,String title) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.height = height;
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
        return "DataModel{" +
                "imageUrl='" + imageUrl + '\'' +
                ", height=" + height +
                ", title='" + title + '\'' +
                '}';
    }
}
