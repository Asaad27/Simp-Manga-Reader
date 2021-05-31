package com.simpmangareader.provider.mangadex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.simpmangareader.callbacks.NetworkChapterPageSucceed;
import com.simpmangareader.callbacks.NetworkCoverFailed;
import com.simpmangareader.callbacks.NetworkCoverSucceed;
import com.simpmangareader.callbacks.NetworkFailed;
import com.simpmangareader.callbacks.NetworkAllMangaFetchSucceed;

import com.simpmangareader.callbacks.NetworkMangaChaptersSucceed;
import com.simpmangareader.provider.data.Chapter;
import com.simpmangareader.provider.data.Manga;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
								  final NetworkFailed failedCallback,
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
										 final NetworkFailed failedCallback,
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

	private static void GetMangaByURL(NetworkAllMangaFetchSucceed successCallback, NetworkFailed failedCallback, Handler handler, String reqURL) {
		try {
			Manga[] mangas = ParseJsonToManga(getDataFromURL(reqURL));
			handler.post(() -> successCallback.onComplete(mangas));
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			handler.post(() -> failedCallback.onError(e));
		}
	}


	private static Manga[] ParseJsonToManga(String data) throws JSONException {
		//TODO: we can multi thread this function because parsing the manga is independent process
		JSONObject json = new JSONObject(data);
		//start parsing
		JSONArray array = json.getJSONArray("results");
		int count = array.length();
		Manga[] mangas = new Manga[count];
		//CoverScrapper coverScrapper = new CoverScrapper("https://www.anime-planet.com/manga/");
		for (int i = 0; i < count; ++i)
		{
			JSONObject result = array.getJSONObject(i);
			if (!result.getString("result").equals("ok")) continue;
			JSONObject mangaJson = result.getJSONObject("data");
			mangas[i] = new Manga();
			//fill in the fields*
			mangas[i].id = mangaJson.getString("id");
			JSONObject mangaAttributeJson = mangaJson.getJSONObject("attributes");
			mangas[i].title = mangaAttributeJson.getJSONObject("title").getString("en");
			mangas[i].description = (mangaAttributeJson.getJSONObject("description").getString("en"));
			mangas[i].status = (mangaAttributeJson.getString("status"));
			mangas[i].publicationDemographic = (mangaAttributeJson.getString("publicationDemographic"));

		}
		return mangas;
	}


	public static void FetchAllMangaEnglishChapter(final String mangaID,
												   final NetworkMangaChaptersSucceed successCallback,
												   final NetworkFailed failedCallback,
												   final Handler handler)
	{
		//TODO: we can multi thread chapter parsing for the same manga reason
		executor.execute(()->{
			int totalChaptersCount;
			int offset = 0;
			String reqURL = baseURL + "/chapter?manga="+mangaID+"&translatedLanguage[0]=en&limit=100";
			try {
				JSONObject json = new JSONObject(getDataFromURL(reqURL));
				totalChaptersCount = json.getInt("total");
				Chapter[] chapters = new Chapter[totalChaptersCount];
				do {
					JSONArray array = json.getJSONArray("results");
					int fetched = array.length();
					for (int i = 0; i < fetched; ++i)
					{
						JSONObject result = array.getJSONObject(i);
						if (!result.getString("result").equals("ok")) continue;
						JSONObject chapterJson = result.getJSONObject("data");
						chapters[offset + i] = new Chapter();
						chapters[offset + i].id = chapterJson.getString("id");
						JSONObject chapterAttributeJson = chapterJson.getJSONObject("attributes");
						chapters[offset + i].translatedLanguage = "en";
						chapters[offset + i].volume = chapterAttributeJson.getString("volume");
						chapters[offset + i].title = chapterAttributeJson.getString("title");
						chapters[offset + i].chapterNumber = chapterAttributeJson.getString("chapter");
						chapters[offset + i].hash = chapterAttributeJson.getString("hash");
						JSONArray chapterData = chapterAttributeJson.getJSONArray("data");
						int dataSize = chapterData.length();
						chapters[offset + i].data = new String[dataSize];
						for (int j = 0; j < dataSize; ++j)
						{
							chapters[offset + i].data[j] = chapterData.getString(j);
						}
					}
					offset += fetched;
					String nextURL = reqURL + "&offset="+offset;
					json = new JSONObject(getDataFromURL(nextURL));
				}
				while(offset < totalChaptersCount);
				handler.post(() -> successCallback.onComplete(chapters));
			} catch (JSONException | IOException e) {
				handler.post(() -> failedCallback.onError(e));
			}
		});
	}

	private static String getDataFromURL(String reqURL) throws IOException {
		URL url = new URL(reqURL);
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
		String data = "";
		//read data from the stream
		byte[] contents = new byte[1024];
		int bytesRead;
		while ((bytesRead = in.read(contents)) != -1) {
			data += new String(contents, 0, bytesRead);
		}
		in.close();
		return data;
	}

	public static void FetchChapterPictures(Chapter chapter,
											final NetworkChapterPageSucceed successCallback,
											final NetworkFailed failedCallback,
											final Handler handler)
	{
		executor.execute(()->{
			try {
				String reqURL = baseURL + "/at-home/server/" + chapter.id;
				JSONObject json = new JSONObject(getDataFromURL(reqURL));
				String chapterBaseURL = json.getString("baseUrl");
				for (int i = 0; i < chapter.data.length; ++i)
				{
					//multi thread image loading because loading each image is independent from the other
					int finalI = i;
					executor.execute(()->{
						try {
							String pageURL = chapterBaseURL + "/data/"+chapter.hash+"/"+chapter.data[finalI];
							URL url  = new URL(pageURL);
							InputStream in  = url.openConnection().getInputStream();
							Bitmap image = BitmapFactory.decodeStream(in);
							in.close();
							successCallback.onComplete(finalI,image);
						} catch (IOException e) {
							e.printStackTrace();
							//TODO: make new failure callback for image with page index parameter
							handler.post(() -> failedCallback.onError(e));
						}
					});
				}
			}
			catch (JSONException | IOException e)
			{
				handler.post(() -> failedCallback.onError(e));
			}
		});
	}

	public static void GetMangaCover(final String mangaID,
									 final NetworkCoverSucceed successCallback,
									 final NetworkCoverFailed failedCallback,
									 final Handler handler)
	{
		executor.execute(()->{
			try {
				String reqURL = baseURL + "/cover?limit=1&manga[0]=" + mangaID;
				JSONObject json = new JSONObject(getDataFromURL(reqURL));
				String fileName = json.getJSONArray("results").getJSONObject(0).getJSONObject("data").getJSONObject("attributes").getString("fileName");
				String coverURL = "https://uploads.mangadex.org/covers/" + mangaID + "/"+fileName+".256.jpg";
				URL url = new URL(coverURL);
				InputStream in  = url.openConnection().getInputStream();
				Bitmap image = BitmapFactory.decodeStream(in);
				in.close();
				successCallback.onComplete(image);
			}
			catch (JSONException | IOException e)
			{
				handler.post(() -> failedCallback.onError(e));
			}
		});
	}
}
