package com.dicoding.dicodingevent.ui.detail

//import android.R.id
//import android.util.Log
import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope

import com.dicoding.dicodingevent.data.response.Event
import com.dicoding.dicodingevent.data.response.DetailEventResponse
import com.dicoding.dicodingevent.data.retrofit.ApiConfig
//import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _detailEvent = MutableLiveData<Event>()
    val detailEvent: LiveData<Event> = _detailEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "DetailVieModel"
    }


    //@SuppressLint("SuspiciousIndentation")
    fun getDetailEvents(id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvents(id) // Retrofit Call
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(
                call: Call<DetailEventResponse> ,
                response: Response<DetailEventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val detailEventResponse = response.body()
                    if (detailEventResponse != null && !detailEventResponse.error) {
                        _detailEvent.value = detailEventResponse.event
                        Log.d("DetailViewModel" , "Event details fetched successfully")
                    } else {
                        _errorMessage.value =
                            detailEventResponse?.message ?: "Unknown error occurred"
                    }
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<DetailEventResponse> , t: Throwable) {
                Log.e("DetailViewModel" , "API call failed: ${t.message}")
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }
}