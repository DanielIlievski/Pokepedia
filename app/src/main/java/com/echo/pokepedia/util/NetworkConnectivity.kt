package com.echo.pokepedia.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LifecycleObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkConnectivity @Inject constructor(
    @ApplicationContext context: Context
): LifecycleObserver {
    var isNetworkAvailable = false

    init {
        initializeNetworkAvailability(context)
    }

    @SuppressLint("NewApi")
    private fun initializeNetworkAvailability(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
            if (hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                isNetworkAvailable = true
            }
        }
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isNetworkAvailable = true
            }

            override fun onLost(network: Network) {
                isNetworkAvailable = false
            }

            override fun onUnavailable() {
                isNetworkAvailable = false
            }
        }
        )

    }

    companion object {
        private var instance : NetworkConnectivity? = null

        fun instance(context: Context): NetworkConnectivity {
            return instance ?: synchronized(this) {
                instance ?: NetworkConnectivity(context.applicationContext).also { instance = it }
            }
        }
    }
}
