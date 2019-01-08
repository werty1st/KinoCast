package com.ov3rk1ll.kinocast.data;

public class Episode {
    private int episode;
    private String season;

    public Episode(int episode, String season) {
        this.episode = episode;
        this.season = season;
    }

    @Override
    public String toString() {
        return String.format("S%sE%02d", season, episode);
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
