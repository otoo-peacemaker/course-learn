package com.peacemaker.android.courselearn.model


import com.google.gson.annotations.SerializedName

data class Duration(
    @SerializedName("hours")
    val hours: Int?=null,
    @SerializedName("minutes")
    val minutes: Int?=null
)