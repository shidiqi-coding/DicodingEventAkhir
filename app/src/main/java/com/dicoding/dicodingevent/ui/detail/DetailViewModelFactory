package com.dicoding.dicodingevent.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.dicodingevent.entity.FavoriteEventDao


class DetailViewModelFactory(private val favoriteEventDao: FavoriteEventDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(favoriteEventDao) as T

        }
        throw IllegalArgumentException("unknown ViewModel Class")
    }
}
