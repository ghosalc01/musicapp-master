package com.example.android.musicplayer;

/**
 * Created by sky on 19/07/2017.
 */

public class Music {

    private String title;
    private String file;
    private String displayName;
    private String artists;
    private long durationInMs;


    public Music(String title, String file, String displayName, String artists, long durationInMs) {
        this.title = title;
        this.file = file;
        this.displayName = displayName;
        this.artists = artists;
        this.durationInMs = durationInMs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public long getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(long durationInMs) {
        this.durationInMs = durationInMs;
    }
}
