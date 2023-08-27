package com.peacemaker.android.courselearn.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Notification(
    val title: String?=null,
    val time:Long?=null,
    val content: @RawValue Any?=null
) : Parcelable

@Parcelize
data class Content(
    val details: String?=null,
    val contentImg: String?=null
):Parcelable
