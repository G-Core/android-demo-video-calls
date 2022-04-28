package com.gcorelabs.videocallsdemo.screens.call

import android.content.Context
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gcorelabs.videocallsdemo.MainApp
import com.gcorelabs.videocallsdemo.R
import com.gcorelabs.videocallsdemo.databinding.FragmentRoomBinding
import com.gcorelabs.videocallsdemo.showShortToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.roundToInt

class RoomFragment : Fragment(R.layout.fragment_room) {

    private val intentFilter = IntentFilter(AudioManager.ACTION_HEADSET_PLUG)
    private var headsetStateReceiver: HeadsetStateReceiver? = null

    private lateinit var binding: FragmentRoomBinding
    private val viewModel: RoomViewModel by lazy {
        ViewModelProvider(this).get(RoomViewModel::class.java)
    }

    private val interlocutorsAdapter = InterlocutorsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        headsetStateReceiver = HeadsetStateReceiver()
        requireActivity().registerReceiver(headsetStateReceiver, intentFilter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRoomBinding.bind(view)

        checkInternet()
        startCall()
        configureUI()
        configureButtons()
        configureInterlocutors()
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(headsetStateReceiver)
        (requireContext().applicationContext
            ?.getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
            isSpeakerphoneOn = false
            mode = AudioManager.MODE_NORMAL
        }

        super.onDestroy()
        Log.e("RoomFragment", "onDestroy")
    }

    private fun checkInternet() {
        MainApp.isAvailableNetwork.observe(viewLifecycleOwner) { isAvailable ->
            if (!isAvailable) {
                viewModel.closeCall()
                showShortToast(R.string.check_internet_connection)
                findNavController().popBackStack()
            }
        }
    }

    private fun startCall() {
        if (requireArguments().getBoolean(ENABLE_CAM)) {
            viewModel.enableCamera()
        } else {
            viewModel.disableCamera()
        }

        if (requireArguments().getBoolean(ENABLE_MIC)) {
            viewModel.unMuteMicrophone()
        } else {
            viewModel.muteMicrophone()
        }

        viewModel.startCall()
    }

    private fun configureUI() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.controlPanel.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        viewModel.cameraState.observe(viewLifecycleOwner) { state ->
            when (state!!) {
                CameraState.ENABLE -> {
                    binding.controlPanel.camera.setImageResource(R.drawable.ic_videocam_32)

                    binding.localVideoCardView.visibility = View.VISIBLE
                    binding.localVideo.connect()

                    if (viewModel.interlocutors.value?.allPeers?.isNotEmpty() == true) {
                        decreaseLocalVideo()
                    } else {
                        increaseLocalVideo()
                    }
                }
                CameraState.DISABLE -> {
                    binding.controlPanel.camera.setImageResource(R.drawable.ic_videocam_off_32)
                    binding.localVideoCardView.visibility = View.GONE
                }
            }
        }
        viewModel.microphoneState.observe(viewLifecycleOwner) { state ->
            when (state!!) {
                MicrophoneState.UN_MUTE -> {
                    binding.controlPanel.micro.setImageResource(R.drawable.ic_mic_32)
                }
                MicrophoneState.MUTE -> {
                    binding.controlPanel.micro.setImageResource(R.drawable.ic_mic_off_32)
                }
            }
        }
    }

    private fun configureButtons() {

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.closeCall()
            findNavController().popBackStack()
        }

        binding.controlPanel.callBtn.setOnClickListener {

            viewModel.closeCall()
            findNavController().popBackStack()
        }
        binding.controlPanel.camera.setOnClickListener {
            when (viewModel.cameraState.value!!) {
                CameraState.ENABLE -> {
                    viewModel.disableCamera()
                }
                CameraState.DISABLE -> {
                    viewModel.enableCamera()
                }
            }
        }
        binding.controlPanel.micro.setOnClickListener {
            when (viewModel.microphoneState.value!!) {
                MicrophoneState.UN_MUTE -> {
                    viewModel.muteMicrophone()
                }
                MicrophoneState.MUTE -> {
                    viewModel.unMuteMicrophone()
                }
            }
        }
        binding.controlPanel.switchCamera.setOnClickListener {
            viewModel.switchCamera()
        }
    }

    private fun configureInterlocutors() {
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        binding.interlocutorsRecyclerView.layoutManager = layoutManager
        binding.interlocutorsRecyclerView.adapter = interlocutorsAdapter

        viewModel.interlocutors.observe(viewLifecycleOwner) {
            if (it == null || it.allPeers.isEmpty()) {
                binding.interlocutorsRecyclerView.visibility = View.GONE

                if (viewModel.cameraState.value == CameraState.ENABLE) {
                    increaseLocalVideo()
                }
            } else {
                interlocutorsAdapter.setData(it.allPeers)

                if (viewModel.cameraState.value == CameraState.ENABLE) {
                    decreaseLocalVideo()
                }
                binding.interlocutorsRecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.layoutManagerSpanCount.observe(viewLifecycleOwner) { spanCount ->
            layoutManager.spanCount = spanCount
            InterlocutorsAdapter.layoutManagerSpanCount = spanCount

            binding.interlocutorsRecyclerView.children.forEach { view: View ->

                val childViewHolder =
                    binding.interlocutorsRecyclerView.getChildViewHolder(view) as InterlocutorsAdapter.InterlocutorViewHolder

                childViewHolder.adjustAspectRatioViewHolder()
            }
        }
    }

    private fun increaseLocalVideo() = with(binding.localVideoCardView) {
        val density = context.resources.displayMetrics.density
        val width = CoordinatorLayout.LayoutParams.MATCH_PARENT
        val height = CoordinatorLayout.LayoutParams.MATCH_PARENT

        val lp = CoordinatorLayout.LayoutParams(width, height).apply {
            val marginTop = (16 * density).roundToInt()
            setMargins(0, marginTop, 0, 0)
            gravity = Gravity.CENTER
        }
        layoutParams = lp
        radius = 24F
    }

    private fun decreaseLocalVideo() = with(binding.localVideoCardView) {
        val density = context.resources.displayMetrics.density
        val width = (90 * density).roundToInt()
        val height = (120 * density).roundToInt()

        val lp = CoordinatorLayout.LayoutParams(width, height).apply {
            val marginEnd = (8 * density).roundToInt()
            setMargins(0, 0, marginEnd, 0)

            anchorId = R.id.controlPanel
            anchorGravity = Gravity.TOP + Gravity.END
            dodgeInsetEdges = Gravity.BOTTOM
        }
        layoutParams = lp
        radius = 24F
    }

    companion object {
        const val ENABLE_MIC = "ENABLE_MIC_KEY"
        const val ENABLE_CAM = "ENABLE_CAM_KEY"
    }
}