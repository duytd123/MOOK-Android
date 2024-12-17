package com.example.mook21.model;

public class Volume {
    private int id;
    private int volume;

    public Volume(int id, int volume) {
        this.id = id;
        this.volume = volume;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "Volume{" +
                "id=" + id +
                ", volume=" + volume +
                '}';
    }
}
