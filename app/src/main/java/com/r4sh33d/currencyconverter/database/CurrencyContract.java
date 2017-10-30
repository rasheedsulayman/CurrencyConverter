package com.r4sh33d.currencyconverter.database;

import android.provider.BaseColumns;

/**
 * Created by r4sh33d on 10/17/17.
 */

public class CurrencyContract implements BaseColumns{
    public  static final String TABLE_NAME = "currency_record";
    public  static final String COLUMN_COUNTRY_NAME = "title";
    public  static final String COLUMN_COUNTRY_SHORT_CODE = "subtitle";
    public  static final String COLUMN_BTC_EQUIVALENT = "btc_equivalent";
    public  static final String COLUMN_ETH_EQUIVALENT = "eth_equivalent";
    public  static final String COLUMN_IS_ENABLED = "is_enabled";
    public static final String COLUMN_DIALOG_LABEL = "dialog_label";
}


