package com.example.tvseries.app.network

import com.example.tvseries.episodes.data.EpisodeEntity
import com.example.tvseries.shows.data.SeasonEntity
import com.example.tvseries.shows.data.ShowEntity
import com.example.tvseries.shows.data.ShowFilterEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("/shows")
    fun getShowsByPage(@Query("page") pageNumber: Int): Call<List<ShowEntity>>

    @GET("/shows/{show}/episodes")
    fun getEpisodesByShow(@Path("show") show: Int): Call<List<EpisodeEntity>>

    @GET("/shows/{show}/seasons")
    fun getSeasonsByShow(@Path("show") show: Int): Call<List<SeasonEntity>>

    @GET("/search/shows")
    fun getShowsByFilter(@Query("q") filter: String): Call<List<ShowFilterEntity>>

}