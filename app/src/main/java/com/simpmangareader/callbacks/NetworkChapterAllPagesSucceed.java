package com.simpmangareader.callbacks;

import android.graphics.Bitmap;

public interface NetworkChapterAllPagesSucceed {
    void onComplete(int pageNumber, Bitmap pageImage);
}
