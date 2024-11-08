package com.example.testapp;

public class DataModel {
    private String imageUrl;
    private int height;
    private String name;

    public DataModel(String imageUrl, int height,String name) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DataModel{" +
                "imageUrl='" + imageUrl + '\'' +
                ", height=" + height +
                ", name='" + name + '\'' +
                '}';
    }
}
