package com.simpmangareader.provider.mangadex;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.simpmangareader.provider.data.Manga;


/**
 * This class will be responsible on getting contents from the mangadex api
 * */
public class Mangadex
{
	public static final String baseURL = "https://api.mangadex.org";
	private static View currentView;
	private static RequestQueue queue;

	public static void init(Context ctx)
	{
		queue = Volley.newRequestQueue(ctx);
	}

	/**
	*  Fetch limit manga from MangaDex API starting at offset, and display the result into the view
	 *  return true if the request is made, else it returns false
	**/
	static public boolean FetchMangaToView(View view, int offset, int limit)
	{
		if (limit <= 0 || limit > 100 || offset < 0) return false;
		currentView = view;
		String reqURL = baseURL + "/manga?offset="+offset+"&limit="+limit;
		Log.e("TAG","reqURL : " + reqURL);
		MangadexMangaArrayRequest mangaArrayRequest = new MangadexMangaArrayRequest(Request.Method.GET,
				reqURL,
				(Response.Listener<Manga[]>) response -> {
					//TODO : feed the manga data to the view
					//NOTE(Mouad): Log for debug only
					Log.e("Success", new Integer(response.length).toString());
				},
				error -> {
					//TODO: report failure
					Log.e("Error", error.getMessage());
				});
		queue.add(mangaArrayRequest);
		return true;
	}

}
