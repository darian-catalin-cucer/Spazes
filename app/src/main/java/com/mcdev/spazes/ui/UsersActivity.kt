package com.mcdev.spazes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcdev.spazes.R
import com.mcdev.spazes.adapter.UserAdapter
import com.mcdev.spazes.changeStatusBarColor
import com.mcdev.spazes.databinding.ActivityUsersBinding
import com.mcdev.spazes.events.UserListEventListener
import com.mcdev.spazes.events.UserSingleEventListener
import com.mcdev.spazes.viewmodel.SpacesViewModel
import com.mcdev.twitterapikit.`object`.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class UsersActivity : AppCompatActivity(), UserAdapter.OnUserItemClickListener {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel: SpacesViewModel by viewModels()
    private var userAdapter : UserAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        userAdapter = UserAdapter(this, this)
        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UsersActivity)
//            itemAnimator = null//todo uncomment when it is causing crash
            this.adapter = userAdapter
        }

        changeStatusBarColor(R.color.white)

        getUsersByUsernames("LK_Gemma")

        binding.userBackBtn.setOnClickListener {
            finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.findUserByUsername.collect {
                when (it) {
                    is UserSingleEventListener.Success -> {
                        Log.d("TAG", "onCreate: UserSingle Event listener is success : ${it.data.toString()}" )
                    }
                    is UserSingleEventListener.Failure -> {
                        Log.d("TAG", "onCreate: UserSingle Event listener is failed")
                    }
                    is UserSingleEventListener.Empty -> {
                        Log.d("TAG", "onCreate: UserSingle Event listener is empty")
                    }
                    is UserSingleEventListener.Loading -> {
                        Log.d("TAG", "onCreate: UserSingle Event listener is loading")
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.findUsersByUserNames.collect {
                when (it) {
                    is UserListEventListener.Success -> {
                        it.data?.let { it1 -> userAdapter!!.submitResponse(it1) }
                        Log.d("TAG", "onCreate: UserSingle Event listener is success : ${it.data.toString()}" )}
                    is UserListEventListener.Failure -> {
                        Log.d("TAG", "onCreate: UserList Event listener is failed")
                    }
                    is UserListEventListener.Empty -> {
                        Log.d("TAG", "onCreate: UserList Event listener is empty")
                    }
                    is UserListEventListener.Loading -> {
                        Log.d("TAG", "onCreate: UserList Event listener is loading")
                    }
                }
            }
        }
    }

    private fun getUserByUsername(username: String) {
        viewModel.getUsersByUsername(username = username)
    }

    private fun getUsersByUsernames(usernames: String) {
        viewModel.getUsersByUsernames(usernames = usernames)
    }
    override fun onUserItemClick(user: User, position: Int) {
//        TODO("Not yet implemented")
    }
}