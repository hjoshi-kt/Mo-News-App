package com.example.newsapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.databinding.MoNotificationCenterItemBinding
import com.moengage.inbox.core.model.InboxMessage

class NotificationCenterCustomAdapter(val messages : MutableList<InboxMessage>, val context : Context, val listener : Listeners) : RecyclerView.Adapter<NotificationCenterCustomAdapter.NotificationCenterCustomViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationCenterCustomViewHolder {
        val binding = MoNotificationCenterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationCenterCustomViewHolder(binding)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: NotificationCenterCustomViewHolder, position: Int) {
        val binding = holder.binding
        binding.title.text = messages[position].textContent.title
        binding.message.text = messages[position].textContent.message
        binding.summary.text = messages[position].textContent.summary

        binding.delete.setOnClickListener{
            listener.onMessageDelete(messages, position)
        }

        binding.card.setOnClickListener {
            listener.onMessageClick(messages,position)
        }
    }

    class NotificationCenterCustomViewHolder(val binding: MoNotificationCenterItemBinding) : RecyclerView.ViewHolder(binding.root)

}

interface Listeners {
    fun onMessageDelete(messages : MutableList<InboxMessage>, position : Int)
    fun onMessageClick(messages : MutableList<InboxMessage>, position : Int)
}