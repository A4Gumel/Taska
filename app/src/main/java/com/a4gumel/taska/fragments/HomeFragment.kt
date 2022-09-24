package com.a4gumel.taska.fragments

import android.content.SharedPreferences.Editor
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.a4gumel.taska.R
import com.a4gumel.taska.activities.MainActivity
import com.a4gumel.taska.adapter.NotesRvAdapter
import com.a4gumel.taska.databinding.FragmentHomeBinding
import com.a4gumel.taska.utils.SwipeToDelete
import com.a4gumel.taska.utils.closeKeyboard
import com.a4gumel.taska.viewModel.NoteActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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
            duration = 300
        }

        enterTransition = MaterialElevationScale(true).apply {
            duration = 300
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
        swipeToDeleteItem(homeBinding.notesRv)

        homeBinding.searchEditText.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                view.clearFocus()
                view.closeKeyboard()
            }

            return@setOnEditorActionListener true
        }

        homeBinding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Show no data
            }

            override fun onTextChanged(searchText: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (searchText.toString().isNotEmpty()) {

                    val text = searchText.toString()
                    val query = "%$text"

                    if (query.isNotEmpty()) {

                        noteActivityViewModel.searchNotes(query)
                            .observe(viewLifecycleOwner) { list ->

                                rvAdapter.submitList(list)

                            }
                    } else {

                        showDataChanges()

                    }
                } else {
                    showDataChanges()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            homeBinding.notesRv.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->

                when {
                    scrollY > oldScrollY -> {
                        homeBinding.addNoteFab.shrink()
                    }

                    // just little or no scroll on fab
                    scrollX == oldScrollY -> {
                        homeBinding.addNoteFab.extend()
                    }

                    else -> {
                        homeBinding.addNoteFab.extend()
                    }
                }
            }

        }
    }

    private fun swipeToDeleteItem(notesRv: RecyclerView) {

        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = rvAdapter.currentList[position]
                var btnTapped = false
                noteActivityViewModel.delete(note)

                homeBinding.searchEditText.apply {
                    closeKeyboard()
                    clearFocus()
                }

                if (homeBinding.searchEditText.text.toString().isEmpty()) {
                    showDataChanges()
                }

                val snackbar = Snackbar.make(
                    requireView(),
                    "Note deleted",
                    Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {

                        transientBottomBar?.setAction("UNDO") {
                            noteActivityViewModel.addNote(note)
                            btnTapped=true
                            //show no data
                        }

                        super.onShown(transientBottomBar)

                    }
                }).apply {
                    animationMode=Snackbar.ANIMATION_MODE_SLIDE
                    setAnchorView(R.id.addNoteFab)
                }

                snackbar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple5
                    )
                )

                snackbar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(homeBinding.notesRv)
    }

    private fun showDataChanges() {
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner) { list ->
            //no data visible
            rvAdapter.submitList(list)
        }
    }

    private fun displayItems() {
        when (resources.configuration.orientation) {

            Configuration.ORIENTATION_LANDSCAPE -> rvSetup(3)
            Configuration.ORIENTATION_PORTRAIT -> rvSetup(2)
        }
    }

    private fun rvSetup(spanCount: Int) {

        homeBinding.notesRv.apply {
            layoutManager = StaggeredGridLayoutManager(
                spanCount,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            rvAdapter = NotesRvAdapter()
            rvAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = rvAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

        showDataChanges()
    }
}