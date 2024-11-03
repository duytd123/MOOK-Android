package com.example.testapp;

public class DataModel {
    private String imageUrl;
    private int height;
    //private int width;
    private String name;

    public DataModel(String imageUrl, int height,String name) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.height = height;
      //  this.width = width;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public int getHeight() {
        return height;
    }

//    public int getWidth() {
//        return width;
//    }
//
//    public void setWidth(int width) {
//        this.width = width;
//    }
    public String getName() {
        return name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
