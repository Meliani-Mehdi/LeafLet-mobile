package com.app.leaflet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "groups",
    foreignKeys = [ForeignKey(
        entity = UnivClass::class,
        parentColumns = ["id"],
        childColumns = ["univClassId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["name", "type"], unique = true)]
)
data class UnivGroup(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val type: String = "TD",
    @ColumnInfo(index = true) val univClassId: Int?
)