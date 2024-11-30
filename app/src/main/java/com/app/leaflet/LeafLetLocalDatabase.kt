package com.app.leaflet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UnivClass::class, UnivGroup::class], version = 1, exportSchema = false)
abstract class LeafLetLocalDatabase : RoomDatabase() {
    abstract fun univClassDao(): ClassDao
    abstract fun groupDao(): GroupDao
}
