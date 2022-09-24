package com.a4gumel.taska.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a4gumel.taska.R
import com.a4gumel.taska.databinding.NoteItemLayoutBinding
import com.a4gumel.taska.model.Note
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak

class NotesRvAdapter: ListAdapter<Note, NotesRvAdapter.NotesViewHolder>(DiffUtilCallback()){

    inner class NotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val contentBinding = NoteItemLayoutBinding.bind(itemView)
        val cardView: MaterialCardView = contentBinding.noteItemCard
        val noteTitle: MaterialTextView = contentBinding.noteItemTitle
        val noteContent: MaterialTextView = contentBinding.noteItemContent
        val noteDate: MaterialTextView = contentBinding.noteItemDate

        val markWon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ){visitor, _ , -> visitor.forceNewLine()}
                }
            })
            .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        getItem(position).let { note ->
            holder.apply {
                noteTitle.text = note.title
                markWon.setMarkdown(noteContent, note.content)
                noteDate.text = note.date
                cardView.setBackgroundColor(note.color)

                itemView.setOnClickListener {

                }

                noteContent.setOnClickListener {

                }
            }
        }
    }
}