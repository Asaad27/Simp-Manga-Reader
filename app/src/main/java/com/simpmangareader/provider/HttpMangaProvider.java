package com.simpmangareader.provider;


import com.simpmangareader.provider.data.MangaDetail;

import java.util.Collection;

public abstract class HttpMangaProvider implements IProvider {
    String baseURL;
    String name;

    abstract public Collection<MangaDetail> GetPopularManga(int pageNumber);
    abstract public Collection<MangaDetail> GetMangaList(int pageNumber);
    abstract public Collection<MangaDetail> SearchForManga(int pageNumber, String query);
}
