package com.simpmangareader.callbacks;

import com.simpmangareader.provider.data.MangaDetail;

public interface NetworkAllMangaFetchSucceed {
    void onComplete(MangaDetail[] result);
}
