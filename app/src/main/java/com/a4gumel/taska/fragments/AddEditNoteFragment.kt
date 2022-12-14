package com.a4gumel.taska.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.a4gumel.taska.R
import com.a4gumel.taska.activities.MainActivity
import com.a4gumel.taska.databinding.FragmentAddEditNoteBinding
import com.a4gumel.taska.model.Note
import com.a4gumel.taska.utils.closeKeyboard
import com.a4gumel.taska.viewModel.NoteActivityViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dev.sasikanth.colorsheet.ColorSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddEditNoteFragment : Fragment(R.layout.fragment_add_edit_note) {

    private var TAG: String = "AddEditNoteFragment"

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentAddEditNoteBinding
    private var note: Note? = null
    private var color = -1
    private lateinit var result: String
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private var job = CoroutineScope(Dispatchers.Main)
    private val args: AddEditNoteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }

        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentBinding = FragmentAddEditNoteBinding.bind(view)

        navController = Navigation.findNavController(view)

        val activity = activity as MainActivity

        activity.setSupportActionBar(contentBinding.addNoteToolbar)

        ViewCompat.setTransitionName(
            contentBinding.noteScrollView,
            "recyclerView_${args.note?.id}"
        )

        contentBinding.addNoteToolbar.setNavigationOnClickListener {
            requireView().closeKeyboard()
            navController.popBackStack()
        }

        try {

            contentBinding.noteContentEdt.setOnFocusChangeListener { _, hasFocus ->

                Log.d(TAG, "The focus : ".plus(hasFocus))

                if (hasFocus) {

                    contentBinding.noteContentEdt.setStylesBar(contentBinding.noteMarkdowns)
                    contentBinding.noteMarkdowns.visibility = View.VISIBLE

                } else contentBinding.noteMarkdowns.visibility = View.GONE
            }
        } catch (e: Throwable) {

            Log.e(TAG, e.printStackTrace().toString())
        }

        contentBinding.saveNote.setOnClickListener {

            saveNote()
        }

        val colors = resources.getIntArray(R.array.colors)

        contentBinding.pickColorFab.setOnClickListener {

            ColorSheet().colorPicker(
                colors = colors,
                noColorOption = false,
                listener = { value ->
                    // Handle color
                    color = value
                    contentBinding.apply {
                        contentBinding.noteScrollView.setBackgroundColor(color)
                        activity.window.statusBarColor = color
                        activity.window.navigationBarColor = color
                        contentBinding.parentLayout.setBackgroundColor(color)
                    }
                })
                .show(activity.supportFragmentManager)
        }

        // When user opens up and existing note then
        setUpNote()
    }

    private fun setUpNote() {
        val note = args.note
        val title = contentBinding.noteTitleEdt
        val content = contentBinding.noteContentEdt

        if (note != null) {

            color = note.color
            title.setText(note.title)
            content.renderMD(note.content)

            contentBinding.apply {
                job.launch {
                    delay(10)
                }
                contentBinding.noteScrollView.setBackgroundColor(color)
                contentBinding.parentLayout.setBackgroundColor(color)
            }

            activity?.window?.statusBarColor = color
            activity?.window?.navigationBarColor = color
        }
    }

    private fun saveNote() {

        if (contentBinding.noteContentEdt.text.toString()
                .isEmpty() || contentBinding.noteTitleEdt.text.toString().isEmpty()
        ) {

            Snackbar.make(
                contentBinding.bottomLayout,
                "Please enter something to save",
                Snackbar.LENGTH_LONG
            ).show()
        } else {

            note = args.note

            when (note) {
                null -> {
                    noteActivityViewModel.addNote(
                        Note(
                            0,
                            contentBinding.noteTitleEdt.text.toString(),
                            contentBinding.noteContentEdt.getMD(),
                            currentDate,
                            color
                        )
                    )

                    result = "Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )

                    navController.navigate(AddEditNoteFragmentDirections.actionAddEditNoteFragmentToHomeFragment())

                }
                else -> {

                    updateNote()
                    navController.popBackStack()
                }
            }
        }

    }

    private fun updateNote() {

        if (note!=null) {
            noteActivityViewModel.updateNote(
                Note(
                    note!!.id,
                    contentBinding.noteTitleEdt.text.toString(),
                    contentBinding.noteContentEdt.getMD(),
                    currentDate,
                    color
                )
            )
        }
    }

}