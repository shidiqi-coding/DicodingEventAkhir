package com.dicoding.dicodingevent

import com.dicoding.dicodingevent.data.response.DetailEventResponse
import com.dicoding.dicodingevent.data.response.EventResponse
import com.dicoding.dicodingevent.data.retrofit.ApiService
import retrofit2.Call
//import okhttp3.Response

class EventRepository(private val apiService: ApiService) {
    fun getEventsCallback(active: Int , query: String?): Call<EventResponse> {
        return apiService.getEvent(active , query)
    }

    fun getEventDetail(id: String): Call<DetailEventResponse> {
        return apiService.getDetailEvents(id)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(apiService: ApiService): EventRepository {
            return instance ?: synchronized(this) {
                instance ?: EventRepository(apiService).also { instance = it }
            }
        }
    }
}

