package com.mcdev.spazes.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mcdev.spazes.R
import com.mcdev.spazes.databinding.UserItemBinding
import com.mcdev.spazes.getOriginalTwitterAvi
import com.mcdev.twitterapikit.`object`.User
import com.mcdev.twitterapikit.response.UserListResponse

class UserAdapter (val context: Context, val listener: OnUserItemClickListener): RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    private val usersDifferCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val usersDiffer = AsyncListDiffer(this, usersDifferCallback)

    interface OnUserItemClickListener {
        fun onUserItemClick(user: User, position: Int)
        fun onAddRemoveItemClick(user: User, position: Int)
    }

    inner class UserViewHolder(val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        )

        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val users = usersDiffer.currentList
        val user = usersDiffer.currentList[position]

        val username = "@${user.username}"
        val displayName = user.name
        val userPhotoUrl = user.profileImageUrl?.getOriginalTwitterAvi()
        //val isVerified = user.verified

//        when (user?.verified) {
//            true -> {
//                holder.binding.userVerifiedBadge.visibility = View.VISIBLE
//            }
//            else -> {
//                holder.binding.userVerifiedBadge.visibility = View.GONE
//            }
//        }

        holder.binding.apply {
            this.itemLay.background = ResourcesCompat.getDrawable(context.resources, R.drawable.bg_users_remove, context.theme)
            this.userAvi.setImageURI(userPhotoUrl)
            this.addRemoveBtn.setActualImageResource(R.drawable.minus)
            this.userName.text = username
//            this.userDisplayName.text = displayName
            this.userDisplayName.apply {
                customizeDisplayName.apply {
                    textSize = 17f
                    setTypeface(this.typeface, Typeface.BOLD)
                }
                setDisplayName(displayName!!, user.verified)
            }
        }


        holder.itemView.setOnClickListener {
            listener.onUserItemClick(user, position)
        }

        holder.binding.addRemoveBtn.setOnClickListener {
            listener.onAddRemoveItemClick(user, position)
        }
    }

    override fun getItemCount(): Int {
        return usersDiffer.currentList.size
    }

    fun submitResponse(res: UserListResponse) {
        usersDiffer.submitList(res.data)
    }

}