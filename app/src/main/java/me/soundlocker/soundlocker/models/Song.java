package me.soundlocker.soundlocker.models;

import java.net.URL;

public class Song {
    private String songName;
    private URL previewUrl;
    private URL imageUrl;

    public Song(String songName, URL previewUrl, URL imageUrl) {
        this.songName = songName;
        this.previewUrl = previewUrl;
        this.imageUrl = imageUrl;
    }

    public String getSongName() {
        return songName;
    }

    public URL getPreviewUrl() {
        return previewUrl;
    }

    public URL getImageUrl() { return imageUrl; }
}