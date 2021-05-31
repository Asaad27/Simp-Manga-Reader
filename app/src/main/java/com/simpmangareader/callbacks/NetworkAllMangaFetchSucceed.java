package com.simpmangareader.callbacks;

import com.simpmangareader.provider.data.Manga;

public interface NetworkAllMangaFetchSucceed {
    void onComplete(Manga[] result);
}
