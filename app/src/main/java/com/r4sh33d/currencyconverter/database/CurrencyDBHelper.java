package com.r4sh33d.currencyconverter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by r4sh33d on 10/17/17.
 */

public class CurrencyDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CurrencyContract.TABLE_NAME + " (" +
                    CurrencyContract._ID + " INTEGER PRIMARY KEY," +
                    CurrencyContract.COLUMN_COUNTRY_NAME + " TEXT," +
                    CurrencyContract.COLUMN_COUNTRY_SHORT_CODE + " TEXT," +
                    CurrencyContract.COLUMN_BTC_EQUIVALENT + " DOUBLE," +
                    CurrencyContract.COLUMN_ETH_EQUIVALENT + " DOUBLE," +
                    CurrencyContract.COLUMN_IS_ENABLED + " BOOLEAN)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CurrencyContract.TABLE_NAME;

    public CurrencyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
