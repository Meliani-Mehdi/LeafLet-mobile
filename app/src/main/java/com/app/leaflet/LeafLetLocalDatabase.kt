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

    //how to use database:
    /*
    val database = LeafLetLocalDatabase.getDatabase(this)

    val univClassDao = database.univClassDao()
    val groupDao = database.groupDao()
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
