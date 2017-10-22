package com.r4sh33d.currencyconverter;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.r4sh33d.currencyconverter.database.CurrencyContract;

import java.util.ArrayList;

/**
 * Created by r4sh33d on 10/20/17.
 */

public class Utils {


    private static final String TAG = "debugtag";

    public static void logMessage(String message){
        Log.d(TAG , message);
    }

    public static ArrayList<Currency> getCurrencciesFromCursor(Cursor cursor) {

        ArrayList<Currency> arrayList = new ArrayList<>();

        int countryNameIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_COUNTRY_NAME);
        int countryShortcode = cursor.getColumnIndex(CurrencyContract.COLUMN_COUNTRY_SHORT_CODE);
        int btcEquivIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_BTC_EQUIVALENT);
        int ethEquivIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_ETH_EQUIVALENT);
        int isEnabledIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_IS_ENABLED);
         logMessage("cursor == null " + (cursor == null));
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                arrayList.add(new Currency(
                        cursor.getString(countryNameIndex),
                        cursor.getString(countryShortcode),
                        cursor.getDouble(btcEquivIndex),
                        cursor.getDouble(ethEquivIndex),
                        cursor.getInt(isEnabledIndex)
                ));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();

        return arrayList;

    }

    public static Cursor makeConversionRatesCursor(SQLiteDatabase database) {

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                CurrencyContract._ID,
                CurrencyContract.COLUMN_COUNTRY_NAME,
                CurrencyContract.COLUMN_COUNTRY_SHORT_CODE,
                CurrencyContract.COLUMN_BTC_EQUIVALENT,
                CurrencyContract.COLUMN_ETH_EQUIVALENT,
                CurrencyContract.COLUMN_IS_ENABLED,
                CurrencyContract.COLUMN_DIALOG_LABEL
        };

        // Filter results WHERE "title" = 'My Title'
/*        String selection = CurrencyContract.COLUMN_IS_ENABLED
                + " = ?";
        String[] selectionArgs = {"1"};*/

        // How you want the results sorted in the resulting Cursor


        Cursor cursor = database.query(
                CurrencyContract.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,
                null // don't filter by row groups
        );

        DatabaseUtils.dumpCursor(cursor);
        return cursor;

    }

}
