package com.dicoding.dicodingevent.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.dicodingevent.entity.FavoriteEventDao

@Database(entities = [Entity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteEventDao() : FavoriteEventDao

    companion object {
        @Volatile
         private var INSTANCE: AppDatabase? = null

        fun getInstance(context:Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
