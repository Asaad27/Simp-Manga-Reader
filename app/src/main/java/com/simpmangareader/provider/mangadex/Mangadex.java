package com.simpmangareader.provider.mangadex;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.simpmangareader.callbacks.NetworkAllMangaFetchFailed;
import com.simpmangareader.callbacks.NetworkAllMangaFetchSucceed;
import com.simpmangareader.provider.data.MangaDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;

import javax.net.ssl.HttpsURLConnection;


/**
 * This class will be responsible on getting contents from the mangadex api
 * */
public class Mangadex
{
	public static final String baseURL = "https://api.mangadex.org";
	private static Executor executor;

	public static void init(Context ctx, Executor executor)
	{
		Mangadex.executor = executor;
	}

	/**
	*  Fetch limit manga from MangaDex API starting at offset, and display the result into the view
	 *  return true if the request is made, else it returns false
	**/
	static public void FetchManga(int offset, int limit,
								  final NetworkAllMangaFetchSucceed successCallback,
								  final NetworkAllMangaFetchFailed failedCallback,
								  final Handler handler)
	{
		if (limit <= 0) limit = 1;
		else if (limit > 100) limit = 100;
		if (offset < 0) offset = 0;

		int finalOffset = offset;
		int finalLimit = limit;
		executor.execute(() -> {
			String reqURL = baseURL + "/manga?offset="+ finalOffset +"&limit="+ finalLimit;
			GetMangaByURL(successCallback, failedCallback, handler, reqURL);
		});
	}

	static public void SearchMangaByName(int offset, int limit, String name,
										 final NetworkAllMangaFetchSucceed successCallback,
										 final NetworkAllMangaFetchFailed failedCallback,
										 final Handler handler)
	{
		if (limit <= 0) limit = 1;
		else if (limit > 100) limit = 100;
		if (offset < 0) offset = 0;

		int finalOffset = offset;
		int finalLimit = limit;
		executor.execute(() -> {
			String reqURL = baseURL + "/manga?offset="+ finalOffset +"&limit="+ finalLimit+"&title="+name;
			GetMangaByURL(successCallback, failedCallback, handler, reqURL);
		});
	}

	private static void GetMangaByURL(NetworkAllMangaFetchSucceed successCallback, NetworkAllMangaFetchFailed failedCallback, Handler handler, String reqURL) {
		Log.e("getMangaByURL", "reqURL : " + reqURL);
		try {
			URL url = new URL(reqURL);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String data = "";
			//read data from the stream
			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = in.read(contents)) != -1) {
				data += new String(contents, 0, bytesRead);
			}
			MangaDetail[] mangas = ParseJsonToManga(data);

			handler.post(() -> successCallback.onComplete(mangas));
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			handler.post(() -> failedCallback.onError(e));
		}
	}


	private static MangaDetail[] ParseJsonToManga(String data) throws JSONException {
		JSONObject json = new JSONObject(data);
		//start parsing
		int count = json.getInt("limit");
		JSONArray array = json.getJSONArray("results");
		MangaDetail[] mangas = new MangaDetail[count];
		for (int i = 0; i < count; ++i)
		{
			JSONObject result = array.getJSONObject(i);
			if (!result.getString("result").equals("ok")) continue;
			JSONObject mangaJson = result.getJSONObject("data");
			mangas[i] = new MangaDetail();
			//fill in the fields*
			mangas[i].setId(mangaJson.getString("id"));
			JSONObject mangaAttributeJson = mangaJson.getJSONObject("attributes");
			mangas[i].setTitle(mangaAttributeJson.getJSONObject("title").getString("en"));
			mangas[i].setDescription(mangaAttributeJson.getJSONObject("description").getString("en"));
			mangas[i].setThumbnailUrl(mangaAttributeJson.getJSONObject("links").getString("ap"));

			//TODO : get cover
			//mangas[i].ChapterCount = mangaAttributeJson.getInt("lastVolume");
			mangas[i].setStatus(mangaAttributeJson.getString("status"));
			mangas[i].setPublicationDemographic(mangaAttributeJson.getString("publicationDemographic"));

		}
		return mangas;
	}

}
