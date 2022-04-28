package com.gcorelabs.videocallsdemo

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gcore.videocalls.meet.GCoreMeet

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initGCoreMeet()
        initInternetAvailabilityCheck()
    }

    private fun initGCoreMeet() = with(GCoreMeet.instance) {
        init(this@MainApp, null, false)

        webRtcHost = getString(R.string.default_webrtc_host)
        port = resources.getInteger(R.integer.default_webrtc_port)
        clientHostName = getString(R.string.default_client_host_name)
        hostName = getString(R.string.default_host_name)
    }

    private fun initInternetAvailabilityCheck() {
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .apply {
                registerDefaultNetworkCallback(defaultNetworkCallback)
            }
    }

    private val defaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            mutableNetworkIsAvailable.postValue(true)
        }

        override fun onUnavailable() {
            mutableNetworkIsAvailable.postValue(false)
        }

        override fun onLost(network: Network) {
            mutableNetworkIsAvailable.postValue(false)
        }
    }

    companion object {
        private val mutableNetworkIsAvailable = MutableLiveData(false)
        val isAvailableNetwork: LiveData<Boolean> = mutableNetworkIsAvailable
    }
}