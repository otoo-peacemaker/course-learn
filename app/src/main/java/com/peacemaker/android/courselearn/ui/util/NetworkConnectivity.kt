package com.peacemaker.android.courselearn.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.telephony.TelephonyManager

class NetworkConnectivity(context: Context) {

    private var isConnected: Boolean = false
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnected = true
        }
        override fun onLost(network: Network) {
            super.onLost(network)
            isConnected = false
        }
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

    /**
     * Check if the connection is fast
     * @param type
     * @param subType
     * @return
     */
    fun isConnectionFast(type: Int, subType: Int): Boolean {
        return when (type) {
            ConnectivityManager.TYPE_WIFI -> true
            ConnectivityManager.TYPE_MOBILE -> when (subType) {
                TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
                TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
                TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
                TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
                TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
                TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
                TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                // API level 11
                TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
                // API level 9
                TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
                // API level 13
                TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
                // API level 8
                TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
                // API level 11
                TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
                // Unknown
                TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
                else -> false
            }
            else -> false
        }
    }

}
