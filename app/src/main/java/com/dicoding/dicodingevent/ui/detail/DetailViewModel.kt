package com.dicoding.dicodingevent.ui.detail

//import android.R.id
//import android.util.Log
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewModelScope

import com.dicoding.dicodingevent.data.response.Event
import com.dicoding.dicodingevent.data.response.DetailEventResponse
import com.dicoding.dicodingevent.data.retrofit.ApiConfig
import com.dicoding.dicodingevent.entity.FavoriteEvent
import com.dicoding.dicodingevent.entity.FavoriteEventDao
import kotlinx.coroutines.launch

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("SENSELESS_COMPARISON")
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
                        checkIfFavorite(id)
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

    private fun checkIfFavorite(eventId: String) {
        val eventIdLong = eventId.toLongOrNull() ?: 0L
        favoriteEventDao.isFavorite(eventIdLong).observeForever { isFav ->
            _isFavorite.postValue(isFav ?:false)
        }
    }

    fun addToFavorite(event: Event) {
        viewModelScope.launch {
            val eventIdLong = event.id.toLong()
            if (eventIdLong != null) {
                val favoriteEvent = FavoriteEvent(
                    id = eventIdLong ,
                    name = event.name ,
                    mediaCover = event.mediaCover
                )
                favoriteEventDao.addToFavorite(favoriteEvent)
                _isFavorite.postValue(true)
                Log.d(TAG , "Added event ${event.id} to favorites")

            } else {
                Log.d(TAG , "Failed to add event : Invalid event ID Format")
            }
        }
    }

    fun removeFromFavorite(event: Event) {
        viewModelScope.launch {
            favoriteEventDao.removeFromFavorite(event.id.toLong())
            _isFavorite.postValue(false)
            Log.d(TAG , "Removed event ${event.id} from favorites")
        }
    }

    fun toggleFavorite(event: FavoriteEvent) {
        val eventIdLong = event.id.toLong()
        if(eventIdLong != null) {
            viewModelScope.launch {
                favoriteEventDao.isFavorite(eventIdLong).observeForever { isFav ->
                    viewModelScope.launch {
                        if (isFav == true) {
                            removeFromFavorite(event)
                        } else {
                            addToFavorite(event)
                        }
                    }
                }
            }

        }
    }
}
