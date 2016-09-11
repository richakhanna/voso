package com.richdroid.voso.rest;

/**
 * Created by richa.khanna on 5/11/16.
 */

import com.richdroid.voso.model.AllSearchResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface ApiService {

    @FormUrlEncoded
    @POST("search")
    Call<AllSearchResponse> getRequestedGifs(@Field("is_mobile") String isMobile, @Field("text_query") String textQuery);
    //http://demo1054051.mockable.io/search
    //is_mobile=true&text_query=hello%20Google%20search%20me%20some%20happy%20face

    @FormUrlEncoded
    @POST("search")
    Call<AllSearchResponse> getSimilarGifs(@Field("is_mobile") String isMobile, @Field("url") String url);


}
