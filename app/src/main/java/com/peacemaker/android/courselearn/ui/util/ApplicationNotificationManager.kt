package com.peacemaker.android.courselearn.ui.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.peacemaker.android.courselearn.MainActivity
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.ui.util.Constants.CHANNEL_ID
import com.peacemaker.android.courselearn.ui.util.Constants.NOTIFICATION_ID

object ApplicationNotificationManager {
    private var unreadNotificationsCount = 0

    @SuppressLint("MissingPermission")
    fun sendNotification(context: Activity, title: String, message: String) {
        val channelId = CHANNEL_ID
        val notificationId = NOTIFICATION_ID // Use a unique notification ID

        unreadNotificationsCount++
        updateBadgeCount(context)

        // Create an intent to open the NotificationActivity with fragment identifier
        val intent = Intent(context.applicationContext, MainActivity::class.java).apply {
            putExtra(
                "fragmentToOpen",
                "MessageDetailsFragment"
            ) // Replace with your fragment identifier
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        //using the nav graph
        val intentGraph = NavDeepLinkBuilder(context.applicationContext)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_message)
            .createPendingIntent()

        // Check if notifications are enabled
        if (NotificationManagerCompat.from(context.applicationContext).areNotificationsEnabled()) {
            val notification: Notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.avatar_person)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(intentGraph)
                .addAction(
                    R.drawable.google,
                    "View details",
                    intentGraph)
                .setAutoCancel(true)
                .build()

            // Send the notification
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, notification)
        } else {
            // Notifications are disabled, prompt the user to enable notifications
            showEnableNotificationsDialog(context)
        }
    }

    // Show a dialog to prompt the user to enable notifications
    private fun showEnableNotificationsDialog(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle("Enable Notifications")
            .setMessage("Notifications are currently disabled. Enable notifications to receive important updates.")
            .setPositiveButton("Open Settings") { _, _ ->
                openNotificationSettings(context)
            }
            .setNegativeButton("Cancel", null)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
    // Open the app's notification settings
    private fun openNotificationSettings(context: Context) {
        val intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        }
        context.startActivity(intent)
    }

    // Function to update the badge count on the bottom navigation
     fun updateBadgeCount(activity: Activity) {
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.nav_view)
        val menuItemId = R.id.navigation_message // Update with the correct menu item ID
        val menuItem = bottomNavigationView.menu.findItem(menuItemId)

        // Create or update the badge
        val badgeDrawable = BadgeDrawable.create(activity)
        badgeDrawable.horizontalOffset = BadgeDrawable.TOP_END
        if (unreadNotificationsCount > 0) {
            badgeDrawable.number = unreadNotificationsCount
            menuItem.icon = badgeDrawable
        } else {
            menuItem.icon = null // Remove the badge
        }
    }

}