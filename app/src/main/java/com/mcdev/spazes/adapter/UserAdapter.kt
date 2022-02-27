package com.mcdev.spazes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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

        holder.binding.apply {
            this.userAvi.setImageURI(userPhotoUrl)
            this.userName.text = username
            this.userDisplayName.text = displayName
            this.addRemoveBtn.setActualImageResource(R.drawable.minus)
        }


        holder.itemView.setOnClickListener {
            listener.onUserItemClick(user, position)
        }
    }

    override fun getItemCount(): Int {
        return usersDiffer.currentList.size
    }

    fun submitResponse(res: UserListResponse) {
        usersDiffer.submitList(res.data)
    }

}