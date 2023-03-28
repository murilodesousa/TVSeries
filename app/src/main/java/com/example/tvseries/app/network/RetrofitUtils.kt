package com.example.tvseries.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitUtils {

    companion object {
        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.tvmaze.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

}