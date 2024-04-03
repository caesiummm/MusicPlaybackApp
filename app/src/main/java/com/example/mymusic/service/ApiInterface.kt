package com.example.mymusic.service

import com.example.mymusic.dataClass.Tracks
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {
    @Headers("X-RapidAPI-Key: 1e3d235804msh0b74146ef2c8554p13de9bjsn306dbb9df039",
            "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com")
    @GET("search")
    fun getData(@Query("q") query: String) : Call<Tracks>
}