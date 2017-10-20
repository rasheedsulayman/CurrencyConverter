package com.r4sh33d.currencyconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.r4sh33d.currencyconverter.network.CurrencyRetrofitService;
import com.r4sh33d.currencyconverter.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private CurrencyListAdapter currencyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setAdapter(currencyListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    void getConversionRatesFromServer(){



        CurrencyRetrofitService currencyRetrofitService  = RetrofitClient.getClient().create(CurrencyRetrofitService.class);
        Call<JsonObject> call = currencyRetrofitService.getConVersionRates(RetrofitClient.CRYPTOCURRENCIES_TO_CONVERT_FROM ,
                RetrofitClient.COUNTRYCURRENCIES_TO_CONVERT_TO);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
