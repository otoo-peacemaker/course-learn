package com.peacemaker.android.courselearn.model

data class AppUser(
    val id: String?=null,
    val name: String?=null,
    val email: String?=null,
    val phoneNumber: String?=null,
    val password: String?=null,
    val profileImage: String?=null,
    val courses : Courses?=null
)
