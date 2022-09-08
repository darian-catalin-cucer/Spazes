package com.mcdev.spazes.adapter

import android.content.Context
import android.graphics.Color
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
import com.mcdev.spazes.databinding.SpaceItemRecyclerBinding
import com.mcdev.spazes.databinding.SpaceItemV2Binding
import com.mcdev.spazes.formatDateAndTime
import com.mcdev.spazes.getOriginalTwitterAvi
import com.mcdev.tweeze.util.VerifiedBadge
import com.mcdev.twitterapikit.`object`.Space
import com.mcdev.twitterapikit.`object`.User
import com.mcdev.twitterapikit.model.SpaceState
import com.mcdev.twitterapikit.response.SpaceListResponse
import java.util.*

class  SpacesAdapter(val context: Context, val listener: OnSpacesItemClickListener): RecyclerView.Adapter<SpacesAdapter.SpacesViewHolder>() {

    inner class SpacesViewHolder(val binding: SpaceItemRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpacesAdapter.SpacesViewHolder {
        val binding = SpaceItemRecyclerBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.space_item_recycler, parent, false))

        return SpacesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpacesAdapter.SpacesViewHolder, position: Int) {
        val holderBinding = holder.binding.spaceComponentRecycler
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
            holderBinding.setSpeakerImageUri(hostList)
//            when {
//                hostList.size == 2 -> {
//                    val hostNum1 = hostList[1]
////                    holderBinding.speakerOneAvi.setImageURI(hostNum1)
//                    holderBinding.setSpeakerImageUri(hostNum1)
//                }
//                hostList.size >= 2 -> {
//                    val hostNum1 = hostList[1]
//                    val hostNum2 = hostList[2]
////                    holderBinding.speakerOneAvi.setImageURI(hostNum1)
////                    holderBinding.speakerTwoAvi.setImageURI(hostNum2)
//                }
//                else -> {
//                    //set these null values to prevent duplicates
////                    holderBinding.speakerOneAvi.setImageURI("null")
////                    holderBinding.speakerTwoAvi.setImageURI("null")
//                }
//            }
        }


//        holderBinding.twitterDisplayNameView.customizeDisplayName.setTextColor(Color.WHITE)
        holderBinding.setDisplayNameColor(Color.WHITE)
//        holderBinding.twitterDisplayNameView.setDisplayName(creator!!.name!!, creator.verified,VerifiedBadge.WHITE)//creator's name with verification status
        holderBinding.setDisplayName(creator!!.name!!, creator.verified, VerifiedBadge.WHITE)

//        holderBinding.bgSpeaker.setImageURI(creator?.profileImageUrl?.getOriginalTwitterAvi())
        holderBinding.setCardBgImageUri(creator.profileImageUrl?.getOriginalTwitterAvi())
//        holderBinding.speakerAvi.setImageURI(creator?.profileImageUrl)

//        holderBinding.title.text = title ?: "${creator?.name}'s Space"
        holderBinding.title = title ?: "${creator.name}'s Space"

        when (space.state) {
            SpaceState.LIVE.value -> {
                holderBinding.apply {
                    isLive = true
//                    liveViews.visibility = View.VISIBLE
//                    stateScheduledView.visibility = View.GONE
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
                    participantCount = compactCount
//                    lotTv.text = context.resources.getString(R.string.listen_on_twitter)
                }
            }
            SpaceState.SCHEDULED.value -> {
                holderBinding.apply {
                    isLive = false
//                    stateScheduledView.visibility = View.VISIBLE
//                    liveViews.visibility = View.GONE
//                    lotTv.text = context.resources.getString(R.string.set_reminder)
                    scheduledDate = space.scheduledStart?.formatDateAndTime()
                }
            }
        }



//        holderBinding.lotLay.setOnClickListener {
//            listener.onGoToClick(space, position)
//        }
//
        holder.itemView.setOnClickListener {
            listener.onSpacesItemClick(space, position)
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


    interface OnSpacesItemClickListener {
        fun onSpacesItemClick(spaces: Space, position: Int)
        fun onGoToClick(spaces: Space, position: Int)
    }
}