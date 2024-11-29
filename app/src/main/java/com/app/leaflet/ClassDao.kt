package com.app.leaflet
import UnivClass
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClassDao {
    @Insert
    suspend fun insertClass(univClass: UnivClass)

    @Update
    suspend fun updateClass(univClass: UnivClass)

    @Query("Select * from classes where year = :year")
    suspend fun getClassByYear(year: Int): List<UnivClass>

    @Query("Select * from classes where id = :id")
    suspend fun getClassById(id: Int): UnivClass?

    @Delete
    suspend fun deleteClass(univClass: UnivClass)

    @Query("Delete from classes where id = :id")
    suspend fun deleteClassById(id: Int)

}