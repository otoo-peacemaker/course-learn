package com.peacemaker.android.courselearn.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.RawValue


@Parcelize
data class CoursesItem(
    @SerializedName("authorName")
    val authorName: String?=null,
    @SerializedName("courseAbout")
    val courseAbout: String?=null,
    @SerializedName("courseBgImg")
    val courseBgImg: String?=null,
    @SerializedName("courseName")
    val courseName: String?=null,
    @SerializedName("duration")
    val duration: @RawValue Duration?=null,
    @SerializedName("lessons")
    val lessons: @RawValue List<Lesson>?=null,
    @SerializedName("price")
    val price: String?=null
):Parcelable