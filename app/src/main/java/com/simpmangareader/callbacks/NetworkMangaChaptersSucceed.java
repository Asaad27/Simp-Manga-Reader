package com.simpmangareader.callbacks;

import com.simpmangareader.provider.data.Chapter;

public interface NetworkMangaChaptersSucceed {
    void onComplete(Chapter[] result, int offset, int totalSize);
}
