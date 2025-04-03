package com.dicoding.dicodingevent.entity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToFavorite(favoriteEvent: FavoriteEvent)

    @Query("DELETE FROM FavoriteEvent WHERE id = :eventId")
    fun removeFromFavorite(eventId: Long)

    @Query("SELECT * FROM FavoriteEvent WHERE id = :eventId")
    fun getFavoriteById(eventId: Long): LiveData<FavoriteEvent?>

    @Query("SELECT EXISTS(SELECT * FROM FavoriteEvent Where id =:eventId)")
    fun isFavorite(eventId: Long): LiveData<Boolean>


}