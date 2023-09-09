package com.peacemaker.android.courselearn.ui.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.peacemaker.android.courselearn.ui.util.Utils.Constants.PERMISSION_REQUEST_CODE

class NetworkConnectivity(val context: Context) {
    private val permission = Manifest.permission.READ_PHONE_STATE
    private val granted = PackageManager.PERMISSION_GRANTED

    private var isConnected: Boolean = false
    private var isStrongNetwork: Boolean = false
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnected = true
            isStrongNetwork = isStrongNetwork() == true
            Utils.showToast(context, "Network Available")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isConnected = false
            isStrongNetwork = false
            Utils.showToast(context, "Network Lost")
        }

        override fun onUnavailable() {
            super.onUnavailable()
            isConnected = false
            isStrongNetwork = false
            Utils.showToast(context, "Network onUnavailable")
        }
    }

    init {
        checkStrongConnectivity()
    }

    fun startListening() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun stopListening() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun isConnected(): Boolean {
        return isConnected
    }

    fun isStrongNetwork(): Boolean? {
        if (ContextCompat.checkSelfPermission(context, permission) == granted) {
            // Permission is granted, you can access network type
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            // Check if the network type indicates a strong network for data transmission
            return when (telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_LTE, // 4G
                TelephonyManager.NETWORK_TYPE_HSPAP, // 3G
                TelephonyManager.NETWORK_TYPE_HSDPA, // 3G
                TelephonyManager.NETWORK_TYPE_HSUPA, // 3G
                TelephonyManager.NETWORK_TYPE_UMTS -> true // 3G
                else -> false
            }
        } else {
            // Permission is not granted, request it from the user
            // You can use ActivityCompat.requestPermissions to request the permission
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(permission), PERMISSION_REQUEST_CODE
            )
            return null
        }
    }

    private fun isStrongNetworkConnected(): Boolean {
        return isConnected && isStrongNetwork
    }

    private fun checkStrongConnectivity() {
        if (isStrongNetworkConnected()) Utils.showToast(
            context,
            "Strong network"
        ) else Utils.showToast(context, "Weak network")
    }
}
