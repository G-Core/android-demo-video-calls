package com.gcorelabs.videocallsdemo.screens.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager

class HeadsetStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = intent.getIntExtra("state", 0) == 1

        val audioManager =
            context.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = !isConnected

        if (isConnected) {
            audioManager.mode = AudioManager.MODE_IN_CALL
        } else {
            audioManager.mode = AudioManager.MODE_NORMAL
        }
    }
}