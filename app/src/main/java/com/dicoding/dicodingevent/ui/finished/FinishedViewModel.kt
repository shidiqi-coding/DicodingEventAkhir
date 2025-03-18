package com.dicoding.dicodingevent.ui.finished

//import android.content.ContentValues.TAG
import android.util.Log
import android.util.Log.e
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingevent.data.response.EventResponse
//import com.dicoding.dicodingevent.data.response.EventResponse
import com.dicoding.dicodingevent.data.response.ListEventsItem
import com.dicoding.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel : ViewModel() {

    private val _eventlist = MutableLiveData<List<ListEventsItem>>()
    val eventlist: LiveData<List<ListEventsItem>> = _eventlist

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    companion object {
        private const val TAG = "FinishedViewModel"
    }

    init {
        fetchEvents(active = 0 , query = null) // Fetch upcoming events by default
    }

    private fun fetchEvents(active: Int , query: String?) {
        _loading.value = true
        _error.value = null

        val call = ApiConfig.getApiService().getEvent(active = active , q = query)
        call.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse> , response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _eventlist.value = response.body()?.listEvents
                } else {
                    _error.value = "Failed to fetch events: ${response.message()}"
                    Log.e(TAG , "onFailure: ${response.message()}")
                }
                _loading.value = false
            }


            override fun onFailure(call: Call<EventResponse> , t: Throwable) {
                _error.value = "Network error: ${t.message}"
                Log.e(TAG , "onFailure: ${t.message}")
                _loading.value = false
            }
        })
    }

    fun fetchFinishedEvents() {
        fetchEvents(active = 0 , query = null)
    }

    fun fetchAllEvents() {
        fetchEvents(active = -1 , query = null)
    }

    fun searchEvents(query: String) {
        fetchEvents(active = 0 , query = query)
    }

}







