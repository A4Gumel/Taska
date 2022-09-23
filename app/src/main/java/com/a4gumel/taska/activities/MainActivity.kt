package com.a4gumel.taska.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.a4gumel.taska.R
import com.a4gumel.taska.databinding.ActivityMainBinding
import com.a4gumel.taska.db.NoteDatabase
import com.a4gumel.taska.repository.NoteRepository
import com.a4gumel.taska.viewModel.NoteActivityViewModel
import com.a4gumel.taska.viewModel.NoteActivityViewModelFactory

class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"

    private lateinit var noteActivityViewModel: NoteActivityViewModel;
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        try {
            setContentView(binding.root)
            val noteRepository = NoteRepository(NoteDatabase(this))
            val noteActivityViewModelFactory = NoteActivityViewModelFactory(noteRepository)
            noteActivityViewModel = ViewModelProvider(this,
                noteActivityViewModelFactory)[NoteActivityViewModel::class.java]
        } catch (e: Exception) {

            Log.e(TAG, e.toString())
        }
    }
}