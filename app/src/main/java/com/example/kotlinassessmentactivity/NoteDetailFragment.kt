package com.example.kotlinassessmentactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kotlinassessmentactivity.data.Note
import com.example.kotlinassessmentactivity.data.getDateTime
import com.example.kotlinassessmentactivity.databinding.FragmentNoteDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * [NoteDetailFragment] displays the details of the selected note.
 */
class NoteDetailFragment : Fragment() {
    private val navigationArgs: NoteDetailFragmentArgs by navArgs()
    lateinit var note: Note

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database.noteDao()
        )
    }

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Binds views with the passed in note data.
     */
    private fun bind(note: Note) {
        binding.apply {
            noteTitle.text = note.noteTitle
            noteText.text = note.noteText
            noteUpdateAt.text = note.getDateTime()
            deleteNote.setOnClickListener { showConfirmationDialog() }
            editNote.setOnClickListener { editNote() }
        }
    }

    /**
     * Navigate to the Edit note screen.
     */
    private fun editNote() {
        val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToAddNoteFragment(
            getString(R.string.edit_fragment_title),
            note.id
        )
        this.findNavController().navigate(action)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.noteId
        // Retrieve the note details using the noteId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        viewModel.retrieveNote(id).observe(this.viewLifecycleOwner) { selectedNote ->
            note = selectedNote
            bind(note)
        }
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
