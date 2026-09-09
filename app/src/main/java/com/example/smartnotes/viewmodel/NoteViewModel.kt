package com.example.smartnotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartnotes.SmartNotesApp
import com.example.smartnotes.data.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Manages all business logic, data formatting, and coroutine scopes safely.
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as SmartNotesApp).database.noteDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Reactive data flow: Automatically updates UI when database or search query changes
    val activeNotes = dao.getActiveNotes().combine(_searchQuery) { notes, query ->
        if (query.isBlank()) {
            notes 
        } else {
            notes.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.content.contains(query, ignoreCase = true) 
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val trashedNotes = dao.getTrashedNotes().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun saveNote(id: Long, title: String, content: String, isPinned: Boolean = false) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            if (id == 0L) {
                // Do not save completely empty notes
                if (title.isNotBlank() || content.isNotBlank()) {
                    dao.insert(Note(title = title, content = content, createdAt = currentTime, modifiedAt = currentTime, isPinned = isPinned))
                }
            } else {
                val existingNote = dao.getNoteById(id)
                // Only update database if data actually changed
                if (existingNote != null && (existingNote.title != title || existingNote.content != content || existingNote.isPinned != isPinned)) {
                    dao.update(existingNote.copy(title = title, content = content, modifiedAt = currentTime, isPinned = isPinned))
                }
            }
        }
    }

    fun moveToTrash(note: Note) {
        viewModelScope.launch {
            dao.update(note.copy(isTrashed = true, modifiedAt = System.currentTimeMillis()))
        }
    }

    fun restoreFromTrash(note: Note) {
        viewModelScope.launch {
            dao.update(note.copy(isTrashed = false, modifiedAt = System.currentTimeMillis()))
        }
    }

    fun deletePermanently(note: Note) {
        viewModelScope.launch { dao.delete(note) }
    }

    fun emptyTrash() {
        viewModelScope.launch { dao.emptyTrash() }
    }

    suspend fun getNoteById(id: Long): Note? = dao.getNoteById(id)
}

