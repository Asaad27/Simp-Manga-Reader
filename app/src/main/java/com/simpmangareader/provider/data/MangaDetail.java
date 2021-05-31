package com.simpmangareader.provider.data;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class MangaDetail {
    private String id;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl; // we will use this to get a cover picture for now as the API doesn't support covers yet
    private int thumbnail;
    public Bitmap cover;
    private String author;
    private String artist;
    private String category;
    public String status;
    public String publicationDemographic;
    private ArrayList<ChapterDetail> chapters = new ArrayList<>();
    private MangaState state;



    public MangaDetail(String title, int thumbnail) {
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public MangaDetail(String title, String thumbnailUrl, ArrayList<ChapterDetail> chapters) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.chapters = chapters;
    }

    public MangaDetail(String title, int thumbnail, ArrayList<ChapterDetail> chapters) {
        this.title = title;
        this.chapters = chapters;
        this.thumbnail = thumbnail;
    }

    public MangaDetail() {

    }

    public ArrayList<ChapterDetail> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<ChapterDetail> chapters) {
        this.chapters = chapters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public MangaState getState() {
        return state;
    }

    public void setState(MangaState state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublicationDemographic() {
        return publicationDemographic;
    }

    public void setPublicationDemographic(String publicationDemographic) {
        this.publicationDemographic = publicationDemographic;
    }
}
