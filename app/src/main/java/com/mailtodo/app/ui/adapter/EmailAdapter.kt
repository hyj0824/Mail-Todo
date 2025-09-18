package com.mailtodo.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mailtodo.app.data.model.Email
import com.mailtodo.app.databinding.ItemEmailBinding
import java.text.SimpleDateFormat
import java.util.*

class EmailAdapter(
    private val onEmailClick: (Email) -> Unit
) : ListAdapter<Email, EmailAdapter.EmailViewHolder>(EmailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val binding = ItemEmailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EmailViewHolder(
        private val binding: ItemEmailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(email: Email) {
            binding.apply {
                textSubject.text = email.subject
                textFrom.text = email.from
                textDate.text = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
                    .format(email.receivedDate)

                textSummary.text = email.summary ?: email.content.take(100) + "..."

                // Show date indicator if email contains dates
                iconDateIndicator.visibility = if (email.hasDateContent)
                    android.view.View.VISIBLE else android.view.View.GONE

                // Show processed indicator
                iconProcessed.visibility = if (email.isProcessed)
                    android.view.View.VISIBLE else android.view.View.GONE

                root.setOnClickListener {
                    onEmailClick(email)
                }

                // Set read/unread styling
                root.alpha = if (email.isRead) 0.7f else 1.0f
            }
        }
    }

    class EmailDiffCallback : DiffUtil.ItemCallback<Email>() {
        override fun areItemsTheSame(oldItem: Email, newItem: Email): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Email, newItem: Email): Boolean {
            return oldItem == newItem
        }
    }
}
