package com.app.leaflet
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudentDao {

    @Insert
    suspend fun insertStudent(student: UnivStudent)

    @Update
    suspend fun updateStudent(student: UnivStudent)

    @Query("Select * from students where univGroupId = :groupId")
    suspend fun getStudentByGroupId(groupId: Int): List<UnivStudent>

    @Query("Select * from students where id = :id")
    suspend fun getStudentById(id: Int): UnivStudent?

    @Delete
    suspend fun deleteStudent(student: UnivStudent)

    @Query("Delete from students where id = :id")
    suspend fun deleteStudentById(id: Int)

}