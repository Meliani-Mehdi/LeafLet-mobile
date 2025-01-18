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

    @Query("""
        SELECT c.name AS className,
               c.specialty AS classSpecialty,
               c.level AS classLevel,
               c.year AS classYear,
               g.name AS groupName,
               g.type AS groupType,
               p.day AS day,
               p.time AS time,
               g.id AS groupId,
               p.id AS planerId
        FROM classes c
        JOIN groups g ON g.univClassId = c.id
        JOIN planer p ON p.univGroupId = g.id
        WHERE c.year = :year
        ORDER BY c.year, c.level, g.name, p.day, p.time
    """)
    suspend fun getSessionPlansByYear(year: String): List<SessionPlan>



}