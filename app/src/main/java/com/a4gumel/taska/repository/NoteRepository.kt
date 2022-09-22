package com.a4gumel.taska.repository

import com.a4gumel.taska.db.NoteDatabase
import com.a4gumel.taska.model.Note

class NoteRepository(private val noteDb: NoteDatabase) {

    fun getAllNotes() = noteDb.getNoteDAO().getAllItems();

    fun searchNotes(query: String) = noteDb.getNoteDAO().searchItem(query);

    suspend fun addNote(note: Note) = noteDb.getNoteDAO().insertItem(note)

    suspend fun updateNote(note: Note) = noteDb.getNoteDAO().updateItem(note)

    suspend fun deleteNote(note: Note) = noteDb.getNoteDAO().deleteItem(note)

}