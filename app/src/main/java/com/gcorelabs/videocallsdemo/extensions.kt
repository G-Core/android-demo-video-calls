package com.gcorelabs.videocallsdemo

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.isAvailableNetwork() = MainApp.isAvailableNetwork.value!!

fun Fragment.showShortToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showShortToast(messageId: Int) {
    Toast.makeText(requireContext(), messageId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showLongToast(messageId: Int) {
    Toast.makeText(requireContext(), messageId, Toast.LENGTH_LONG).show()
}