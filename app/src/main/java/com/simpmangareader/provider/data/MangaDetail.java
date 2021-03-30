package com.simpmangareader.provider.data;

public class MangaDetail {
    String title;
    String url;
    String author;
    String artist;
    String genre;
    MangaState state;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public MangaState getState() {
        return state;
    }

    public void setState(MangaState state) {
        this.state = state;
    }
}
