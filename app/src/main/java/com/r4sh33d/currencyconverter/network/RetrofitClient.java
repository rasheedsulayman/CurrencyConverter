package com.r4sh33d.currencyconverter.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by r4sh33d on 10/18/17.
 */

public class RetrofitClient {



    public static final String baseUrl = "https://min-api.cryptocompare.com/";
    public  static  final String CRYPTOCURRENCIES_TO_CONVERT_FROM = "BTC,ETH";
    public static final String COUNTRYCURRENCIES_TO_CONVERT_TO = "";



    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
// add your other interceptors …

// add logging as last interceptor
        httpClient.addInterceptor(logging);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit;
    }


    public static CurrencyRetrofitService getCurrencyRetrofitService() {
        return RetrofitClient.getClient().create(CurrencyRetrofitService.class);


    }
}