package com.app.leaflet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planer",
    foreignKeys = [ForeignKey(
        entity = UnivGroup::class,
        parentColumns = ["id"],
        childColumns = ["univGroupId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["day", "time", "univGroupId"], unique = true)]
)
data class UnivPlaner(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: String = "",
    val time: String = "",
    @ColumnInfo(index = true) val univGroupId: Int?
)
