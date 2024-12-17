package com.example.mook21.model;

public class Cover {
    private String thumbnail;
    private String background;

    public Cover(String thumbnail, String background) {
        this.thumbnail = thumbnail;
        this.background = background;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Override
    public String toString() {
        return "Cover{" +
                "thumbnail='" + thumbnail + '\'' +
                ", background='" + background + '\'' +
                '}';
    }
}
