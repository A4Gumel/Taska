package com.a4gumel.taska.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a4gumel.taska.model.Note
import com.a4gumel.taska.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteActivityViewModel(private var noteRepository: NoteRepository): ViewModel() {

    fun addNote(newNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.addNote(newNote)
    }

    fun updateNote(toUpdateNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.updateNote(toUpdateNote)
    }

    fun delete(toDeleteNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.addNote(toDeleteNote)
    }

    fun searchNotes(query: String): LiveData<List<Note>> {

        return noteRepository.searchNotes(query)
    }

    fun getAllNotes(): LiveData<List<Note>> = noteRepository.getAllNotes()
}