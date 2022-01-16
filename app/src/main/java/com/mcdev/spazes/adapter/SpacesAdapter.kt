package com.mcdev.spazes.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mcdev.spazes.R
import com.mcdev.spazes.databinding.SpaceItemBinding
import com.mcdev.spazes.databinding.SpaceItemV2Binding
import com.mcdev.spazes.dto.Spaces
import com.mcdev.spazes.dto.User
import com.mcdev.spazes.formatDateAndTime
import com.mcdev.twitterapikit.model.SpaceState

class SpacesAdapter(val context: Context, val listener: OnItemClickListener): RecyclerView.Adapter<SpacesAdapter.SpacesViewHolder>() {

    inner class SpacesViewHolder(val binding: SpaceItemV2Binding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpacesAdapter.SpacesViewHolder {
        val binding = SpaceItemV2Binding.bind(LayoutInflater.from(parent.context).inflate(R.layout.space_item_v2, parent, false))

        return SpacesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpacesAdapter.SpacesViewHolder, position: Int) {
        val space = spacesDiffer.currentList[position]


        val creatorId = space.creator_id
        val title = space?.title
        val hostIds = space?.host_ids

        val hostList : MutableList<User> = ArrayList<User>()
        hostList.clear()

        if (hostIds != null) {
            for (i in hostIds) {
                val hosts : List<User> = currentUsersList().filter {
                    it.id == i
                }
                hostList.addAll(hosts)
            }
        }

        if (hostList.size == 2) {
            holder.binding.speakerOneAvi.setImageURI(hostList[1].profile_image_url)
        }else if (hostList.size >= 2) {
            holder.binding.speakerOneAvi.setImageURI(hostList[1].profile_image_url)
            holder.binding.speakerTwoAvi.setImageURI(hostList[2].profile_image_url)
        }

        val creator : User? =  currentUsersList().find {
            it.id == creatorId
        }

        holder.binding.participants.text = creator?.name
        holder.binding.speakerAvi.setImageURI(creator?.profile_image_url)
        if (title.isNullOrBlank().not()) {
                holder.binding.title.text = title
            } else {
                holder.binding.title.text = "${creator?.name}'s Space"
            }

        when (space.state) {
            SpaceState.LIVE.value -> {
                holder.binding.apply {
                    liveViews.visibility = View.VISIBLE
                    stateScheduledView.visibility = View.GONE
                    participantCount.text = space.participant_count.toString()
//                    lotTv.text = context.resources.getString(R.string.listen_on_twitter)
                }
            }
            SpaceState.SCHEDULED.value -> {
                holder.binding.apply {
                    stateScheduledView.visibility = View.VISIBLE
                    liveViews.visibility = View.GONE
//                    lotTv.text = context.resources.getString(R.string.set_reminder)
                    stateScheduledView.text = space.scheduled_start?.formatDateAndTime()
                }
            }
        }


//        holder.binding.lotLay.setOnClickListener {
//            listener.onGoToClick(space, position)
//        }
//
        holder.itemView.setOnClickListener {
            listener.onItemClick(space, position)
        }
    }

    override fun getItemCount(): Int {
        return spacesDiffer.currentList.size
    }

    fun getUserCount(): Int {
        return usersDiffer.currentList.size
    }

    private val spacesDifferCallback = object : DiffUtil.ItemCallback<Spaces>() {
        override fun areItemsTheSame(oldItem: Spaces, newItem: Spaces): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Spaces, newItem: Spaces): Boolean {
            return oldItem == newItem
        }
    }

    private val usersDifferCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    val spacesDiffer = AsyncListDiffer(this, spacesDifferCallback)
    val usersDiffer = AsyncListDiffer(this, usersDifferCallback)

    fun submitSpacesList(list: List<Spaces>) {
        spacesDiffer.submitList(list)
    }

    fun currentSpacesList(): List<Spaces> {
        return spacesDiffer.currentList
    }

    fun submitUsersList(list: List<User>) {
        usersDiffer.submitList(list)
    }

    fun currentUsersList(): List<User> {
        return usersDiffer.currentList
    }

    interface OnItemClickListener {
        fun onItemClick(spaces: Spaces, position: Int)
        fun onGoToClick(spaces: Spaces, position: Int)
    }
}