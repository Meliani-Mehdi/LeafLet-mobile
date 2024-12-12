package com.app.leaflet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PresenceDao {

    @Insert
    suspend fun insertPresence(presence: UnivPresence)

    @Update
    suspend fun updatePresence(presence: UnivPresence)

    @Query("Select * from presence where univSessionId = :sessionId")
    suspend fun getPresenceBySessionId(sessionId: Int): List<UnivPresence>

    @Query("Select * from presence where univStudentId = :studentId")
    suspend fun getPresenceByStudentId(studentId: Int): List<UnivPresence>

    @Query("Select * from presence where id = :id")
    suspend fun getPresenceById(id: Int): UnivPresence?

    @Delete
    suspend fun deletePresence(presence: UnivPresence)

}