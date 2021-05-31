package com.simpmangareader.provider.mangadex;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CoverScrapper {
    private String baseUrl; //website url
    private Document doc;

    private  String className = "mainEntry";

    public CoverScrapper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String Scrap(String apiUrl)
    {
        String url = baseUrl + apiUrl;

        String link = "";
        {
            try {
                doc = Jsoup.connect(url).get();
                Elements elements = doc.getElementsByClass(className).get(0).getElementsByTag("img");
                link = elements.get(0).absUrl("src");

                System.out.println("link : " + link);

            } catch (IOException e) {
                System.out.println("error while scrapping url : " + e);
                e.printStackTrace();
            }
            catch (IndexOutOfBoundsException e){
                System.out.println("indexOutOfbound : " + e);
                e.printStackTrace();
            }
        }

        return link;
    }


    public void setClassName(String className) {
        this.className = className;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
