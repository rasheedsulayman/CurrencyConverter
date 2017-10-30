package com.r4sh33d.currencyconverter.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.r4sh33d.currencyconverter.model.Currency;
import com.r4sh33d.currencyconverter.adapters.CurrencyListAdapter;
import com.r4sh33d.currencyconverter.fragments.EnableCurrencyDialogFragment;
import com.r4sh33d.currencyconverter.R;
import com.r4sh33d.currencyconverter.utils.Utils;
import com.r4sh33d.currencyconverter.database.CurrencyContract;
import com.r4sh33d.currencyconverter.database.CurrencyDBHelper;
import com.r4sh33d.currencyconverter.network.CurrencyRetrofitService;
import com.r4sh33d.currencyconverter.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton fab;
    CurrencyDBHelper currencyDBHelper;
    SQLiteDatabase database;
    ArrayList<Currency> currencyArrayList = new ArrayList<>();
    CoordinatorLayout coordinatorLayout;
    HashMap<String, Integer> shortCodeFlagMap = new HashMap<>();
    HashMap<String, Integer> shortCodeCurrencySymbolMap = new HashMap<>();
    HashMap<String, String> codeCountryMap = new HashMap<>();
    SharedPreferences sharedPreferences;
    private CurrencyListAdapter currencyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fabAddCurrency);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        currencyListAdapter = new CurrencyListAdapter(this, currencyArrayList);
        recyclerView.setAdapter(currencyListAdapter);

        currencyDBHelper = new CurrencyDBHelper(this);
        database = currencyDBHelper.getWritableDatabase();// we still want to provide conversion without internet connection

        buildCurrencyDetailsMapsFromShortCodes();//This helps in associating local data with the conversion rates from the server
        sharedPreferences = getSharedPreferences(
                Utils.SHARED_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        if (!Utils.isDeviceConnected(this)) {
            //We are offline , load the last requested conversions rates from database
            refreshRecyclerViewItems();
        } else {
            //we have internet connection , load an up-to-date conversions from the server
            getConversionRatesFromServer();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //let user create conversion cards
                EnableCurrencyDialogFragment dialogFragment = new EnableCurrencyDialogFragment();
                dialogFragment.show(getFragmentManager().beginTransaction(), null);
            }
        });
    }


    void getConversionRatesFromServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading up to date conversion ratio from the server ,  Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        CurrencyRetrofitService currencyRetrofitService = RetrofitClient.getClient().create(CurrencyRetrofitService.class);
        Call<String> call = currencyRetrofitService.getConVersionRates(RetrofitClient.CRYPTOCURRENCIES_TO_CONVERT_FROM,
                buildCountryCurrencyParams());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //we have new data , drop the previous cached one
                database.delete(CurrencyContract.TABLE_NAME, null, null);
                JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();
                if (jsonObject != null && jsonObject.has("BTC") && jsonObject.has("ETH")) {

                    Set<String> shortCodeskeySet = jsonObject.getAsJsonObject("BTC").keySet();
                    for (String shortCode : shortCodeskeySet) {
                        ContentValues values = new ContentValues();
                        values.put(CurrencyContract.COLUMN_COUNTRY_SHORT_CODE, shortCode);
                        values.put(CurrencyContract.COLUMN_COUNTRY_NAME, codeCountryMap.get(shortCode));
                        values.put(CurrencyContract.COLUMN_BTC_EQUIVALENT, jsonObject.getAsJsonObject("BTC").get(shortCode).getAsDouble());
                        values.put(CurrencyContract.COLUMN_ETH_EQUIVALENT, jsonObject.getAsJsonObject("ETH").get(shortCode).getAsDouble());
                        values.put(CurrencyContract.COLUMN_DIALOG_LABEL, codeCountryMap.get(shortCode) + " (" + shortCode + ")");

                        if (shortCode.equals("NGN") || shortCode.equals("USD")
                                || shortCode.equals("EUR")) {
                            Utils.logMessage("getting shared prefs ---> " + shortCode + " : " + sharedPreferences.getBoolean(shortCode, false));
                            //The idea here is to have some pre-enabled currencies when opening the app for the first time .
                            // so that we don't have an empty screen first time  we open the app
                            values.put(CurrencyContract.COLUMN_IS_ENABLED, 1);
                        } else {
                            //get user's preference
                            values.put(CurrencyContract.COLUMN_IS_ENABLED, sharedPreferences.getBoolean(shortCode, false) ? 1 : 0);
                        }                        database.insert(CurrencyContract.TABLE_NAME, null, values);
                    }
                    dialog.dismiss();
                    refreshRecyclerViewItems();
                } else {
                    showSnackBar("Something went wrong ...");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                showSnackBar("Network connection error...");
            }
        });
    }

    void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getConversionRatesFromServer();
            }
        });
    }

    public void refreshRecyclerViewItems() {

        currencyArrayList.clear();
        currencyArrayList.addAll(Utils.getCurrencyList(Utils.makeConversionRatesCursor(database),
                shortCodeFlagMap, shortCodeCurrencySymbolMap));

        if (currencyArrayList.size() > 0) {
            //we have new data
            currencyListAdapter.notifyDataSetChanged();

        } else {
            //we are offline the first time of opening the app
            showSnackBar("Please turn on your internet connection and retry...");
        }
    }


    public void buildCurrencyDetailsMapsFromShortCodes() {

        String[] arrayOfCurrencyShortCodes = getResources().getStringArray(R.array.country_short_codes);
        String[] arrayOfCOuntryNames = getResources().getStringArray(R.array.country_names);

        TypedArray arrayOfFlags = getResources().obtainTypedArray(R.array.countryflags);
        TypedArray arrayOfSymbols = getResources().obtainTypedArray(R.array.currency_symbols);

        for (int i = 0; i < arrayOfCurrencyShortCodes.length; i++) {
            codeCountryMap.put(arrayOfCurrencyShortCodes[i], arrayOfCOuntryNames[i]);
            shortCodeFlagMap.put(arrayOfCurrencyShortCodes[i], arrayOfFlags.getResourceId(i, -1));
            shortCodeCurrencySymbolMap.put(arrayOfCurrencyShortCodes[i], arrayOfSymbols.getResourceId(i, -1));
        }
        arrayOfFlags.recycle();
        arrayOfSymbols.recycle();
    }


    //this methods builds the list of currencies to convert to . eg "NGN,USD,GPB,EUR"
    private String buildCountryCurrencyParams() {
        String[] arrayOfCurrencyShortCodes = getResources().getStringArray(R.array.country_short_codes);
        StringBuilder stringBuilder = new StringBuilder();
        String result = "";
        for (String countryShortcode : arrayOfCurrencyShortCodes) {
            stringBuilder.append(countryShortcode).append(",");
        }
        result = stringBuilder.toString();
        return result.substring(0, result.length() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();

    }
}
