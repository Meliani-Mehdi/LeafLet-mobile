package com.app.leaflet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlanerDao {

    @Insert
    suspend fun insertPlaner(planer: UnivPlaner)

    @Insert
    suspend fun insertPlaners(planers: List<UnivPlaner>)

    @Update
    suspend fun updatePlaner(planer: UnivPlaner)

    @Query("Select * from planer where univGroupId = :groupId")
    suspend fun getPlanerByGroupId(groupId: Int): List<UnivPlaner>

    @Query("Select * from planer where id = :id")
    suspend fun getPlanerById(id: Int): UnivPlaner?

    @Delete
    suspend fun deletePlaner(planer: UnivPlaner)

}