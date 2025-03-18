package com.dicoding.dicodingevent.entity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addtoFavorite(favoriteEvent: FavoriteEvent)

    @Delete
    fun removeFromFavorite(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM FavoriteEvent WHERE id = :eventId")
    fun getFavoriteById(eventId: String): LiveData<FavoriteEvent?>

    @Query("SELECT EXISTS(SELECT * FROM FavoriteEvent Where id =:eventId)")
    fun isFavorite(eventId: String): LiveData<Boolean>



}