package com.example.newsapp.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.newsapp.R
import com.example.newsapp.ui.MainActivity
import com.example.newsapp.util.Utils

/**
 * Service class for handling notifications in the News App.
 * Utilizes the NotificationCompat library for creating and displaying notifications.
 */

class NotificationService {

    companion object {

        // Package name for the notification service
        private const val packageName = "com.example.newsapp.notifications"

        /**
         * Builds and displays a notification using the NotificationCompat library.
         *
         * @param context The context in which the notification is displayed.
         * @param title The title of the notification.
         * @param description The description/body of the notification.
         */

        fun notificationBuilder(context: Context, title: String, description: String) {
            // Create an intent to open the MainActivity when the notification is clicked
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Create a PendingIntent to launch the MainActivity
            val pendingIntent = PendingIntent.getActivity(context,
                Utils.NOTIFICATION_REQUEST_CODE,intent, PendingIntent.FLAG_IMMUTABLE)

            // Build the notification using NotificationCompat
            var builder = NotificationCompat.Builder(context.applicationContext, Utils.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setVibrate(longArrayOf(2000))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setContent(generateNotificationView(context,title,description))

            // Get the NotificationManager
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create a notification channel for devices with API level 26 and above
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                val notificationChannel = NotificationChannel(Utils.NOTIFICATION_CHANNEL_ID,Utils.NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notificationChannel)
            }

            // Notify with the built notification
            notificationManager.notify(Utils.NOTIFICATION_REQUEST_CODE,builder.build())
        }

        /**
         * Generates a custom RemoteViews for the notification layout.
         *
         * @param context The context in which the notification is displayed.
         * @param title The title of the notification.
         * @param description The description/body of the notification.
         * @return A RemoteViews object representing the custom notification layout.
         */
        @SuppressLint("RemoteViewLayout")
        fun generateNotificationView (context: Context, title : String, description : String) : RemoteViews {
            val remoteView = RemoteViews(packageName, R.layout.notification_layout)
            remoteView.setTextViewText(R.id.notification_title,title)
            remoteView.setTextViewText(R.id.notification_description,description)
            remoteView.setImageViewResource(R.id.notification_banner,R.drawable.notification)
            return remoteView
        }

    }

}