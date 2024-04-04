package com.example.newsapp.notifications

import android.util.Log
import com.example.newsapp.util.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moengage.firebase.MoEFireBaseHelper
import com.moengage.pushbase.MoEPushHelper

/**
 * Service class for handling Firebase Cloud Messaging (FCM) notifications.
 * Extends [FirebaseMessagingService].
 */

class FCMNotificationService : FirebaseMessagingService() {

    /**
     * Called when a new FCM token is generated for the device.
     *
     * @param token The new FCM token for the device.
     */

    override fun onNewToken(token: String) {
        Log.d(Utils.NEWS_APP_LOG, "device token")
        Log.d(Utils.NEWS_APP_LOG, token)
        MoEFireBaseHelper.getInstance().passPushToken(applicationContext,token)
        super.onNewToken(token)
    }

    /**
     * Called when a new FCM message is received.
     *
     * @param message The received FCM message.
     */

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(Utils.NEWS_APP_LOG,"message received")
        Log.d(Utils.NEWS_APP_LOG, message.data.toString())
        // Build and display a notification based on the received FCM message
        if (MoEPushHelper.getInstance().isFromMoEngagePlatform(message.data)) {
            MoEFireBaseHelper.getInstance().passPushPayload(applicationContext, message.data)
        } else {
            NotificationService.notificationBuilder(this, message.notification?.title?:"", message.notification?.body?:"")
        }
    }
}