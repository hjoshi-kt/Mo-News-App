package com.example.newsapp.notifications

import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.newsapp.R
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

    override fun isNotificationRequired(context: Context, payload: Bundle): Boolean {
        var show = "true"
        payload.getString("show")?.let { show = it }
        super.isNotificationRequired(context, payload)
        return show.toBoolean()
    }
}