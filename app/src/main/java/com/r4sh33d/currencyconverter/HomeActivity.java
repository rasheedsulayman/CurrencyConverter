package com.r4sh33d.currencyconverter;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.JsonObject;
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
    private CurrencyListAdapter currencyListAdapter;
    CurrencyDBHelper currencyDBHelper;
    SQLiteDatabase database;
    ArrayList<Currency> currencyArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(currencyListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        currencyListAdapter = new CurrencyListAdapter(this, currencyArrayList);
        recyclerView.setAdapter(currencyListAdapter);
        currencyDBHelper = new CurrencyDBHelper(this);
        database = currencyDBHelper.getWritableDatabase();

    }


    void getConversionRatesFromServer() {
        CurrencyRetrofitService currencyRetrofitService = RetrofitClient.getClient().create(CurrencyRetrofitService.class);
        Call<JsonObject> call = currencyRetrofitService.getConVersionRates(RetrofitClient.CRYPTOCURRENCIES_TO_CONVERT_FROM,
                buildCountryCurrencyParams());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Gets the data repository in write mode
                database = currencyDBHelper.getWritableDatabase();
                JsonObject jsonObject = response.body();
                Set<String> shortCodeskeySet = jsonObject.getAsJsonObject("BTC").keySet();
                HashMap<String, String> countryCodeMap = getCountryIsoCodeMap();

                for (String shortCode : shortCodeskeySet) {
                    ContentValues values = new ContentValues();
                    values.put(CurrencyContract.COLUMN_COUNTRY_SHORT_CODE, shortCode);
                    values.put(CurrencyContract.COLUMN_COUNTRY_NAME, countryCodeMap.get(shortCode));
                    values.put(CurrencyContract.COLUMN_BTC_EQUIVALENT, jsonObject.get("BTC").getAsDouble());
                    values.put(CurrencyContract.COLUMN_ETH_EQUIVALENT, jsonObject.get("ETH").getAsDouble());
                    values.put(CurrencyContract.COLUMN_IS_ENABLED, 0);
                    database.insert(CurrencyContract.TABLE_NAME, null, values);
                }

                refreshRecyclerViewItems();


            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    public void refreshRecyclerViewItems() {

        currencyArrayList = Utils.readConversionRatesFromDB(database);
        currencyListAdapter.notifyDataSetChanged();

    }


    public HashMap<String, String> getCountryIsoCodeMap() {
        HashMap<String, String> codeCountryMap = new HashMap<>();
        String[] arrayOfCurrencyShortCodes = getResources().getStringArray(R.array.country_short_codes);
        String[] arrayOfCOuntryNames = getResources().getStringArray(R.array.country_names);
        for (int i = 0; i < arrayOfCurrencyShortCodes.length; i++) {
            codeCountryMap.put(arrayOfCurrencyShortCodes[i], arrayOfCOuntryNames[i]);
        }
        return codeCountryMap;


    }


    private String buildCountryCurrencyParams() {
        String[] arrayOfCurrencyShortCodes = getResources().getStringArray(R.array.country_short_codes);
        StringBuilder stringBuilder = new StringBuilder();
        String result = "";
        for (String countryShortcode : arrayOfCurrencyShortCodes) {
            stringBuilder.append(countryShortcode).append(",");
            result = stringBuilder.toString();
        }
        return result.substring(0, result.length() - 1);
    }
}
