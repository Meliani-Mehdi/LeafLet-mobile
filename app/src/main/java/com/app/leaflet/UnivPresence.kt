package com.app.leaflet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "presence",
    foreignKeys = [
        ForeignKey(
            entity = UnivSession::class,
            parentColumns = ["id"],
            childColumns = ["univSessionId"],
            onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = UnivStudent::class,
            parentColumns = ["id"],
            childColumns = ["univStudentId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index(value = ["univSessionId", "univStudentId"], unique = true)]
)
data class UnivPresence(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(index = true) val univSessionId: Int?,
    @ColumnInfo(index = true) val univStudentId: Int?,
    val present: String?,
    val comment: String?
)
