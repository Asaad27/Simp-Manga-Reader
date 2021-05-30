package com.simpmangareader.provider.mangadex;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.simpmangareader.provider.data.Manga;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

class MangadexMangaArrayRequest extends Request<Manga[]> {
    private final Response.Listener<Manga[]> listener;

    public MangadexMangaArrayRequest(int methode, String url, Response.Listener<Manga[]> listener, Response.ErrorListener errorListener) {
        super(methode, url, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<Manga[]> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(
                            response.data,
                            HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONObject(jsonString);
            //start parsing
            int count = json.getInt("limit");
            Manga[] mangas = new Manga[count];
            JSONArray array = json.getJSONArray("results");
            for (int i = 0; i < count; ++i){
                JSONObject result = array.getJSONObject(i);
                if (!result.getString("result").equals("ok")) continue;
                JSONObject mangaJson = result.getJSONObject("data");
                mangas[i] = new Manga();
                //fill in the fields
                mangas[i].id = mangaJson.getString("id");
                JSONObject mangaAttributeJson = mangaJson.getJSONObject("attributes");
                mangas[i].title = mangaAttributeJson.getJSONObject("title").getString("en");
                mangas[i].description = mangaAttributeJson.getJSONObject("description").getString("en");
                mangas[i].link_ap = mangaAttributeJson.getJSONObject("links").getString("ap");
                //TODO : get cover
                //mangas[i].ChapterCount = mangaAttributeJson.getInt("lastVolume");
                mangas[i].status = mangaAttributeJson.getString("status");
                mangas[i].publicationDemographic = mangaAttributeJson.getString("publicationDemographic");
            }
            return Response.success(
                    mangas, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Manga[] response) {
        listener.onResponse(response);
    }
}