package com.example.kotlinassessmentactivity

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_FORCED
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kotlinassessmentactivity.data.Note
import com.example.kotlinassessmentactivity.data.getDateTime
import com.example.kotlinassessmentactivity.databinding.FragmentAddNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment to add or update an Note in the Note database.
 */
class AddNoteFragment : Fragment() {

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database.noteDao()
        )
    }
    private val navigationArgs: NoteListFragmentArgs by navArgs()

    lateinit var note: Note

    // Binding object instance corresponding to the fragment_add_note.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty and the timestamp is not zero
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.noteTitle.text.toString(),
            binding.noteText.text.toString(),
            System.currentTimeMillis(),
        )
    }

    /**
     * Binds views with the passed in [Note] information.
     */
    private fun bind(note: Note) {
        binding.apply {
            noteTitle.setText(note.noteTitle, TextView.BufferType.SPANNABLE)
            noteText.setText(note.noteText, TextView.BufferType.SPANNABLE)
//            saveAction.setOnClickListener { updateNote() }
            floatingDeleteButton.visibility = VISIBLE
            floatingDeleteButton.setOnClickListener { showConfirmationDialog() }

            noteUpdateAt.visibility = VISIBLE
            noteUpdateAt.text = note.getDateTime()

            noteText.setSelection(noteText.length())
        }
    }

    /**
     * Inserts the new Note into database and navigates up to list fragment.
     */
    private fun addNewNote() {
        if (isEntryValid()) {
            viewModel.addNewNote(
                this.binding.noteTitle.text.toString(),
                this.binding.noteText.text.toString(),
                System.currentTimeMillis(),
            )
        } else {
            Toast.makeText(context,"Empty Note discarded",Toast.LENGTH_SHORT).show()
        }

//        val action = AddNoteFragmentDirections.actionAddNoteFragmentToNoteListFragment()
//        findNavController().navigate(action)
    }

    /**
     * Updates an existing Note in the database and navigates up to list fragment.
     */
    private fun updateNote() {
        if (isEntryValid()) {
            // Check if there are changes from title or text
            if( note.noteTitle == binding.noteTitle.text.toString() &&
                note.noteText == binding.noteText.text.toString() ) {
//                Toast.makeText(context,"No changes made",Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateNote(
                    note.id,
                    binding.noteTitle.text.toString(),
                    binding.noteText.text.toString(),
                    System.currentTimeMillis(),
                )
            }
        } else {
            viewModel.deleteNote(note)
            Toast.makeText(context,"Empty Note discarded",Toast.LENGTH_SHORT).show()
        }

//        val action = AddNoteFragmentDirections.actionAddNoteFragmentToNoteListFragment()
//        findNavController().navigate(action)
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the note.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteNote()
            }
            .show()
    }

    /**
     * Deletes the current note and navigates to the list fragment.
     */
    private fun deleteNote() {
        viewModel.deleteNote(note)
        findNavController().navigateUp()
    }

    /**
     * Called when the view is created.
     * The noteId Navigation argument determines the edit Note or add new Note.
     * If the noteId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.noteId
        if (id > 0) {
            viewModel.retrieveNote(id).observe(this.viewLifecycleOwner) { selectedNote ->
                note = selectedNote
                bind(note)
            }
        }

        binding.noteText.requestFocus()
        val inputMethodManager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.noteText, SHOW_IMPLICIT)
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        if(this::note.isInitialized)
            updateNote()
        else
            addNewNote()

        super.onDestroyView()

        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
