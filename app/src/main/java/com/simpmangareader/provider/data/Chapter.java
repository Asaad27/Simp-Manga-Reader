package com.simpmangareader.provider.data;

import java.io.Serializable;

public class Chapter implements Serializable {
    public String id;
    public String chapterNumber;
	public String translatedLanguage;
	public String volume;
	public String title;
	public String hash;
	public String[] data;
	public String CoverBitmapEncoded;
	public String MangaTitle;


	public int getPageCount(){return data.length;}
}
