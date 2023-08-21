package com.peacemaker.android.courselearn.ui.courses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peacemaker.android.courselearn.data.FirebaseHelper
import com.peacemaker.android.courselearn.model.CoursesItem
import com.peacemaker.android.courselearn.ui.util.Resource

class CourseViewModel : ViewModel() {

    private val _addCourseLiveData = MutableLiveData<Resource<String>>()
    val addCourseLiveData: LiveData<Resource<String>> = _addCourseLiveData


    fun addToCourse(coursesItem: CoursesItem?){
        FirebaseHelper.UserDataCollection().addUserData(coursesItem as Any,"users","my_courses"){ success, message->
            if (success) _addCourseLiveData.value = Resource.success(message)
            else _addCourseLiveData.value = Resource.error(null,message)
        }
    }
}