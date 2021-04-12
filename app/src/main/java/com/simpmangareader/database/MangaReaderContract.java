package com.simpmangareader.database;

import android.provider.BaseColumns;

public final class MangaReaderContract {
    private MangaReaderContract(){
    }

    public static class MangaEntry implements BaseColumns {
        public static final String TABLE_NAME = "manga";
        public static final String COLUMN_NAME_TITLE = "title";
    }

}
