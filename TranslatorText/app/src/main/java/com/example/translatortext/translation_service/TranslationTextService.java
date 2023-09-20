package com.example.translatortext.translation_service;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TranslationTextService {
    @GET("/api/mode-list")
    Call<List<String>> getModeList();

    @POST("/api/translate")
    @Headers("Content-Type: application/json")
    Call<TranslateResult> translate(@Body RequestBody params);
}
