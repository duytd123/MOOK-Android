package com.example.mook21.model;

import java.util.List;

public class Mix {
    private int mixSoundId;
    private int category;
    private String name;
    private Cover cover;
    private List<SoundDetail> sounds;

    public static class Cover {
        private String thumbnail;
        private String background;

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
    }

    public static class SoundDetail {
        private int id;
        private int volume;

        public int getId() {
            return id;
        }

        public int getVolume() {
            return volume;
        }
    }

    // Getters
    public int getMixSoundId() { return mixSoundId; }
    public int getCategory() { return category; }
    public String getName() { return name; }
    public Cover getCover() { return cover; }
    public List<SoundDetail> getSounds() { return sounds; }
}
