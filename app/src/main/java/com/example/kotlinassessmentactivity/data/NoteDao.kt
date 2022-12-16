package com.example.kotlinassessmentactivity.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Note database
 */
@Dao
interface NoteDao {

    // Get the notes from the latest noteUpdateAt
    @Query("SELECT * from note ORDER BY noteUpdateAt DESC")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * from note WHERE id = :id")
    fun getNote(id: Int): Flow<Note>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Note into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Note)

    @Update
    suspend fun update(item: Note)

    @Delete
    suspend fun delete(item: Note)
}
