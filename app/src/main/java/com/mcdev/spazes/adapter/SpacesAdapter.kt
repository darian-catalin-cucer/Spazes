package com.mcdev.spazes.adapter

import android.content.Context
import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.text.CompactDecimalFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mcdev.spazes.R
import com.mcdev.spazes.databinding.SpaceItemV2Binding
import com.mcdev.spazes.formatDateAndTime
import com.mcdev.spazes.getOriginalTwitterAvi
import com.mcdev.twitterapikit.`object`.Space
import com.mcdev.twitterapikit.`object`.User
import com.mcdev.twitterapikit.model.SpaceState
import com.mcdev.twitterapikit.response.SpaceListResponse
import java.util.*

class  SpacesAdapter(val context: Context, val listener: OnItemClickListener): RecyclerView.Adapter<SpacesAdapter.SpacesViewHolder>() {

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
        val users = usersDiffer.currentList
        val space = spacesDiffer.currentList[position]

        val creatorId = space.creatorId
        val title = space?.title
        val hostIds = space?.hostIds

        val hostList : MutableList<String> = mutableListOf()
        val creator: User? = users.find { it.id == creatorId }//find creator in the list of returned users

        if (hostIds != null) {
            for (i in hostIds) {
                val hosts : String? = users.find {
                    it.id == i
                }?.profileImageUrl

                if (hosts != null) {
                    hostList.add(hosts)
                }
            }
        }

        if (hostList.isEmpty().not()) {
            when {
                hostList.size == 2 -> {
                    val hostNum1 = hostList[1]
                    holder.binding.speakerOneAvi.setImageURI(hostNum1)
                }
                hostList.size >= 2 -> {
                    val hostNum1 = hostList[1]
                    val hostNum2 = hostList[2]
                    holder.binding.speakerOneAvi.setImageURI(hostNum1)
                    holder.binding.speakerTwoAvi.setImageURI(hostNum2)
                }
                else -> {
                    //set these null values to prevent duplicates
                    holder.binding.speakerOneAvi.setImageURI("null")
                    holder.binding.speakerTwoAvi.setImageURI("null")
                }
            }
        }


        holder.binding.participants.text = creator?.name //creator's name
        when (creator?.verified) {
            true -> {
                holder.binding.hostVerifiedBadge.visibility = View.VISIBLE
            }
            else -> {
                holder.binding.hostVerifiedBadge.visibility = View.GONE
            }
        }
        holder.binding.bgSpeaker.setImageURI(creator?.profileImageUrl?.getOriginalTwitterAvi())
        holder.binding.speakerAvi.setImageURI(creator?.profileImageUrl)
        holder.binding.title.text = title ?: "${creator?.name}'s Space"
//        if (title.isNullOrBlank().not()) {
//            holder.binding.title.text = title
//        } else {
//            holder.binding.title.text = "${creator?.name}'s Space"
//        }

        when (space.state) {
            SpaceState.LIVE.value -> {
                holder.binding.apply {
                    liveViews.visibility = View.VISIBLE
                    stateScheduledView.visibility = View.GONE
                    /*truncate participant count*/
                    val compactCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        NumberFormatter.with()
                            .notation(Notation.compactShort())
                            .precision(Precision.maxFraction(1))
                            .locale(Locale.US)
                            .format(space.participantCount)
                            .toString()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            val fmt: CompactDecimalFormat = CompactDecimalFormat.getInstance(
                                Locale.US,
                                CompactDecimalFormat.CompactStyle.SHORT
                            )
                            fmt.maximumFractionDigits = 1
                            fmt.format(space.participantCount)
                        } else {
                            //"VERSION.SDK_INT < N"
                                //does not truncate participant count for android 6 and lower
                            space.participantCount.toString()
                        }
                    }
                    participantCount.text = compactCount
//                    lotTv.text = context.resources.getString(R.string.listen_on_twitter)
                }
            }
            SpaceState.SCHEDULED.value -> {
                holder.binding.apply {
                    stateScheduledView.visibility = View.VISIBLE
                    liveViews.visibility = View.GONE
//                    lotTv.text = context.resources.getString(R.string.set_reminder)
                    stateScheduledView.text = space.scheduledStart?.formatDateAndTime()
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

    private val spacesDifferCallback = object : DiffUtil.ItemCallback<Space>() {
        override fun areItemsTheSame(oldItem: Space, newItem: Space): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Space, newItem: Space): Boolean {
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

    fun submitResponse(res: SpaceListResponse) {
        usersDiffer.submitList(res.includes?.users)
        spacesDiffer.submitList(res.data)
    }


    interface OnItemClickListener {
        fun onItemClick(spaces: Space, position: Int)
        fun onGoToClick(spaces: Space, position: Int)
    }
}