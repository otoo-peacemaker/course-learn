package com.peacemaker.android.courselearn.ui.util

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider


data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = null)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}


sealed class VerificationStatus {
    data class CodeSent(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken) : VerificationStatus()
    data class Success(val user: FirebaseUser?) : VerificationStatus()
    data class Error(val exception: Exception) : VerificationStatus()
}