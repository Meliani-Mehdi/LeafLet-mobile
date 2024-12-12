package com.app.leaflet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [ForeignKey(
        entity = UnivPlaner::class,
        parentColumns = ["id"],
        childColumns = ["univPlanerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["date", "univPlanerId"], unique = true)]
)
data class UnivSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String = "",
    @ColumnInfo(index = true) val univPlanerId: Int?
)
