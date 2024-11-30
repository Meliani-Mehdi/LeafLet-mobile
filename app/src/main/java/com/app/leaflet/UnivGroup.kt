package com.app.leaflet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Year

@Entity(
    tableName = "groups",
    foreignKeys = [ForeignKey(
        entity = UnivClass::class,
        parentColumns = ["id"],
        childColumns = ["univClassId"],
        onDelete = ForeignKey.CASCADE // Cascade deletes groups if UnivClass is deleted
    )]
)
data class UnivGroup(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary Key
    val name: String = "",
    val type: String = "TD",
    val year: Int = Year.now().value, // Default year
    @ColumnInfo(index = true) val univClassId: Int? // Foreign Key
)