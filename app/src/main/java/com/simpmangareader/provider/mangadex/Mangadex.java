package com.simpmangareader.provider.mangadex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
		JSONObject json = new JSONObject(data);
		//start parsing
		JSONArray array = json.getJSONArray("results");
		int count = array.length();
		Manga[] mangas = new Manga[count];
		ExecutorService exec =  Executors.newFixedThreadPool(4);
		List<Callable<Object>> calls = new ArrayList<>();
		for (int i = 0; i < count; ++i)
		{
			int finalI = i;
			calls.add(Executors.callable(()->{
				try {
					JSONObject result = array.getJSONObject(finalI);
					if (!result.getString("result").equals("ok"))
					{
						return;
					}
					JSONObject mangaJson = result.getJSONObject("data");
					mangas[finalI] = new Manga();
					//fill in the fields*
					mangas[finalI].id = mangaJson.getString("id");
					JSONObject mangaAttributeJson = mangaJson.getJSONObject("attributes");
					mangas[finalI].title = mangaAttributeJson.getJSONObject("title").getString("en");
					mangas[finalI].description = (mangaAttributeJson.getJSONObject("description").getString("en"));
					mangas[finalI].status = (mangaAttributeJson.getString("status"));
					mangas[finalI].publicationDemographic = (mangaAttributeJson.getString("publicationDemographic"));
				}
				catch (JSONException e)
				{
					//do nothing
				}
			}));
		}
		try {
			exec.invokeAll(calls);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return mangas;
	}


	public static void FetchAllMangaEnglishChapter(final String mangaID,
												   final NetworkMangaChaptersSucceed successCallback,
												   final NetworkFailed failedCallback,
												   final Handler handler)
	{
		executor.execute(()->{
			int totalChaptersCount;
			int offset = 0;
			String reqURL = baseURL + "/chapter?manga="+mangaID+"&translatedLanguage[0]=en&limit=25";
			try {
				JSONObject json = new JSONObject(getDataFromURL(reqURL));
				totalChaptersCount = json.getInt("total");
				//Chapter[] chapters = new Chapter[totalChaptersCount];
				do {
					JSONArray array = json.getJSONArray("results");
					int fetched = array.length();
					int finalOffset = offset;
					executor.execute(()->{
						try {
							Chapter[] chapters = ParseChapter(array, fetched);
							handler.post(() -> successCallback.onComplete(chapters, finalOffset, totalChaptersCount));
						}
						catch (JSONException e) {
							handler.post(() -> failedCallback.onError(e));
						}
					});
					offset += fetched;
					String nextURL = reqURL + "&offset="+offset;
					json = new JSONObject(getDataFromURL(nextURL));
				}
				while(offset < totalChaptersCount);
			} catch (JSONException | IOException e) {
				handler.post(() -> failedCallback.onError(e));
			}
		});
	}

	private static Chapter[] ParseChapter(JSONArray array, int fetched) throws JSONException {
		Chapter[] chapters = new Chapter[fetched];
		for (int i = 0; i < fetched; ++i) {
			JSONObject result = array.getJSONObject(i);
			if (!result.getString("result").equals("ok")) continue;
			JSONObject chapterJson = result.getJSONObject("data");
			chapters[i] = new Chapter();
			chapters[i].id = chapterJson.getString("id");
			JSONObject chapterAttributeJson = chapterJson.getJSONObject("attributes");
			chapters[i].translatedLanguage = "en";
			chapters[i].volume = chapterAttributeJson.getString("volume");
			chapters[i].title = chapterAttributeJson.getString("title");
			chapters[i].chapterNumber = chapterAttributeJson.getString("chapter");
			chapters[i].hash = chapterAttributeJson.getString("hash");
			JSONArray chapterData = chapterAttributeJson.getJSONArray("data");
			int dataSize = chapterData.length();
			chapters[i].data = new String[dataSize];
			for (int j = 0; j < dataSize; ++j) {
				chapters[i].data[j] = chapterData.getString(j);
			}
		}
		return chapters;
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
