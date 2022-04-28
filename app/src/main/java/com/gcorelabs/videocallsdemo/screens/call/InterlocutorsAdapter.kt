package com.gcorelabs.videocallsdemo.screens.call

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gcorelabs.videocallsdemo.R
import com.gcorelabs.videocallsdemo.databinding.PeerItemBinding
import gcore.videocalls.meet.model.Peer

class InterlocutorsAdapter : RecyclerView.Adapter<InterlocutorsAdapter.InterlocutorViewHolder>() {

    private var listInterlocutors: MutableList<Peer> = ArrayList()

    fun setData(interlocutors: List<Peer>) {
        val diffCallback = PeersDiffCallback(listInterlocutors, interlocutors)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listInterlocutors.clear()
        listInterlocutors.addAll(interlocutors)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InterlocutorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PeerItemBinding.inflate(inflater, parent, false)

        return InterlocutorViewHolder(binding, parent.measuredHeight, parent.measuredWidth)
    }

    override fun onBindViewHolder(
        holder: InterlocutorViewHolder,
        position: Int
    ) {
        holder.bind(listInterlocutors[position])
    }

    override fun getItemCount(): Int {
        return listInterlocutors.size
    }

    class InterlocutorViewHolder(
        private val binding: PeerItemBinding,
        private val parentHeight: Int,
        private val parentWidth: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(interlocutor: Peer) {
            binding.peerVideo.connect(interlocutor.id)
            binding.interlocutorName.text = interlocutor.displayName

            checkMicroInterlocutor()
            binding.peerVideo.getAudioState().addOnPropertyChangedCallback(audioStateCallback)

            checkVideoStateInterlocutor()
            adjustAspectRatioViewHolder()
            binding.peerVideo.getVideoState().addOnPropertyChangedCallback(videoStateCallback)
            binding.peerVideo.getVideoState()
                .addOnPropertyChangedCallback(aspectRatioViewHolderCallback)

            Log.e("interlocutor", "isAttached ${binding.root.isAttachedToWindow}")

        }

        private val audioStateCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                checkMicroInterlocutor()
            }
        }

        private fun checkMicroInterlocutor() = with(binding) {
            if (peerVideo.getAudioState().get() == true) {
                indicatorMicro.setImageResource(R.drawable.ic_peer_micro_24)
            } else {
                indicatorMicro.setImageResource(R.drawable.ic_peer_micro_mute_24)
            }
        }

        private val videoStateCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                checkVideoStateInterlocutor()
            }
        }

        private fun checkVideoStateInterlocutor() {
            with(binding) {
                if (peerVideo.getVideoState().get() == true) {
                    indicatorCamera.setImageResource(R.drawable.ic_peer_camera_24)
                    peerVideoCardView.visibility = View.VISIBLE
                } else {
                    indicatorCamera.setImageResource(R.drawable.ic_peer_camera_mute_24)
                    peerVideoCardView.visibility = View.INVISIBLE
                }
            }
        }

        private val aspectRatioViewHolderCallback = object : Observable
        .OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                adjustAspectRatioViewHolder()
            }
        }

        fun adjustAspectRatioViewHolder() {
            with(binding) {
                if (peerVideo.getVideoState().get() == true) {
                    itemView.layoutParams.height = (parentWidth / layoutManagerSpanCount) * 4 / 3
                } else {
                    itemView.layoutParams.height = parentWidth / layoutManagerSpanCount
                }
            }
        }
    }

    companion object {
        var layoutManagerSpanCount = 1
    }
}