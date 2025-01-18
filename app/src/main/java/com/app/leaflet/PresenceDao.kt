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

    @Query("""
        INSERT INTO presence (univSessionId, univStudentId, present, comment)
        SELECT :sessionId, s.id, NULL, NULL
        FROM students s
        WHERE s.univGroupId = (
            SELECT p.univGroupId
            FROM planer p
            WHERE p.id = (
                SELECT sp.univPlanerId
                FROM sessions sp
                WHERE sp.id = :sessionId
            )
        )
        AND s.id NOT IN (
            SELECT p.univStudentId
            FROM presence p
            WHERE p.univSessionId = :sessionId
        )
    """)
    suspend fun insertMissingStudentsForSession(sessionId: Int)

    @Query("Select * from presence where univSessionId = :sessionId")
    suspend fun getPresenceBySessionId(sessionId: Int): List<UnivPresence>

    @Query("Select * from presence where univStudentId = :studentId")
    suspend fun getPresenceByStudentId(studentId: Int): List<UnivPresence>

    @Query("Select * from presence where id = :id")
    suspend fun getPresenceById(id: Int): UnivPresence?

    @Delete
    suspend fun deletePresence(presence: UnivPresence)

}