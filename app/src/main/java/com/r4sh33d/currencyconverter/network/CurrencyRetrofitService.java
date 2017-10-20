package com.r4sh33d.currencyconverter.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by r4sh33d on 10/17/17.
 */

public interface CurrencyRetrofitService {


    @GET("")
   public Call<JsonObject> getConVersionRates(@Query("fsyms") String fromSys , @Query("tsyms") String toSys);



    @GET("users/{user}/repos")
 public    Call<String> listRepos(@Path("user") String user);


}
