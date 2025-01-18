package com.app.leaflet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: UnivSession): Long

    @Update
    suspend fun updateSession(session: UnivSession)

    @Query("Select * from sessions where univPlanerId = :planerId")
    suspend fun getSessionByPlanerId(planerId: Int): List<UnivSession>

    @Query("Select * from sessions where id = :id")
    suspend fun getSessionById(id: Int): UnivSession?

    @Delete
    suspend fun deleteSession(session: UnivSession)

}