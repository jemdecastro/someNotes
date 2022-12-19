package com.example.kotlinassessmentactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlinassessmentactivity.databinding.NoteListFragmentBinding

/**
 * Main fragment displaying details for all items in the database.
 */
class NoteListFragment : Fragment() {
    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database.noteDao()
        )
    }

    private var _binding: NoteListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NoteListAdapter {
            val action = NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(
                getString(R.string.edit_fragment_title),
                it.id
            )
            this.findNavController().navigate(action)
//
//            val action = NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(
//                it.id
//            )
//            this.findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 2)

        binding.recyclerView.adapter = adapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        viewModel.allNotes.observe(this.viewLifecycleOwner) { notes ->
            notes.let {
                adapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val action = NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }
    }
}
