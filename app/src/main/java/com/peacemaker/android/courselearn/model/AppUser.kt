package com.peacemaker.android.courselearn.model

import com.peacemaker.android.courselearn.ui.util.VerificationStatus

data class AppUser(
    val id: String?=null,
    val name: String?=null,
    val email: String?=null,
    val phoneNumber: String?=null,
    val password: String?=null,
    val profileImage: String?=null,
    val status: String?=null,
    val courses : Courses?=null
)
