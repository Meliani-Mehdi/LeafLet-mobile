package com.app.leaflet
import Group
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GroupDao {

    @Insert
    suspend fun insertGroup(group: Group)

    @Update
    suspend fun updateGroup(group: Group)

    @Query("Select * from groups where year = :year")
    suspend fun getGroupByYear(year: Int): List<Group>

    @Query("Select * from groups where id = :id")
    suspend fun getGroupById(id: Int): Group?

    @Query("Select * from groups where univClassId = :univId")
    suspend fun getGroupByClassId(univId: Int): List<Group>

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("Delete from groups where id = :id")
    suspend fun deleteGroupById(id: Int)
}