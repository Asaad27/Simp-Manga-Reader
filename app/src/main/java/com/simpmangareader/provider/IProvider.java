package com.simpmangareader.provider;

/*
* interface for manga providers
*/

import com.simpmangareader.provider.data.ChapterDetail;
import com.simpmangareader.provider.data.MangaDetail;

import java.util.Collection;

public interface IProvider {
    Collection<ChapterDetail> GetChaptersDetail(MangaDetail manga);
}
