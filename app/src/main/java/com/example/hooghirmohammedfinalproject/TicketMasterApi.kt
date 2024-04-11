package com.example.hooghirmohammedfinalproject

import android.view.KeyEvent
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketMasterApi {
    @GET("events.json")
    fun searchEvents(
        @Query("apikey") apiKey: String,
        @Query("keyword") searchTerm: String,
        @Query("city") city: String,
        @Query("sort") sortBy: String
    ): Call<TicketData>
}