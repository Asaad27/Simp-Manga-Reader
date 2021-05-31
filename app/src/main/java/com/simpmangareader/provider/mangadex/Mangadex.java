package com.simpmangareader.provider.mangadex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.simpmangareader.callbacks.NetworkChapterPageFailed;
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
 * This class will be responsible on getting contents from the mangadex api.
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
	*  Fetch limit manga from MangaDex API starting at offset, the fetch is done on a separate
	 *  worker thread
	 *  if the the call succeed the the successCallback is invoked, else the failedCallback
	 *  is invoked.
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

	/**
	 * Fetch limit manga that are their name matches "name" from MangaDex API starting at offset,
	 * the fetch is done on a separate worker thread
	 * if the the call succeed the the successCallback is invoked, else the failedCallback
	 * is invoked.
	 * **/
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

	/**
	 * utility function that fetches manga from mangadex API given that the reqURL is well formed
	 * if succeed it invoke successCallback, else it invokes failedCallback.
	 * **/
	private static void GetMangaByURL(NetworkAllMangaFetchSucceed successCallback, NetworkFailed failedCallback, Handler handler, String reqURL) {
		try {
			Manga[] mangas = ParseJsonToManga(getDataFromURL(reqURL));
			handler.post(() -> successCallback.onComplete(mangas));
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			handler.post(() -> failedCallback.onError(e));
		}
	}


	/**
	 * utility function that parses Manga from a Json string, the parsing is multithreaded,
	 * the function wait for all the invoked threads to finish before it return.
	 * **/
	private static Manga[] ParseJsonToManga(String data) throws JSONException {
		JSONObject json = new JSONObject(data);
		//start parsing
		JSONArray array = json.getJSONArray("results");
		int count = array.length();
		Manga[] mangas = new Manga[count];
		//list of the workers that will parse the Manga from json string
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
		//invoke all the workers and wait for them to finish...
		try {
			((ExecutorService)executor).invokeAll(calls);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return mangas;
	}

	/**
	 * Fetches all the english chapters of a manga identified by mangaID in descending order,
	 * the function fetches 25 chapter at a time, if the fetch succeeded it invokes successCallback
	 * else it invoke failedCallback, and continue fetching the remaining chapters.
	 * **/
	public static void FetchAllMangaEnglishChapter(final String mangaID,
												   final NetworkMangaChaptersSucceed successCallback,
												   final NetworkFailed failedCallback,
												   final Handler handler)
	{
		executor.execute(()->{
			int totalChaptersCount;
			int offset = 0;
			String reqURL = baseURL + "/chapter?manga="+mangaID+"&translatedLanguage[0]=en&limit=25&order[chapter]=desc";
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

	/**
	 * utility function to parse Chapter from json string.
	 **/
	private static Chapter[] ParseChapter(JSONArray array, int fetched) throws JSONException {
		Chapter[] chapters = new Chapter[fetched];
		List<Callable<Object>> calls = new ArrayList<>();

		for (int i = 0; i < fetched; ++i) {
			int finalI = i;
			calls.add(Executors.callable(()->{
				try {
					JSONObject result = array.getJSONObject(finalI);
					if (!result.getString("result").equals("ok")) return;
					JSONObject chapterJson = result.getJSONObject("data");
					chapters[finalI] = new Chapter();
					chapters[finalI].id = chapterJson.getString("id");
					JSONObject chapterAttributeJson = chapterJson.getJSONObject("attributes");
					chapters[finalI].translatedLanguage = "en";
					chapters[finalI].volume = chapterAttributeJson.getString("volume");
					chapters[finalI].title = chapterAttributeJson.getString("title");
					chapters[finalI].chapterNumber = chapterAttributeJson.getString("chapter");
					chapters[finalI].hash = chapterAttributeJson.getString("hash");
					JSONArray chapterData = chapterAttributeJson.getJSONArray("data");
					int dataSize = chapterData.length();
					chapters[finalI].data = new String[dataSize];
					for (int j = 0; j < dataSize; ++j) {
						chapters[finalI].data[j] = chapterData.getString(j);
					}
				}
				catch (JSONException e)
				{
					//do nothing
				}
			}));
			//invoke all the workers and wait for them to finish...
			try {
				((ExecutorService)executor).invokeAll(calls);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return chapters;
	}

	/**
	 * utility function to open connection and get data from a url, the data is returned as string.
	 * **/
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

	/**
	 * Fetches page images of a chapter, the images are fetched in parallel, if an image is fetched
	 * the succeedCallback is invoked with the bitmap of the image and the page index, if it failed
	 * failedCallback is invoked, if it's a general error the page index is -1.
	 * **/
	public static void FetchChapterPictures(Chapter chapter,
											final NetworkChapterPageSucceed successCallback,
											final NetworkChapterPageFailed failedCallback,
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
							handler.post(() -> failedCallback.onError(e, finalI));
						}
					});
				}
			}
			catch (JSONException | IOException e)
			{
				handler.post(() -> failedCallback.onError(e, -1));
			}
		});
	}

	/**
	 * Fetches cover of the manga identified by mangaID, if succeed successCallback is invoked with
	 * cover bitmap as parameter, in case of a failure, failedCallback is invoked.
	 * **/
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
