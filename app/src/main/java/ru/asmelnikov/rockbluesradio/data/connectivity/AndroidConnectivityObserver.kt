package ru.asmelnikov.rockbluesradio.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.asmelnikov.rockbluesradio.domain.connectivity.ConnectivityObserver

class AndroidConnectivityObserver(context: Context) : ConnectivityObserver {

    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    override val isConnected: Flow<Boolean> = callbackFlow {
        val defaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(capabilities.hasInternet())
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        trySend(isCurrentlyConnected())
        connectivityManager.registerDefaultNetworkCallback(defaultNetworkCallback)

        awaitClose { connectivityManager.unregisterNetworkCallback(defaultNetworkCallback) }
    }.distinctUntilChanged()

    private fun isCurrentlyConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        return connectivityManager.getNetworkCapabilities(activeNetwork)?.hasInternet() == true
    }

    private fun NetworkCapabilities.hasInternet(): Boolean =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
