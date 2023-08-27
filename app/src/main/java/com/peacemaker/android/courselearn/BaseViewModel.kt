package com.peacemaker.android.courselearn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BaseViewModel : ViewModel() {
    private val _unreadNotificationsCount = MutableLiveData<Int>()
    val unreadNotificationsCount: LiveData<Int> = _unreadNotificationsCount

    fun updateUnreadNotificationsCount(count: Int) {
        _unreadNotificationsCount.value = count
    }
}
