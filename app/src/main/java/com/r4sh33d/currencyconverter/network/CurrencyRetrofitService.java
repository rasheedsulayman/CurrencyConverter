package com.r4sh33d.currencyconverter.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by r4sh33d on 10/17/17.
 */

public interface CurrencyRetrofitService  {


     @GET("users/{user}/repos")
     Call<String> listRepos(@Path("user") String user);
}
