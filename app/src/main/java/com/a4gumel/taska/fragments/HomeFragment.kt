package com.a4gumel.taska.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.a4gumel.taska.R
import com.a4gumel.taska.activities.MainActivity
import com.a4gumel.taska.adapter.NotesRvAdapter
import com.a4gumel.taska.databinding.FragmentHomeBinding
import com.a4gumel.taska.utils.closeKeyboard
import com.a4gumel.taska.viewModel.NoteActivityViewModel
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homeBinding: FragmentHomeBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private lateinit var rvAdapter: NotesRvAdapter


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

        displayItems()

        homeBinding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Show no data
            }

            override fun onTextChanged(searchText: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (searchText.toString().isNotEmpty()) {

                    val text = searchText.toString()
                    val query = "%$text"

                    if (query.isNotEmpty()) {

                        noteActivityViewModel.searchNotes(query).observe(viewLifecycleOwner) {

                        }
                    } else {

                        showDataChanges()

                    }
                } else {
                    showDataChanges()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }

        })

        @RequiresApi(Build.VERSION_CODES.M)

        if (Build.VERSION.SDK_INT >= VERSION_CODES.M)
        homeBinding.notesRv.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->

            when{
                scrollY > oldScrollY -> {
                    homeBinding.addNoteFab.shrink()
                }

                // just little or no scroll on fab
                scrollX==oldScrollY -> {
                    homeBinding.addNoteFab.extend()
                }

                else -> {
                    homeBinding.addNoteFab.extend()
                }
            }
        }
    }

    private fun showDataChanges() {
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner) { list ->
            //no data visible
        }
    }

    private fun displayItems() {
        when(resources.configuration.orientation) {

            Configuration.ORIENTATION_LANDSCAPE -> rvSetup(3)
            Configuration.ORIENTATION_PORTRAIT -> rvSetup(2)
        }
    }

    private fun rvSetup(spanCount: Int) {

        homeBinding.notesRv.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount,
                StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            rvAdapter = NotesRvAdapter()
            rvAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter=rvAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

        showDataChanges()
    }
}