package com.simpmangareader.provider.data;

import android.graphics.Bitmap;


import java.io.IOException;

public class Manga
{
    public String id;
    public String title;
    public String description;
    public String link_ap; // we will use this to get a cover picture for now as the API doesn't support covers yet
	public Bitmap cover;
    public int ChapterCount;
	public String status;
	public String publicationDemographic;
}
