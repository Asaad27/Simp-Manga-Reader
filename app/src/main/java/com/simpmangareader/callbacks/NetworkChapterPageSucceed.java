package com.simpmangareader.callbacks;

import android.graphics.Bitmap;

public interface NetworkChapterPageSucceed {
    void onComplete(int pageNumber, Bitmap pageImage);
}
