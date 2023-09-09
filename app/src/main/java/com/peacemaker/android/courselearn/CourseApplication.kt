package com.peacemaker.android.courselearn

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.CHANNEL_DESCRIPTION
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.CHANNEL_ID
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.CHANNEL_NAME

class CourseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID
            val channelName = CHANNEL_NAME
            val channelDescription = CHANNEL_DESCRIPTION

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}