package com.r4sh33d.currencyconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.r4sh33d.currencyconverter.database.CurrencyContract;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by r4sh33d on 10/20/17.
 *
 */

public class Utils {

    private static final String TAG = "debugtag";
    static String CURRENCY_INTENT_KEY = "currency_intent_key";

    static void logMessage(String message) {
        Log.d(TAG, message);
    }

    static ArrayList<Currency> getCurrencyList(Cursor cursor,
                                               HashMap<String, Integer> shortCodeFlagMap,
                                               HashMap<String, Integer> shortCodeCurrencySymbolMap) {
        ArrayList<Currency> arrayList = new ArrayList<>();
        int countryNameIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_COUNTRY_NAME);
        int countryShortcode = cursor.getColumnIndex(CurrencyContract.COLUMN_COUNTRY_SHORT_CODE);
        int btcEquivIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_BTC_EQUIVALENT);
        int ethEquivIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_ETH_EQUIVALENT);
        int isEnabledIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_IS_ENABLED);
        logMessage("cursor == null " + (cursor == null));
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                String shortCode = cursor.getString(countryShortcode);
                arrayList.add(new Currency(
                        cursor.getString(countryNameIndex),
                        shortCode,
                        cursor.getDouble(btcEquivIndex),
                        cursor.getDouble(ethEquivIndex),
                        cursor.getInt(isEnabledIndex),
                        shortCodeFlagMap.get(shortCode),
                        shortCodeCurrencySymbolMap.get(shortCode)
                ));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;

    }

    public static Cursor makeCreateCardDialogCursor(SQLiteDatabase database) {

        String[] projection = {
                CurrencyContract._ID,
                CurrencyContract.COLUMN_DIALOG_LABEL,
                CurrencyContract.COLUMN_IS_ENABLED
        };


        Cursor cursor = database.query(
                CurrencyContract.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null // don't filter by row groups
        );

        DatabaseUtils.dumpCursor(cursor);
        return cursor;

    }


    public static Cursor makeConversionRatesCursor(SQLiteDatabase database) {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.

        String[] projection = {
                CurrencyContract.COLUMN_COUNTRY_NAME,
                CurrencyContract.COLUMN_COUNTRY_SHORT_CODE,
                CurrencyContract.COLUMN_BTC_EQUIVALENT,
                CurrencyContract.COLUMN_ETH_EQUIVALENT,
                CurrencyContract.COLUMN_IS_ENABLED,
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = CurrencyContract.COLUMN_IS_ENABLED
                + " = ?";
        String[] selectionArgs = {"1"};

        // How you want the results sorted in the resulting Cursor


        Cursor cursor = database.query(
                CurrencyContract.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,
                null // don't filter by row groups
        );

        DatabaseUtils.dumpCursor(cursor);
        return cursor;

    }

    public static boolean isDeviceConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    static void updateCheckedRows(ArrayList<Integer> rowsToUpdate, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.COLUMN_IS_ENABLED, 1);

        String selection = CurrencyContract._ID + " IN (" + makePlaceholders(rowsToUpdate.size()) + ")";
        String[] selectionArgs = makeDbPlaceHolderFromIds(rowsToUpdate);
        int count = database.update(
                CurrencyContract.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Utils.logMessage("Succesully updated " + count + " rows ");
    }

    static void updateUncheckedRows(ArrayList<Integer> rowsToUpdate, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.COLUMN_IS_ENABLED, 0);

        String selection = CurrencyContract._ID + " IN (" + makePlaceholders(rowsToUpdate.size()) + ")";
        String[] selectionArgs = makeDbPlaceHolderFromIds(rowsToUpdate);
        int count = database.update(
                CurrencyContract.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Utils.logMessage("Successfully updated " + count + " rows ");

    }


    public static String[] makeDbPlaceHolderFromIds(ArrayList<Integer> arrayList) {
        String[] toReturn = new String[arrayList.size()];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = String.valueOf(arrayList.get(i));
        }
        return toReturn;
    }


    public static String makePlaceholders(int len) {
        Utils.logMessage("The length in makePlaceHolder is " + len);
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
