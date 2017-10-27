package com.r4sh33d.currencyconverter;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
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
    CoordinatorLayout coordinatorLayout;
    HashMap<String, Integer> shortCodeFlagMap = new HashMap<>();
    HashMap<String, Integer> shortCodeCurrencySymbolMap = new HashMap<>();
    HashMap<String, String> codeCountryMap = new HashMap<>();
    AlertDialog alertDialog;


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
        database = currencyDBHelper.getWritableDatabase();
        buildCurrencyDetailsMapsFromShortCodes();

        if (!Utils.isDeviceConnected(this)) {
            //load the last requested conversions from database
            refreshRecyclerViewItems();
        } else {
            //we have internet connection , load an up-to-date conversions from the  server
            getConversionRatesFromServer();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpCreateCardDialog();
            }
        });
    }


    void setUpCreateCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ArrayList<Integer> selectedItems = new ArrayList<>();  // Where we track the selected items
        final ArrayList<Integer> desabledItems = new ArrayList<>(); // Where we keep track of disabled items


        alertDialog = builder.setMultiChoiceItems(Utils.makeCreateCardDialogCursor(database), CurrencyContract.COLUMN_IS_ENABLED,
                CurrencyContract.COLUMN_DIALOG_LABEL, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        Toast.makeText(HomeActivity.this, "Item clicked which is " + which
                                + " isChecked " + isChecked, Toast.LENGTH_SHORT).show();

                        int indexToUse = which + 1; // database rows start from 1 , while the "which" parameter  from callback start from zero , so adjust the offset.
                        if (isChecked) {
                            if (desabledItems.contains(indexToUse)) {
                                desabledItems.remove(Integer.valueOf(indexToUse));
                            }
                            selectedItems.add(indexToUse);
                        } else {
                            if (selectedItems.contains(indexToUse)) {
                                selectedItems.remove(Integer.valueOf(indexToUse));
                            }
                            desabledItems.add(indexToUse);
                        }


                    }
                }).setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.logMessage("seleced items " + selectedItems);
                Utils.logMessage("Disabled items " + desabledItems);
                if (selectedItems.size() > 0) {
                    Utils.updateCheckedRows(selectedItems, database);
                }
                if (desabledItems.size() > 0) {
                    Utils.updateUncheckedRows(desabledItems, database);

                }
                refreshRecyclerViewItems();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setTitle("Create Conversion Cards").create();

        alertDialog.getListView().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alertDialog.show();
    }


    void getConversionRatesFromServer() {
        Utils.logMessage("getConversionRatesFromServer called");
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
                Utils.logMessage("onResponse called with " + response.body());
                Toast.makeText(HomeActivity.this, "onResponse called with ", Toast.LENGTH_SHORT).show();
                database = currencyDBHelper.getWritableDatabase();
                database.delete(CurrencyContract.TABLE_NAME, null, null);
                JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();
                Set<String> shortCodeskeySet = jsonObject.getAsJsonObject("BTC").keySet();


                for (String shortCode : shortCodeskeySet) {
                    ContentValues values = new ContentValues();
                    values.put(CurrencyContract.COLUMN_COUNTRY_SHORT_CODE, shortCode);
                    values.put(CurrencyContract.COLUMN_COUNTRY_NAME, codeCountryMap.get(shortCode));
                    values.put(CurrencyContract.COLUMN_BTC_EQUIVALENT, jsonObject.getAsJsonObject("BTC").get(shortCode).getAsDouble());
                    values.put(CurrencyContract.COLUMN_ETH_EQUIVALENT, jsonObject.getAsJsonObject("ETH").get(shortCode).getAsDouble());
                    values.put(CurrencyContract.COLUMN_DIALOG_LABEL, codeCountryMap.get(shortCode) + " (" + shortCode + ")");
                    values.put(CurrencyContract.COLUMN_IS_ENABLED, 0);
                    long id = database.insert(CurrencyContract.TABLE_NAME, null, values);
                    Utils.logMessage("row inserted with id " + id);
                }
                dialog.dismiss();
                refreshRecyclerViewItems();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                Snackbar.make(coordinatorLayout, "Network connection error...", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getConversionRatesFromServer();
                            }
                        });
            }
        });
    }


    public void refreshRecyclerViewItems() {
        Utils.logMessage("inside refreshRecyclerviewItems");
        currencyArrayList.clear();
        currencyArrayList.addAll(Utils.getCurrencyList(Utils.makeConversionRatesCursor(database),
                shortCodeFlagMap, shortCodeCurrencySymbolMap));
        Utils.logMessage(currencyArrayList.toString());
        currencyListAdapter.notifyDataSetChanged();

    }


    public void buildCurrencyDetailsMapsFromShortCodes() {
        String[] arrayOfCurrencyShortCodes = getResources().getStringArray(R.array.country_short_codes);
        String[] arrayOfCOuntryNames = getResources().getStringArray(R.array.country_names);

        TypedArray arrayOfFlags = getResources().obtainTypedArray(R.array.countryflags);
        TypedArray arrayOfSymbols = getResources().obtainTypedArray(R.array.currency_symbols);

        for (int i = 0; i < arrayOfCurrencyShortCodes.length; i++) {
            Utils.logMessage(arrayOfFlags.getResourceId(i, -1) + "");
            codeCountryMap.put(arrayOfCurrencyShortCodes[i], arrayOfCOuntryNames[i]);
            shortCodeFlagMap.put(arrayOfCurrencyShortCodes[i], arrayOfFlags.getResourceId(i, -1));
            shortCodeCurrencySymbolMap.put(arrayOfCurrencyShortCodes[i], arrayOfSymbols.getResourceId(i, -1));
        }
        arrayOfFlags.recycle();
        arrayOfSymbols.recycle();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }
}
