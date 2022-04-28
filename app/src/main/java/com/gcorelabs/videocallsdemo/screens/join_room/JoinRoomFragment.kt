package com.gcorelabs.videocallsdemo.screens.join_room

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gcorelabs.videocallsdemo.R
import com.gcorelabs.videocallsdemo.databinding.FragmentJoinRoomBinding
import com.gcorelabs.videocallsdemo.isAvailableNetwork
import com.gcorelabs.videocallsdemo.screens.call.RoomFragment
import com.gcorelabs.videocallsdemo.showShortToast
import gcore.videocalls.meet.GCoreMeet

class JoinRoomFragment : Fragment(R.layout.fragment_join_room) {

    private lateinit var binding: FragmentJoinRoomBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentJoinRoomBinding.bind(view)

        if (!allPermissionGranted()) {
            requestPermissions.launch(REQUIRED_PERMISSIONS)
        }

        binding.joinBtn.setOnClickListener {

            if (isAvailableNetwork()) {
                configureCall()
                routeToRoom()
            } else {
                showShortToast(R.string.check_internet_connection)
            }
        }
    }

    private fun routeToRoom() = findNavController().navigate(
        R.id.action_joinRoomFragment_to_roomFragment,
        bundleOf(
            RoomFragment.ENABLE_CAM to binding.toggleVideo.isChecked,
            RoomFragment.ENABLE_MIC to binding.toggleAudio.isChecked
        )
    )

    private fun configureCall() {
        with(GCoreMeet.instance) {
            roomManager.displayName = binding.etUserName.text.toString()
            setRoomId(binding.etRoomId.text.toString())
            clientHostName = binding.etClientHostName.text.toString()
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (!allPermissionGranted()) {
            requireActivity().finish()
        }
    }

    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}