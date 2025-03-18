package com.dicoding.dicodingevent

import android.content.Context
import com.dicoding.dicodingevent.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository{
        val apiService = ApiConfig.getApiService()
        val database =
        return EventRepository.getInstance(apiService)
    }
}