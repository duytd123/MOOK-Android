package com.example.mook21.modelDAO;

public class PlayingSound {
    private int id;
    private String name;
    private String fileName;
    private String icon;
    private int volume;

    public PlayingSound(int id, String name, String fileName, String icon, int volume) {
        this.id = id;
        this.name = name;
        this.fileName = fileName;
        this.icon = icon;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "PlayingSound{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", icon='" + icon + '\'' +
                ", volume=" + volume +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
