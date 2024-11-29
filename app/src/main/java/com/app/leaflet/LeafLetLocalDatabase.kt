package com.app.leaflet
import Group
import UnivClass
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UnivClass::class, Group::class], version = 1)
abstract class LeafLetLocalDatabase : RoomDatabase() {
    abstract fun univClassDao(): ClassDao
    abstract fun groupDao(): GroupDao

    companion object {
        @Volatile
        private var INSTANCE: LeafLetLocalDatabase? = null

        fun getDatabase(context: Context): LeafLetLocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LeafLetLocalDatabase::class.java,
                    "LeafLet_Local_Database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
