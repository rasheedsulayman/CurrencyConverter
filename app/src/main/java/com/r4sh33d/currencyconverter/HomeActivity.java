package com.r4sh33d.currencyconverter;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private CurrencyListAdapter currencyListAdapter;
    CurrencyDBHelper currencyDBHelper;
    SQLiteDatabase database;
    ArrayList<Currency> currencyArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fabAddCurrency);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        currencyListAdapter = new CurrencyListAdapter(this, currencyArrayList);
        recyclerView.setAdapter(currencyListAdapter);
        currencyDBHelper = new CurrencyDBHelper(this);
        database = currencyDBHelper.getWritableDatabase();
        getConversionRatesFromServer();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog();
            }
        });

    }


    void setUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ArrayList<Integer> selectedItems = new ArrayList<>();  // Where we track the selected items
        final HashMap<Integer, Boolean> selectedItemsMap = new HashMap<>();
        //SparseBooleanArray sparse = new SparseBooleanArray(10);

        builder.setMultiChoiceItems(Utils.makeConversionRatesCursor(database), CurrencyContract.COLUMN_IS_ENABLED,
                CurrencyContract.COLUMN_DIALOG_LABEL, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Toast.makeText(HomeActivity.this, "Item clicked which is " + which
                                + " isChecked " + isChecked, Toast.LENGTH_SHORT).show();

                        selectedItemsMap.put(which, isChecked);


                    }
                }).setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // New value for one column
                ContentValues values = new ContentValues();
                //  values.put(FeedEntry.COLUMN_NAME_TITLE, title);

                //Which row to update, based on the title
                String selection = CurrencyContract._ID + " IN (" + makePlaceholders(selectedItemsMap.size()) + ")";
                String[] selectionArgs = makeDbPlaceHolderFromIds(selectedItemsMap);
                int count = database.update(
                        CurrencyContract.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }


    String[] makeDbPlaceHolderFromIds(HashMap<Integer, Boolean> map) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i : map.keySet()) {
            arrayList.add(String.valueOf(i));
        }
        String[] toReturn = new String[arrayList.size()];
        return arrayList.toArray(toReturn);
    }

    String makePlaceholders(int len) {
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

    void getConversionRatesFromServer() {
        Utils.logMessage("getConversionRatesFromServer called");

        CurrencyRetrofitService currencyRetrofitService = RetrofitClient.getClient().create(CurrencyRetrofitService.class);
        Call<String> call = currencyRetrofitService.getConVersionRates(RetrofitClient.CRYPTOCURRENCIES_TO_CONVERT_FROM,
                buildCountryCurrencyParams());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Utils.logMessage("onResponse called with " + response.body());
                Toast.makeText(HomeActivity.this, "onResponse called with ", Toast.LENGTH_SHORT).show();

                database = currencyDBHelper.getWritableDatabase();
                JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();
                Set<String> shortCodeskeySet = jsonObject.getAsJsonObject("BTC").keySet();
                HashMap<String, String> countryCodeMap = getCountryIsoCodeMap();

                for (String shortCode : shortCodeskeySet) {
                    ContentValues values = new ContentValues();
                    values.put(CurrencyContract.COLUMN_COUNTRY_SHORT_CODE, shortCode);
                    values.put(CurrencyContract.COLUMN_COUNTRY_NAME, countryCodeMap.get(shortCode));
                    values.put(CurrencyContract.COLUMN_BTC_EQUIVALENT, jsonObject.getAsJsonObject("BTC").get(shortCode).getAsDouble());
                    values.put(CurrencyContract.COLUMN_ETH_EQUIVALENT, jsonObject.getAsJsonObject("ETH").get(shortCode).getAsDouble());
                    values.put(CurrencyContract.COLUMN_DIALOG_LABEL, countryCodeMap.get(shortCode) + " (" + shortCode + ")");
                    values.put(CurrencyContract.COLUMN_IS_ENABLED, 0);
                    long id = database.insert(CurrencyContract.TABLE_NAME, null, values);
                    Utils.logMessage("row inserted with id " + id);

                }

                refreshRecyclerViewItems();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Network call failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void refreshRecyclerViewItems() {
        Utils.logMessage("inside refreshRecyclerviewItems");
        currencyArrayList.addAll(Utils.getCurrencciesFromCursor(Utils.makeConversionRatesCursor(database)));
        Utils.logMessage(currencyArrayList.toString());
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
        }
        result = stringBuilder.toString();
        return result.substring(0, result.length() - 1);
    }
}
