package com.example.kotlinassessmentactivity

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kotlinassessmentactivity.data.Note
import com.example.kotlinassessmentactivity.databinding.FragmentAddNoteBinding

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
    private val navigationArgs: NoteDetailFragmentArgs by navArgs()

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
            saveAction.setOnClickListener { updateNote() }
        }
    }

    /**
     * Inserts the new Note into database and navigates up to list fragment.
     */
    private fun addNewNote() {
        if (isEntryValid()) {
            viewModel.addNewNote(
                binding.noteTitle.text.toString(),
                binding.noteText.text.toString(),
                System.currentTimeMillis(),
            )
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNoteListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Updates an existing Note in the database and navigates up to list fragment.
     */
    private fun updateNote() {
        if (isEntryValid()) {
            viewModel.updateNote(
                this.navigationArgs.noteId,
                this.binding.noteTitle.text.toString(),
                this.binding.noteText.text.toString(),
                System.currentTimeMillis(),
            )
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNoteListFragment()
            findNavController().navigate(action)
        }
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
        } else {
            binding.saveAction.setOnClickListener {
                addNewNote()
            }
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
