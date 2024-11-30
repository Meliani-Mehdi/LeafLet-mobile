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
        onDelete = ForeignKey.CASCADE
    )]
)
data class UnivGroup(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val type: String = "TD",
    val year: Int = Year.now().value,
    @ColumnInfo(index = true) val univClassId: Int?
)