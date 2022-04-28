package com.gcorelabs.videocallsdemo.screens.call

import androidx.recyclerview.widget.DiffUtil
import gcore.videocalls.meet.model.Peer

class PeersDiffCallback(
    private val oldList: List<Peer>,
    private val newList: List<Peer>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPeer = oldList[oldItemPosition]
        val newPeer = newList[newItemPosition]

        return oldPeer.id == newPeer.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPeer = oldList[oldItemPosition]
        val newPeer = newList[newItemPosition]

        return oldPeer == newPeer
    }
}