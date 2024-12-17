package com.example.mook21.model;

import java.util.List;

public class Mix {
    private int mixSoundId;
    private int category;
    private String name;
    private Cover cover;
    private List<Volume> volumes;

    public Mix(int mixSoundId, int category, String name, Cover cover, List<Volume> volumes) {
        this.mixSoundId = mixSoundId;
        this.category = category;
        this.name = name;
        this.cover = cover;
        this.volumes = volumes;
    }

    @Override
    public String toString() {
        return "Mix{" +
                "mixSoundId=" + mixSoundId +
                ", category=" + category +
                ", name='" + name + '\'' +
                ", cover=" + cover +
                ", volumes=" + volumes +
                '}';
    }

    public int getMixSoundId() {
        return mixSoundId;
    }

    public void setMixSoundId(int mixSoundId) {
        this.mixSoundId = mixSoundId;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volume> volumes) {
        this.volumes = volumes;
    }
}
