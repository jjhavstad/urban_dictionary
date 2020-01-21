package com.solkismet.urbandictionary.ui.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkListenerHelper {
    companion object {
        private val networkRequest by lazy {
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        }

        fun registerNetworkStateChangeListener(
            connectivityManager: ConnectivityManager,
            onNetworkAvailable: () -> Unit,
            onNetworkUnavailable: () -> Unit
        ): ConnectivityManager.NetworkCallback {
            val networkCallback = object: ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    onNetworkAvailable()
                }

                override fun onLost(network: Network) {
                    onNetworkUnavailable()
                }

                override fun onUnavailable() {
                    onNetworkUnavailable()
                }
            }
            connectivityManager.registerNetworkCallback(
                networkRequest,
                networkCallback
            )
            return networkCallback
        }

        fun unregisterNetworkStateChangeListener(
            connectivityManager: ConnectivityManager,
            networkCallback: ConnectivityManager.NetworkCallback
        ) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}
