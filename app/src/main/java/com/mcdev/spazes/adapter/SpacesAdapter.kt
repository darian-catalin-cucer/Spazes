package com.mcdev.spazes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mcdev.spazes.R
import com.mcdev.spazes.databinding.SpaceItemBinding
import com.mcdev.spazes.dto.Spaces
import com.mcdev.spazes.dto.User
import com.mcdev.spazes.formatDateAndTime
import com.mcdev.twitterapikit.model.SpaceState

class SpacesAdapter(val context: Context, val listener: OnItemClickListener): RecyclerView.Adapter<SpacesAdapter.SpacesViewHolder>() {

    inner class SpacesViewHolder(val binding: SpaceItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpacesAdapter.SpacesViewHolder {
        val binding = SpaceItemBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.space_item, parent, false))

        return SpacesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpacesAdapter.SpacesViewHolder, position: Int) {
        val space = spacesDiffer.currentList[position]


        val creatorId = space.creator_id
        val title = space?.title
        val hostIds = space?.host_ids

        for (i in 0..getUserCount().dec()) {
            val user = usersDiffer.currentList[i]
            holder.binding.participants.text = user.name
            if (title.isNullOrBlank().not()) {
                holder.binding.title.text = title
            } else {
                holder.binding.title.text = "${user.name}'s Space"
            }
        }

        val creator : User? =  currentUsersList().find {
            it.id == creatorId
        }

        holder.binding.speakerAvi.setImageURI(creator?.profile_image_url)


        when (space.state) {
            SpaceState.LIVE.value -> {
                holder.binding.apply {
                    liveViews.visibility = View.VISIBLE
                    stateScheduledView.visibility = View.GONE
                    participantCount.text = space.participant_count.toString()
                    lotTv.text = context.resources.getString(R.string.listen_on_twitter)
                }
            }
            SpaceState.SCHEDULED.value -> {
                holder.binding.apply {
                    stateScheduledView.visibility = View.VISIBLE
                    liveViews.visibility = View.GONE
                    lotTv.text = context.resources.getString(R.string.set_reminder)
                    stateScheduledView.text = space.scheduled_start?.formatDateAndTime()
                }
            }
        }


        holder.binding.lotLay.setOnClickListener {
            listener.onGoToClick(space, position)
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
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
        fun onItemClick(position: Int)
        fun onGoToClick(spaces: Spaces, position: Int)
    }
}