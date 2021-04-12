package com.simpmangareader.database;

public class databaseHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MangaReaderContract.MangaEntry.TABLE_NAME + " (" +
                    MangaReaderContract.MangaEntry._ID + " INTEGER PRIMARY KEY," +
                    MangaReaderContract.MangaEntry.COLUMN_NAME_TITLE + " TEXT,";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MangaReaderContract.MangaEntry.TABLE_NAME;
}
