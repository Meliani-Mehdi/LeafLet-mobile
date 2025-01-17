package com.app.leaflet
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GroupDao {

    @Insert
    suspend fun insertGroup(group: UnivGroup)

    @Update
    suspend fun updateGroup(group: UnivGroup)

    @Query("Select * from groups where id = :id")
    suspend fun getGroupById(id: Int): UnivGroup?

    @Query("Select * from groups where univClassId = :univId")
    suspend fun getGroupByClassId(univId: Int): List<UnivGroup>

    @Delete
    suspend fun deleteGroup(group: UnivGroup)

    @Query("Delete from groups where id = :id")
    suspend fun deleteGroupById(id: Int)

    @Query("Select * from groups where univClassId IN (:classIds)")
    suspend fun getGroupsByClassIds(classIds: List<Int>): List<UnivGroup>
}