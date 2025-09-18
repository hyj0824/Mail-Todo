package com.mailtodo.app.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mailtodo.app.data.model.TodoItem
import com.mailtodo.app.databinding.ItemTodoBinding
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(
    private val onTodoClick: (TodoItem) -> Unit,
    private val onTodoDelete: (TodoItem) -> Unit
) : ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TodoViewHolder(
        private val binding: ItemTodoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: TodoItem) {
            binding.apply {
                textTitle.text = todo.title
                textDescription.text = todo.description

                if (todo.dueDate != null) {
                    textDueDate.text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                        .format(todo.dueDate)
                    textDueDate.visibility = android.view.View.VISIBLE
                } else {
                    textDueDate.visibility = android.view.View.GONE
                }

                checkBoxCompleted.isChecked = todo.isCompleted

                // Apply strikethrough for completed items
                if (todo.isCompleted) {
                    textTitle.paintFlags = textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    root.alpha = 0.6f
                } else {
                    textTitle.paintFlags = textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    root.alpha = 1.0f
                }

                // Show sync status
                when (todo.syncStatus) {
                    com.mailtodo.app.data.model.SyncStatus.SYNCED -> {
                        iconSyncStatus.setImageResource(android.R.drawable.ic_dialog_info)
                        iconSyncStatus.setColorFilter(android.graphics.Color.GREEN)
                    }
                    com.mailtodo.app.data.model.SyncStatus.FAILED -> {
                        iconSyncStatus.setImageResource(android.R.drawable.ic_dialog_alert)
                        iconSyncStatus.setColorFilter(android.graphics.Color.RED)
                    }
                    else -> {
                        iconSyncStatus.setImageResource(android.R.drawable.ic_popup_sync)
                        iconSyncStatus.setColorFilter(android.graphics.Color.GRAY)
                    }
                }

                checkBoxCompleted.setOnClickListener {
                    onTodoClick(todo)
                }

                buttonDelete.setOnClickListener {
                    onTodoDelete(todo)
                }

                root.setOnClickListener {
                    onTodoClick(todo)
                }
            }
        }
    }

    class TodoDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem == newItem
        }
    }
}
