package com.dicoding.dicodingevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingevent.data.response.Event
import com.dicoding.dicodingevent.data.response.DetailEventResponse
import com.dicoding.dicodingevent.data.retrofit.ApiConfig
import com.dicoding.dicodingevent.entity.FavoriteEvent
import com.dicoding.dicodingevent.entity.FavoriteEventDao
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val favoriteEventDao: FavoriteEventDao) : ViewModel() {
    private val _detailEvent = MutableLiveData<Event>()
    val detailEvent: LiveData<Event> = _detailEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    companion object {
        private const val TAG = "DetailViewModel"
    }

    fun getDetailEvents(id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvents(id)
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(
                call: Call<DetailEventResponse>,
                response: Response<DetailEventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val detailEventResponse = response.body()
                    if (detailEventResponse != null && !detailEventResponse.error) {
                        _detailEvent.value = detailEventResponse.event
                        checkIfFavorite(detailEventResponse.event.id.toLong())
                        Log.d(TAG, "Event details fetched successfully")
                    } else {
                        _errorMessage.value =
                            detailEventResponse?.message ?: "Unknown error occurred"
                    }
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                Log.e(TAG, "API call failed: ${t.message}")
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    private fun checkIfFavorite(eventId: Long) {
        viewModelScope.launch {
            val isFav = favoriteEventDao.isFavoriteNow(eventId.toString())
            _isFavorite.postValue(isFav)
        }
    }

    fun toggleFavorite(favoriteEvent: FavoriteEvent) {
        viewModelScope.launch {
            val isFav = favoriteEventDao.isFavoriteNow(favoriteEvent.id)
            if (isFav) {
                favoriteEventDao.removeFromFavorite(favoriteEvent.id)
                _isFavorite.postValue(false)
            } else {
                favoriteEventDao.addToFavorite(favoriteEvent)
                _isFavorite.postValue(true)
            }
        }
    }
}
