package com.dicoding.dicodingevent.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingevent.EventRepository
import com.dicoding.dicodingevent.data.response.EventResponse
import com.dicoding.dicodingevent.data.response.ListEventsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvent = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvent

    private val _finishedEvent = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvent

    private val _isLoadingUpcoming = MutableLiveData<Boolean>()
    val isLoadingUpcoming: LiveData<Boolean> = _isLoadingUpcoming

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadUpcomingEvents(query: String? = null) {
        loadEventsUpcoming(active = 1, query, _upcomingEvent)
    }

    fun loadFinishedEvents(query: String? = null) {
        loadEventsFinished(active = 0, query,_finishedEvent)
    }

    private fun loadEventsUpcoming(active: Int, query: String?,liveData: MutableLiveData<List<ListEventsItem>>) {
        _isLoadingUpcoming.value = true

        repository.getEventsCallback(active, query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoadingUpcoming.value = false
                if (response.isSuccessful) {
                    liveData.value = response.body()?.listEvents
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _errorMessage.value = "Failed to load events: $errorMessage"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingUpcoming.value = false
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }



    private fun loadEventsFinished(active: Int, query: String?, liveData: MutableLiveData<List<ListEventsItem>>) {
        _isLoadingFinished.value = true

        repository.getEventsCallback(active, query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoadingFinished.value = false
                if (response.isSuccessful) {
                    liveData.value = response.body()?.listEvents
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _errorMessage.value = "Failed to load events: $errorMessage"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingFinished.value = false
                _errorMessage.value = "Network error: ${t.message}"
            }
        })

    }

}
