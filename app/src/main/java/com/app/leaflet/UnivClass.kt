package com.app.leaflet

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Year

@Entity(tableName = "classes")
data class UnivClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val specialty: String = "",
    val level: String = "",
    val year: Int = Year.now().value
)
