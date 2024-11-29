package com.cs407.memora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
 import java.text.SimpleDateFormat
 import java.util.Calendar
 import java.util.Date
 import java.util.Locale

// ChatAdapter.kt
class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.message_text)
        val timestampText: TextView = view.findViewById(R.id.timestamp_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_chat_user
        } else {
            R.layout.item_chat_assistant
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text
        holder.timestampText.text = formatTimestamp(message.timestamp)
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_ASSISTANT
    }

    companion object {
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_ASSISTANT = 2
    }

    private fun formatTimestamp(timestamp: Date): String {
        val calendar = Calendar.getInstance()
        val messageTime = Calendar.getInstance().apply {
            time = timestamp
        }

        return if (calendar.get(Calendar.DATE) == messageTime.get(Calendar.DATE) &&
            calendar.get(Calendar.MONTH) == messageTime.get(Calendar.MONTH) &&
            calendar.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR)
        ) {
            // Today - show time
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)
        } else {
            // Different day - show date and time
            SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(timestamp)
        }
    }
}