package com.a4gumel.taska.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoteActivityViewModelFactory(private val noteRepository: NoteRepository):
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteActivityViewModel(noteRepository) as T
    }
}