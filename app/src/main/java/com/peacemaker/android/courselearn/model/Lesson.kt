package com.peacemaker.android.courselearn.model


import com.google.gson.annotations.SerializedName

data class Lesson(
    @SerializedName("content")
    val content: String?=null,
    @SerializedName("duration")
    val duration: Duration?=null,
    @SerializedName("id")
    val id: Int?=null,
    @SerializedName("title")
    val title: String?=null
)