package com.example.scoutoapp;

import com.example.scoutoapp.pojo.GetModelFromMakeId;
import com.example.scoutoapp.pojo.MakeResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiServices {

    @GET("getallmakes?format=json")
    Call<MakeResult> getCarMakes();

    @GET("GetModelsForMakeId/{id}/?format=json")
    Call<GetModelFromMakeId> getModelFromMakeId(@Path("id") Integer id);
}
