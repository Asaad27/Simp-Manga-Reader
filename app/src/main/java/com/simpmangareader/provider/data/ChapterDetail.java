package com.simpmangareader.provider.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChapterDetail implements Parcelable {
    String url;
    String title;
    float number;
    Date upload;
    List<String> pictureLinks = new ArrayList<>();

    public ChapterDetail(String title, float number) {
        this.title = title;
        this.number = number;
    }

    protected ChapterDetail(Parcel in) {
        url = in.readString();
        title = in.readString();
        number = in.readFloat();
        pictureLinks = in.createStringArrayList();
    }

    public static final Creator<ChapterDetail> CREATOR = new Creator<ChapterDetail>() {
        @Override
        public ChapterDetail createFromParcel(Parcel in) {
            return new ChapterDetail(in);
        }

        @Override
        public ChapterDetail[] newArray(int size) {
            return new ChapterDetail[size];
        }
    };

    public List<String> getPictureLinks() {
        return pictureLinks;
    }

    public void setPictureLinks(List<String> pictureLinks) {
        this.pictureLinks = pictureLinks;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public Date getUpload() {
        return upload;
    }

    public void setUpload(Date upload) {
        this.upload = upload;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeFloat(number);
        dest.writeStringList(pictureLinks);
    }
}
