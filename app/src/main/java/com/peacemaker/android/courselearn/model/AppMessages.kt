package com.peacemaker.android.courselearn.model

import android.os.Parcelable
import com.peacemaker.android.courselearn.ui.util.VerificationStatus
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class AppMessages(
val name: String?=null,
val time:Long?=null,
val status: String?=null,
val content: @RawValue MessageBody?=null
) : Parcelable


@Parcelize
data class MessageBody(
    val desc: String?=null,
    val profileImg: String?=null
):Parcelable

