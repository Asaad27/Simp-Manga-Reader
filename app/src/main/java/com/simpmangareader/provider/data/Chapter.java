package com.simpmangareader.provider.data;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chapter implements Serializable {
    public String id;
    public String chapterNumber;
	public String translatedLanguage;
	public String volume;
	public String title;
	public String hash;
	public String[] data;


	public int getPageCount(){return data.length;}
}
