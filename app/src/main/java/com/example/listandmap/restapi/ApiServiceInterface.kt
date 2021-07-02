package com.example.listandmap.restapi

import com.example.listandmap.dataclass.CheckListResponse
import com.example.listandmap.dataclass.MapDirectionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceInterface {

    @GET(ApiConstantsUrl.CHECK_LIST)
    suspend fun getCheckList(): Response<ArrayList<CheckListResponse>>

    @GET(ApiConstantsUrl.GET_DIRECTION)
    suspend fun getDirection(@Query("origin") origin: String,
                     @Query("destination") destination: String,
                     @Query("key") apiKey: String): Response<MapDirectionResponse>
}