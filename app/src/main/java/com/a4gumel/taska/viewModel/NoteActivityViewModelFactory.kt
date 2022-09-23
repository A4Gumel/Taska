package com.a4gumel.taska.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.a4gumel.taska.repository.NoteRepository

class NoteActivityViewModelFactory(private val noteRepository: NoteRepository):
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteActivityViewModel(noteRepository) as T
    }
}