package com.peacemaker.android.courselearn.ui.util

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

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

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int? = null,
    val throwable: Throwable? = null,
    val status: Status
) {
    class Success<T>(data: T, message: String? = null, code: Int? = null) :
        NetworkResult<T>(data, message, code, status = Status.SUCCESS)

    class Error<T>(code: Int? = null, message: String, data: T? = null) :
        NetworkResult<T>(data, message, code, status = Status.ERROR)

    class ApiException<T>(cause: Throwable?, message: String?) :
        NetworkResult<T>(throwable = cause, message = message, status = Status.EXCEPTION)

    class Loading<T>(data: T?) :
        NetworkResult<T>(data, message = "loading..", status = Status.LOADING)
}

sealed class VerificationStatus<T>(
    val data: T? = null,
    val verificationId: String? = null,
    val token: T? = null,
    val exception: Exception? = null
) {
    class CodeSent<T>(verificationId: String, token: T) :
        VerificationStatus<T>(verificationId = verificationId, token = token)

    class Success<T>(data: T?) : VerificationStatus<T>(data = data)
    class Error<T>(exception: Exception) : VerificationStatus<T>(exception = exception)
}

enum class Status { SUCCESS, ERROR, EXCEPTION, LOADING }

inline fun <reified T> Fragment.liveDataObserver(
    liveData: MutableLiveData<NetworkResult<T>?>,
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (String?) -> Unit? = {},
    crossinline onApiException: (String?) -> Unit? = {},
    crossinline showLoading: (Boolean) -> Unit?
) {
    liveData.observe(viewLifecycleOwner) { result ->
        val apiEx = "Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 1 column 2 path $"
        when (result?.status) {
            Status.LOADING -> {
                showLoading.invoke(true)
            }
            Status.SUCCESS -> {
                showLoading.invoke(false)
                result.data?.let(onSuccess)
                Log.d("ObserveSuccessResponse:::::::::", "${result.data}")
            }
            Status.EXCEPTION -> {
                showLoading.invoke(false)
                onApiException.invoke(result.throwable?.message)
                if (result.throwable?.message == apiEx) {
                    Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
                }
                Log.d("ApiException", ":::::::::${result.throwable?.cause}")
            }
            Status.ERROR -> {
                showLoading.invoke(false)
                if (result.message?.contains(apiEx) == true) {
                    Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
                    onError.invoke("No Data Found")
                } else {
                    Toast.makeText(requireContext(), result.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    result.message?.let(onError)
                }
                Log.d("ObserveError", ":::::::::${result.message}")
            }

            else -> {}
        }
    }
}


data class CombinedResults(
    val responses: List<NetworkResult<out Any>?>
) {
    /**
     * A function to return the success response from the api when response code = 200 or
     * 504 which is API exception for response message "No data found" for my case
     * */
    fun isSuccess(): Boolean {
        return responses.all {
            it?.code == 200 || it?.code == 504
        }
    }

    val successMessage: String
        get() {
            val successRes = responses.filter { result -> result?.code == 200 }
            val successResList = successRes.mapIndexed { index, networkResult ->
                "Response ${index + 1} success: ${networkResult?.message}"
            }
            return successResList.joinToString(",\n")
        }

    val errorMessage: String
        get() {
            val failedResponses = responses.filter { it?.code != 200 }
            val errorMessageList = failedResponses.mapIndexed { index, response ->
                "Response ${index + 1} failed: ${response?.message}"
            }
            return errorMessageList.joinToString(",\n")
        }
}