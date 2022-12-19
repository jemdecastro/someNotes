package com.example.kotlinassessmentactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotlinassessmentactivity.data.Note
import com.example.kotlinassessmentactivity.data.NoteDao
import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the Note repository and an up-to-date list of all Notes.
 */
class NoteViewModel(private val NoteDao: NoteDao) : ViewModel() {

    // Cache all Notes from the database using LiveData.
    val allNotes: LiveData<List<Note>> = NoteDao.getNotes().asLiveData()

    /**
     * Updates an existing Note in the database.
     */
    fun updateNote(
        NoteId: Long,
        NoteTitle: String,
        NoteText: String,
        NoteUpdateAt: Long
    ) {
        val updatedNote = getUpdatedNoteEntry(NoteId, NoteTitle, NoteText, NoteUpdateAt)
        updateNote(updatedNote)
    }

    /**
     * Launching a new coroutine to update a Note in a non-blocking way
     */
    private fun updateNote(Note: Note) {
        viewModelScope.launch {
            NoteDao.update(Note)
        }
    }

    /**
     * Inserts the new Note into database.
     * Launching a new coroutine to insert a Note in a non-blocking way
     */
    fun addNewNote(noteTitle: String, noteText: String, noteUpdateAt: Long) {
        val newNote = getNewNoteEntry(noteTitle, noteText, noteUpdateAt)
        viewModelScope.launch {
            NoteDao.insert(newNote)
        }
    }

    /**
     * Launching a new coroutine to delete a Note in a non-blocking way
     */
    fun deleteNote(Note: Note) {
        viewModelScope.launch {
            NoteDao.delete(Note)
        }
    }

    /**
     * Retrieve a Note from the repository.
     */
    fun retrieveNote(id: Long): LiveData<Note> {
        return NoteDao.getNote(id).asLiveData()
    }

    /**
     * Returns true if the Values are valid
     */
    fun isEntryValid(NoteTitle: String, NoteText: String, NoteUpdateAt: Long): Boolean {
        if ( ( NoteTitle.isBlank() && NoteText.isBlank() ) || NoteUpdateAt==0L) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Note] entity class with the Note info entered by the user.
     * This will be used to add a new entry to the Note database.
     */
    private fun getNewNoteEntry(NoteTitle: String, NoteText: String, NoteUpdateAt: Long): Note {
        return Note(
            noteTitle = NoteTitle,
            noteText = NoteText,
            noteUpdateAt = NoteUpdateAt
        )
    }

    /**
     * Called to update an existing entry in the Note database.
     * Returns an instance of the [Note] entity class with the Note info updated by the user.
     */
    private fun getUpdatedNoteEntry(
        NoteId: Long,
        NoteTitle: String,
        NoteText: String,
        NoteUpdateAt: Long
    ): Note {
        return Note(
            id = NoteId,
            noteTitle = NoteTitle,
            noteText = NoteText,
            noteUpdateAt = NoteUpdateAt
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class NoteViewModelFactory(private val NoteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(NoteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}