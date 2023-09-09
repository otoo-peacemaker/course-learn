package com.peacemaker.android.courselearn.ui.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.CHANNEL_ID
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.CHANNEL_NAME
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.NOTIFICATION_ID

class AppFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Handle the new token here, for example, you can send it to your server
        // to associate it with the user's account or perform any other necessary actions.
        // You can use this token to send notifications to this specific device.
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
       // sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle the incoming message and extract data
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
        val otherMessagingService = remoteMessage.data

        // Create an intent to open the desired activity or fragment
        //using the nav graph
        val intentGraph = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_message)
            .createPendingIntent()

        // Create a notification
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ico_message)
            .setContentIntent(intentGraph)
            .setAutoCancel(true)
            .addAction(
                R.drawable.google,
                "View content",
                intentGraph)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Check for Android Oreo and higher for notification channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}
