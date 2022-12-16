package com.example.kotlinassessmentactivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinassessmentactivity.data.Note
import com.example.kotlinassessmentactivity.data.getDateTime
import com.example.kotlinassessmentactivity.databinding.NoteListItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class NoteListAdapter(private val onNoteClicked: (Note) -> Unit) :
    ListAdapter<Note, NoteListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            NoteListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onNoteClicked(current)
        }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: NoteListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.noteTitle.text = note.noteTitle
            binding.noteText.text = note.noteText
            binding.noteUpdateAt.text = note.getDateTime()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote === newNote
            }

            override fun areContentsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote.noteTitle == newNote.noteTitle
            }
        }
    }
}
