package com.example.tvseries.api

import com.example.tvseries.entities.Episode
import com.example.tvseries.entities.Season
import com.example.tvseries.entities.Show
import com.example.tvseries.entities.ShowFilter
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("/shows")
    fun getShowsByPage(@Query("page") pageNumber: Int): Call<List<Show>>

    @GET("/shows/{show}/episodes")
    fun getEpisodesByShow(@Path("show") show: Int): Call<List<Episode>>

    @GET("/shows/{show}/seasons")
    fun getSeasonsByShow(@Path("show") show: Int): Call<List<Season>>

    @GET("/search/shows")
    fun getShowsByFilter(@Query("q") filter: String): Call<List<ShowFilter>>

}