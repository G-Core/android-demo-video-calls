package com.gcorelabs.videocallsdemo.screens.call

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gcore.videocalls.meet.GCoreMeet
import gcore.videocalls.meet.model.Peers

class RoomViewModel(application: Application) : AndroidViewModel(application) {

    private val _cameraState = MutableLiveData<CameraState>()
    val cameraState: LiveData<CameraState> = _cameraState

    private val _microphoneState = MutableLiveData<MicrophoneState>()
    val microphoneState: LiveData<MicrophoneState> = _microphoneState

    private val _interlocutors = GCoreMeet.instance.getPeers()
    val interlocutors: LiveData<Peers?> = _interlocutors

    private val _layoutManagerSpanCount = MutableLiveData<Int>()
    val layoutManagerSpanCount: LiveData<Int> = _layoutManagerSpanCount

    init {
        _cameraState.value = CameraState.ENABLE
        _microphoneState.value = MicrophoneState.UN_MUTE
    }

    fun startCall() {
        GCoreMeet.instance.startConnection(getApplication())
        connect()

        when (cameraState.value!!) {
            CameraState.ENABLE -> GCoreMeet.instance.roomManager.options.startWithCam = true
            CameraState.DISABLE -> GCoreMeet.instance.roomManager.options.startWithCam = false
        }
        when (microphoneState.value!!) {
            MicrophoneState.UN_MUTE -> GCoreMeet.instance.roomManager.options.startWithMic = true
            MicrophoneState.MUTE -> GCoreMeet.instance.roomManager.options.startWithMic = false
        }

        if (GCoreMeet.instance.roomManager.isClosed()) {
            GCoreMeet.instance.roomManager.join()
        }
    }

    fun closeCall() {
        GCoreMeet.instance.roomManager.destroyRoom()
    }

    private fun connect() {
        GCoreMeet.instance.getPeers().observeForever { peers: Peers? ->
            val peersCount = peers?.allPeers?.size ?: 0

            if (peersCount > 1) {
                _layoutManagerSpanCount.value = 2
            } else {
                _layoutManagerSpanCount.value = 1
            }
        }
    }

    fun enableCamera() {
        GCoreMeet.instance.roomManager.enableCam()
        _cameraState.value = CameraState.ENABLE
    }

    fun disableCamera() {
        GCoreMeet.instance.roomManager.disableCam()
        _cameraState.value = CameraState.DISABLE
    }

    fun unMuteMicrophone() {
        if (!GCoreMeet.instance.roomManager.isMicEnabled()) {
            GCoreMeet.instance.roomManager.enableMic()
        } else {
            GCoreMeet.instance.roomManager.unmuteMic()
        }
        _microphoneState.value = MicrophoneState.UN_MUTE
    }

    fun muteMicrophone() {
        GCoreMeet.instance.roomManager.muteMic()
        _microphoneState.value = MicrophoneState.MUTE
    }

    fun switchCamera() {
        GCoreMeet.instance.roomManager.changeCam()
    }
}