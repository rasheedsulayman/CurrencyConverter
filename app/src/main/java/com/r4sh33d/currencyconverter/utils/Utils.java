package com.r4sh33d.currencyconverter.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.r4sh33d.currencyconverter.database.CurrencyContract;
import com.r4sh33d.currencyconverter.model.Currency;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by r4sh33d on 10/20/17.
 *
 */

public class Utils {

    public static final String SHARED_PREFERENCE_FILE_KEY = "com.r4sh33d.currencyconverter.PREFERENCE_FILE_KEY";

    private static final String TAG = "debugtag";

    public static String CURRENCY_INTENT_KEY = "currency_intent_key";

    public static void logMessage(String message) {
        Log.d(TAG, message);
    }
    public static ArrayList<Currency> getCurrencyList(Cursor cursor,
                                                      HashMap<String, Integer> shortCodeFlagMap,
                                                      HashMap<String, Integer> shortCodeCurrencySymbolMap) {
        ArrayList<Currency> arrayList = new ArrayList<>();
        int countryNameIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_COUNTRY_NAME);
        int countryShortcode = cursor.getColumnIndex(CurrencyContract.COLUMN_COUNTRY_SHORT_CODE);
        int btcEquivIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_BTC_EQUIVALENT);
        int ethEquivIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_ETH_EQUIVALENT);
        int isEnabledIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_IS_ENABLED);

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
                CurrencyContract.COLUMN_COUNTRY_SHORT_CODE,
                CurrencyContract.COLUMN_DIALOG_LABEL,
                CurrencyContract.COLUMN_IS_ENABLED
        };

        return database.query(
                CurrencyContract.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

    }
    public static Cursor makeConversionRatesCursor(SQLiteDatabase database) {
        String[] projection = {
                CurrencyContract.COLUMN_COUNTRY_NAME,
                CurrencyContract.COLUMN_COUNTRY_SHORT_CODE,
                CurrencyContract.COLUMN_BTC_EQUIVALENT,
                CurrencyContract.COLUMN_ETH_EQUIVALENT,
                CurrencyContract.COLUMN_IS_ENABLED,
        };
        String selection = CurrencyContract.COLUMN_IS_ENABLED
                + " = ?";
        String[] selectionArgs = {"1"};
        Cursor cursor = database.query(
                CurrencyContract.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }

    public static boolean isDeviceConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * @param rowsToUpdate
     * @param database
     *
     * activates  rows (Currencies ) selected from the dialog
     */
    public static void updateCheckedRows(ArrayList<Integer> rowsToUpdate, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.COLUMN_IS_ENABLED, 1);
        String selection = CurrencyContract._ID + " IN (" + makePlaceholders(rowsToUpdate.size()) + ")";
        String[] selectionArgs = makeDbPlaceHolderFromIds(rowsToUpdate);
        int count = database.update(
                CurrencyContract.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * @param rowsToUpdate
     * @param database
     *
     * de-activate unchecked rows(currency cards) from the dialog
     */
    public static void updateUncheckedRows(ArrayList<Integer> rowsToUpdate, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.COLUMN_IS_ENABLED, 0);
        String selection = CurrencyContract._ID + " IN (" + makePlaceholders(rowsToUpdate.size()) + ")";
        String[] selectionArgs = makeDbPlaceHolderFromIds(rowsToUpdate);
        int count = database.update(
                CurrencyContract.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
    /**
     * @param arrayList
     * @return
     *
     * builds the list of rows to change for the  sqlite 'IN' statement
     */
    public static String[] makeDbPlaceHolderFromIds(ArrayList<Integer> arrayList) {
        String[] toReturn = new String[arrayList.size()];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = String.valueOf(arrayList.get(i));
        }
        return toReturn;
    }
    public static String makePlaceholders(int len) {

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

    public static  boolean isAValidNumber(String number){
        return !TextUtils.isEmpty(number) && !number.equalsIgnoreCase(".");
    }
}
