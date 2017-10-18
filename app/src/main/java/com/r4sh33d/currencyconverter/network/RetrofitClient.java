package com.r4sh33d.currencyconverter.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by r4sh33d on 10/18/17.
 */

public class RetrofitClient {


    public static final String baseUrl = "";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }


    public static CurrencyRetrofitService getCurrencyRetrofitService() {
        return RetrofitClient.getClient().create(CurrencyRetrofitService.class);


    }
}