package com.example.newsapp.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.databinding.MoNotificationCenterItemBinding
import com.example.newsapp.databinding.NewsSingleItemBinding
import com.example.newsapp.models.Articles
import com.example.newsapp.util.Utils
import com.moengage.core.internal.utils.accountMetaForInstance
import com.moengage.inbox.core.MoEInboxHelper
import com.moengage.inbox.core.model.InboxMessage
import com.moengage.inbox.ui.MoEInboxUiHelper
import com.moengage.inbox.ui.adapter.InboxAdapter
import com.moengage.inbox.ui.adapter.InboxListAdapter
import com.moengage.inbox.ui.adapter.ViewHolder
import com.moengage.inbox.ui.listener.OnMessageClickListener
import com.moengage.inbox.ui.model.MessageClickData

class NotificationCenterAdapter : InboxAdapter(), OnMessageClickListener {
    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemViewType(position: Int, inboxMessage: InboxMessage): Int {
        return 0
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        position: Int,
        inboxMessage: InboxMessage,
        inboxListAdapter: InboxListAdapter
    ) {
        val binding = (viewHolder as NotificationViewHolder).binding
        binding.title.text = inboxMessage.textContent.title
        binding.message.text = inboxMessage.textContent.message

        binding.card.setOnClickListener {
            inboxListAdapter.onItemClicked(position, inboxMessage)
        }

        MoEInboxUiHelper.getInstance().setOnMessageClickListener(this)

        binding.delete.setOnClickListener{
            inboxListAdapter.deleteItem(position, inboxMessage)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = MoNotificationCenterItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return NotificationViewHolder(binding)
    }

    class NotificationViewHolder(val binding: MoNotificationCenterItemBinding) : ViewHolder(binding.root)

    override fun onMessageClick(data: MessageClickData): Boolean {
        return true
    }

}