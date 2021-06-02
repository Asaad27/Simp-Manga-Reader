package com.simpmangareader.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simpmangareader.provider.data.Chapter;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.util.BitmapConverter;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesHelper {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferencesHelper sharedPreferencesHelper;

    public static String favPreference_file_key = "simpmangareader.favPreference_file_key" ;
    public static String recPreference_file_key = "simpmangareader.recPreference_file_key" ;
    private final String favSharePreferenceKey = "favorite_manga";
    private final String recSharePreferenceKey = "recent_chapters";
    private final Gson gson = new Gson();
    private final int maxRecentSize = 5;
    public static ArrayList<Manga> favs;


    //Singleton design pattern
    private SharedPreferencesHelper(){
    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (sharedPreferencesHelper == null) {
            sharedPreferencesHelper = new SharedPreferencesHelper();

            sharedPreferences = context.getSharedPreferences(favPreference_file_key, Context.MODE_PRIVATE);
        }
        return sharedPreferencesHelper;
    }

    //choose between recent and bookmarks saved datas
    public void setSharedPreferencesHelper(String key, Context context){
        sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
    }


    public void AddOrRemove(Manga manga){
        String json = sharedPreferences.getString(favSharePreferenceKey, "");
        List<Manga> mangas = new ArrayList<>();
        Boolean isFav = false;
        if(!json.isEmpty())
            mangas = gson.fromJson(json, new TypeToken<List<Manga>>(){}.getType());
        for (Manga m : mangas){
            if (m.id.equals(manga.id)){
                isFav = true;
                mangas.remove(m);
                manga.isFav = false;  //remove manga from favs
                break;
            }
        }
        if (!isFav) {
            manga.codedCover = BitmapConverter.getStringFromBitmap(manga.cover);
            mangas.add(manga);
            manga.isFav = true;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(favSharePreferenceKey, gson.toJson(mangas));
        editor.apply();
    }

    public void AddOrRemove(Chapter chapter){
        String json = sharedPreferences.getString(recSharePreferenceKey, "");
        List<Chapter> chapters = new ArrayList<>();
        Boolean isRec = false;
        if(!json.isEmpty())
            chapters = gson.fromJson(json, new TypeToken<List<Chapter>>(){}.getType());
        for (Chapter c : chapters){
            if (c.id.equals(chapter.id)){
                isRec = true;
                break;
            }
        }
        if (!isRec) {
                chapters.add(0, chapter);
                if(chapters.size() > maxRecentSize)
                    chapters.remove(chapters.size() - 1);
        }
        else{
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(recSharePreferenceKey, gson.toJson(chapters));
        editor.apply();
    }

    public Boolean isFav(Manga manga){
        String json = sharedPreferences.getString(favSharePreferenceKey, "");
        List<Manga> mangas = new ArrayList<>();

        if (!json.isEmpty())
            mangas = gson.fromJson(json, new TypeToken<List<Manga>>() {
            }.getType());
        for (Manga m : mangas)
            if (m.id.equals(manga.id)){
                return true;
            }
        return false;
    }

    public Boolean isRec(Chapter chapter){
        String json = sharedPreferences.getString(recSharePreferenceKey, "");
        List<Chapter> chapters = new ArrayList<>();

        if (!json.isEmpty())
            chapters = gson.fromJson(json, new TypeToken<List<Chapter>>() {
            }.getType());
        for (Chapter c : chapters)
            if (c.id.equals(chapter.id)){
                return true;
            }
        return false;
    }

    public ArrayList<Manga> getAllFavs() {

        String json = sharedPreferences.getString(favSharePreferenceKey, "");
        ArrayList<Manga> mangas = new ArrayList<>();
        if (!json.isEmpty()) {
            mangas = gson.fromJson(json, new TypeToken<List<Manga>>() {
            }.getType());

        }
        //fix bitmap loading issue
        for (Manga m: mangas) {
            m.cover = BitmapConverter.getBitmapFromString(m.codedCover);
        }
        favs = mangas;

        return mangas;
    }

    public Chapter[] getAllRecs() {

        String json = sharedPreferences.getString(recSharePreferenceKey, "");
        ArrayList<Chapter> chapters = new ArrayList<>();
        if (!json.isEmpty()) {
            chapters = gson.fromJson(json, new TypeToken<List<Chapter>>() {
            }.getType());

        }

        Chapter[] resChapters = new Chapter[chapters.size()];
        for (int i = 0; i < chapters.size(); i++)
            resChapters[i] = chapters.get(i);
        return resChapters;
    }

    public void removeHistory(){
        List<Chapter> chapters = new ArrayList<>();
        String json = sharedPreferences.getString(recSharePreferenceKey, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(recSharePreferenceKey, gson.toJson(chapters));
        editor.apply();
    }

}
