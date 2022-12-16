package com.example.kotlinassessmentactivity

import android.app.Application
import com.example.kotlinassessmentactivity.data.NoteRoomDatabase

class NoteApplication : Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    val database: NoteRoomDatabase by lazy { NoteRoomDatabase.getDatabase(this) }
}
