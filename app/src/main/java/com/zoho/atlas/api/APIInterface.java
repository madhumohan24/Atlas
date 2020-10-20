package com.zoho.atlas.api;

import com.zoho.atlas.keys.key;
import com.zoho.atlas.model.CountryData;
import com.zoho.atlas.model.ForecastData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("all")
    Call<List<CountryData>> getcountrydata();

    @Headers("Content-Type: application/json")
    @POST("forecast")
    Call<ForecastData> forecastdata(@Query(key.LAT) String latitude, @Query(key.LON) String longitude, @Query(key.CNT) String size, @Query(key.UNITS) String units, @Query(key.APPID) String appid);
}
