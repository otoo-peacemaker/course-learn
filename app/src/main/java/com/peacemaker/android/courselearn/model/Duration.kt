package com.peacemaker.android.courselearn.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Duration(
    @SerializedName("hours")
    val hours: Int?=null,
    @SerializedName("minutes")
    val minutes: Int?=null
):Parcelable