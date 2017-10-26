package com.r4sh33d.currencyconverter.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by r4sh33d on 10/17/17.
 *
 */

public interface CurrencyRetrofitService {


    @GET("data/pricemulti")
   public Call<String> getConVersionRates(@Query("fsyms") String fromSys , @Query("tsyms") String toSys);


}
