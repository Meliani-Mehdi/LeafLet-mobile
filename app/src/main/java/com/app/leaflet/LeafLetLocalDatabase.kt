package com.app.leaflet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UnivClass::class, UnivGroup::class, UnivStudent::class, UnivPlaner::class, UnivSession::class, UnivPresence::class],
    version = 1, exportSchema = false)
abstract class LeafLetLocalDatabase : RoomDatabase() {
    abstract fun univClassDao(): ClassDao
    abstract fun groupDao(): GroupDao
    abstract fun studentDao(): StudentDao
    abstract fun planerDao(): PlanerDao
    abstract fun sessionDao(): SessionDao
    abstract fun presenceDao(): PresenceDao

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

    //how to use database:
    /*
    val database = LeafLetLocalDatabase.getDatabase(this)

    val univClassDao = database.univClassDao()
    val groupDao = database.groupDao()
    val studentDao = database.studentDao()
     */

    //after you declare these, you can then use them like this:
    /*
    val newUnivClass = UnivClass(
        id = "123",
        name = "Computer Science",
        specialty = "AI",
        year = 2024
    )

    lifecycleScope.launch(Dispatchers.IO) {
        univClassDao.insertClass(newUnivClass)
        val classes = univClassDao.getClassByYear(2024)
    }


    */
}
