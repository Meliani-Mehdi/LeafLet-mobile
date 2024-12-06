package com.app.leaflet

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "classes",
    indices = [Index(value = ["name", "specialty", "level", "year"], unique = true)]
)
data class UnivClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val specialty: String = "",
    val level: String = "",
    val year: String = "${Calendar.getInstance().get(Calendar.YEAR)}-${Calendar.getInstance().get(Calendar.YEAR) + 1}"
)
