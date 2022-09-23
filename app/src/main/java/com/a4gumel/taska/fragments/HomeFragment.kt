package com.a4gumel.taska.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.a4gumel.taska.R
import com.a4gumel.taska.activities.MainActivity
import com.a4gumel.taska.databinding.FragmentHomeBinding
import com.a4gumel.taska.utils.closeKeyboard
import com.a4gumel.taska.viewModel.NoteActivityViewModel
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homeBinding: FragmentHomeBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        exitTransition = MaterialElevationScale(false).apply {
            duration=300
        }

        enterTransition = MaterialElevationScale(true).apply {
            duration=300
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeBinding = FragmentHomeBinding.bind(view)
        val activity = activity as MainActivity
        val navController = Navigation.findNavController(view)
        requireView().closeKeyboard()

//        CoroutineScope(Dispatchers.Main).launch {
//            delay(10)
//        }

        homeBinding.addNoteFab.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToAddEditNoteFragment())
        }
    }
}