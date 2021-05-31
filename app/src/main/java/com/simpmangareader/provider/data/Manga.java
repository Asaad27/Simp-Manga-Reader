package com.simpmangareader.provider.data;

import android.graphics.Bitmap;
import java.io.Serializable;

public class Manga implements Serializable {
    public String id;
    public String title;
    public String description;
    public Bitmap cover;
    public String status;
    public String publicationDemographic;
}
