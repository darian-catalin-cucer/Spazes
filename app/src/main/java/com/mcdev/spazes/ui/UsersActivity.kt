package com.mcdev.spazes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.mcdev.spazes.R
import com.mcdev.spazes.databinding.ActivityUsersBinding
import com.mcdev.spazes.events.UserSingleEventListener
import com.mcdev.spazes.viewmodel.SpacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel: SpacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        getUserByUsername("mcderek_")

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
    }

    private fun getUserByUsername(username: String) {
        viewModel.getUsersByUsername(username = username)
    }
}