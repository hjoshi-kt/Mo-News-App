package com.example.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.ActivityNotificationCenterBinding
import com.example.newsapp.ui.adapters.Listeners
import com.example.newsapp.ui.adapters.NotificationCenterCustomAdapter
import com.example.newsapp.util.Utils
import com.moengage.inbox.core.MoEInboxHelper
import com.moengage.inbox.core.model.InboxMessage

class NotificationCenterActivity : AppCompatActivity(), Listeners {

    private lateinit var binding : ActivityNotificationCenterBinding
    private lateinit var adapter : NotificationCenterCustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MoEInboxHelper.getInstance().fetchAllMessagesAsync(applicationContext) {
            it?.let {
                adapter = NotificationCenterCustomAdapter(it.inboxMessages.toMutableList(), this, this)
                binding.notificationRecyclerView.adapter = adapter
                val layoutManager = LinearLayoutManager(this)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.notificationRecyclerView.layoutManager = layoutManager
            }
        }
    }

    override fun onMessageDelete(messages: MutableList<InboxMessage>, position: Int) {
        messages.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, messages.size)
    }

    override fun onMessageClick(messages: MutableList<InboxMessage>, position: Int) {
        Log.d(Utils.NEWS_APP_LOG,messages[position].toString())
        MoEInboxHelper.getInstance().trackMessageClicked(this, messages[position])
    }
}