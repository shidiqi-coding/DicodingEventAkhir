package com.dicoding.dicodingevent.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingevent.EventRepository
import com.dicoding.dicodingevent.entity.FavoriteEvent
import kotlinx.coroutines.launch

class FavoriteViewModel(private val eventRepository: EventRepository): ViewModel() {

    fun getFavoritedEvents() = eventRepository.getAllFavorites()

    

    fun saveEvent(event: FavoriteEvent){
        viewModelScope.launch {
            eventRepository.setEventFavorite(event, true)
        }
    }

    fun deleteEvent(event: FavoriteEvent) {
        viewModelScope.launch {
            eventRepository.setEventFavorite(event , false)
        }
    }
}
