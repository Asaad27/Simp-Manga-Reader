package com.simpmangareader.provider.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.simpmangareader.util.BitmapConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Manga implements Parcelable {
    public String id;
    public String title;
    public String description;
    public Bitmap cover;
    public String status;
    public String publicationDemographic;
    public Boolean isFav = false;
    public String codedCover;

    public Manga()
    {

    }
    protected Manga(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        cover = in.readParcelable(Bitmap.class.getClassLoader());
        status = in.readString();
        publicationDemographic = in.readString();
        //codedCover = in.readString();
        isFav = in.readBoolean();
    }

    public static final Creator<Manga> CREATOR = new Creator<Manga>() {
        @Override
        public Manga createFromParcel(Parcel in) {
            return new Manga(in);
        }

        @Override
        public Manga[] newArray(int size) {
            return new Manga[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeParcelable(cover, flags);
        //dest.writeString(codedCover);
        dest.writeString(status);
        dest.writeString(publicationDemographic);
        dest.writeBoolean(isFav);
    }
}
