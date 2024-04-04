package com.example.newsapp.notifications

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.newsapp.R
import com.example.newsapp.util.Utils
import com.moengage.pushbase.model.NotificationPayload
import com.moengage.pushbase.push.PushMessageListener

class CustomPushMessageListener : PushMessageListener() {

    override fun onCreateNotification(
        context: Context,
        notificationPayload: NotificationPayload
    ): NotificationCompat.Builder? {
        var handledBySDK = "true"
        notificationPayload.payload.getString("handledBySDK")?.let { handledBySDK = it }
        if (handledBySDK.lowercase() == "false") {
            return NotificationCompat
                .Builder(context, notificationPayload.channelId)
                .setSmallIcon(R.drawable.location)
                .setContentTitle(notificationPayload.text.message)
        } else {
            return super.onCreateNotification(context, notificationPayload)
        }
    }
}