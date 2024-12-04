package com.app.leaflet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [ForeignKey(
        entity = UnivGroup::class,
        parentColumns = ["id"],
        childColumns = ["univGroupId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UnivStudent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    @ColumnInfo(index = true) val univGroupId: Int?
)
