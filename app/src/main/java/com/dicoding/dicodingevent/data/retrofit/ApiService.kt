package com.dicoding.dicodingevent.data.retrofit


//import com.dicoding.dicodingevent.data.response.DailyReminderEventsItem
//import com.dicoding.dicodingevent.data.response.DailyReminderResponse
import com.dicoding.dicodingevent.data.response.DetailEventResponse
import com.dicoding.dicodingevent.data.response.EventResponse
import com.dicoding.dicodingevent.data.response.NotificationEventResponse
//import com.dicoding.dicodingevent.data.response.EventResponse
import com.dicoding.dicodingevent.data.response.SearchEventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvent(
        @Query("active") active: Int ,
        @Query("q") q: String? ,

        ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvents(@Path("id") id: String): Call<DetailEventResponse>

    @GET("events/daily")
    fun getNotificationEvents(): Call<NotificationEventResponse>

        @GET("events/search")
        fun searchEvents(
            @Query("query") query: String
        ): Call<SearchEventResponse>
}
