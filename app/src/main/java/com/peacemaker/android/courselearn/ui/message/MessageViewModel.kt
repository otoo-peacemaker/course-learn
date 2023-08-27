package com.peacemaker.android.courselearn.ui.message

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peacemaker.android.courselearn.ui.util.ApplicationNotificationManager

class MessageViewModel : ViewModel() {
    val unreadNotificationsCountLiveData: LiveData<Int>
        get() = _unreadNotificationsCountLiveData
    private val _unreadNotificationsCountLiveData = MutableLiveData(0)


    fun sendNotification(activity: Activity,title:String, message:String) {
        // Code to send a notification
         ApplicationNotificationManager.sendNotification(activity,title, message)
        _unreadNotificationsCountLiveData.value = _unreadNotificationsCountLiveData.value?.plus(1)
    }

    fun markNotificationAsRead() {
        _unreadNotificationsCountLiveData.value = _unreadNotificationsCountLiveData.value?.minus(1)
    }


}