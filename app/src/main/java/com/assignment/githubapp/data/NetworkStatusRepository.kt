package com.assignment.githubapp.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.RemoteException
import com.assignment.githubapp.model.NetworkStatusState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This Repository manages listening to changes in the network state of the device and
 * emits the network change via a [StateFlow].
 *
 * Typical usage should be using the NetworkStatusViewModel to listen for changes to the [state].
 *
 * This class should be a Singleton and self manages when to register/unregister itself as a
 * listener to networking changes by only registering if it has an active observer of it's
 * state. Once it no longer has active observers it will unregister itself.
 *
 * For devices running [Build.VERSION_CODES.N] or greater, it will use the new [ConnectivityManager.NetworkCallback]
 * and for devices older than N it will use a [BroadcastReceiver].
 *
 */

class NetworkStatusRepository @Inject constructor(
    private val context: Context,
    private val mainDispatcher: CoroutineDispatcher,
    private val appScope: CoroutineScope
) {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var callback: ConnectivityManager.NetworkCallback? = null
    private var receiver: ConnectivityReceiver? = null

    private val _state = MutableStateFlow(getCurrentNetwork())
    val state: StateFlow<NetworkStatusState> = _state

    init {
        _state
            .subscriptionCount
            .map { count -> count > 0 } // map count into active/inactive flag
            .distinctUntilChanged() // only react to true<->false changes
            .onEach { isActive ->
                /** Only subscribe to network callbacks if we have an active subscriber */
                if (isActive) subscribe()
                else unsubscribe()
            }
            .launchIn(appScope)
    }

    /* fetching network connection status synchronously */
    fun hasNetworkConnection() = getCurrentNetwork() == NetworkStatusState.NetworkStatusConnected

    private fun getCurrentNetwork(): NetworkStatusState {
        return try {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .let { connected ->
                    if (connected == true) NetworkStatusState.NetworkStatusConnected
                    else NetworkStatusState.NetworkStatusDisconnected
                }
        } catch (e: RemoteException) {
            NetworkStatusState.NetworkStatusDisconnected
        }
    }

    private fun subscribe() {

        if (callback != null || receiver != null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            callback = NetworkCallbackImpl().also { connectivityManager.registerDefaultNetworkCallback(it) }
        } else {
            receiver = ConnectivityReceiver().also {
                context.registerReceiver(it, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }
        }

        /* emit our initial state */
        emitNetworkState(getCurrentNetwork())
    }

    private fun unsubscribe() {

        if (callback == null && receiver == null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            callback?.run { connectivityManager.unregisterNetworkCallback(this) }
            callback = null
        } else {
            receiver?.run { context.unregisterReceiver(this) }
            receiver = null
        }
    }

    private fun emitNetworkState(newState: NetworkStatusState) {
        appScope.launch(mainDispatcher) {
            _state.emit(newState)
        }
    }

    private inner class ConnectivityReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            /** emit the new network state */
            intent
                .getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
                ?.isConnectedOrConnecting
                .let { connected ->
                    if (connected == true) emitNetworkState(NetworkStatusState.NetworkStatusConnected)
                    else emitNetworkState(getCurrentNetwork())
                }
        }
    }

    private inner class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = emitNetworkState(NetworkStatusState.NetworkStatusConnected)
        override fun onLost(network: Network) = emitNetworkState(NetworkStatusState.NetworkStatusDisconnected)
    }

}