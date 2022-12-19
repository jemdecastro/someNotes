package com.example.kotlinassessmentactivity.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

/**
 * Entity data class represents a single row in the database.
 */
@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "noteTitle")
    val noteTitle: String,

    @ColumnInfo(name = "noteText")
    val noteText: String,

    @ColumnInfo(name = "noteUpdateAt")
    val noteUpdateAt: Long,
)

/**
 * Returns the formatted DateTime from the timestamp
 */
fun Note.getDateTime(): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd yyyy, h:mm:ss a", Locale.ENGLISH)
        sdf.format(Date(noteUpdateAt))
    } catch (e: Exception) {
        ""
    }
}

