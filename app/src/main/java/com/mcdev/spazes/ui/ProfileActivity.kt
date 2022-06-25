package com.mcdev.spazes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.mcdev.spazes.*
import com.mcdev.spazes.databinding.ActivityProfileBinding
import com.mcdev.spazes.enums.LoadAction
import com.mcdev.spazes.events.UserSingleEventListener
import com.mcdev.spazes.viewmodel.LoginViewModel
import com.mcdev.spazes.viewmodel.SpacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val viewModel: SpacesViewModel by viewModels()
    private val TAG = "ProfileActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        changeStatusBarColor(R.color.white)

        val userId = intent.extras?.get("userFirebaseId")
        var profileUrl = intent.extras?.get("userDisplayPhoto")
        var displayName = intent.extras?.get("username")
        val userTwitterId = intent.extras?.get("userTwitterId")
        val userTwitterHandle = intent.extras?.get("userTwitterHandle")

        Log.d("TAG", "onCreate: profile id is $userTwitterId")
        Log.d("TAG", "onCreate: profile handle is $userTwitterHandle")
        binding.profileAvi.setImageURI(profileUrl.toString().getOriginalTwitterAvi())
        binding.displayName.text = displayName.toString()


        binding.profileBackBtn.setOnClickListener {
            finish()
        }

        getUserById(userTwitterId.toString())

        binding.signOutBtn.setOnClickListener {
            AlertDialog.Builder(this@ProfileActivity)
                .setTitle(getString(R.string.logout_dialog_title))
                .setMessage(getString(R.string.logout_confirmation_message))
                .setPositiveButton(getString(R.string.yes)
                ) { _, _ -> lifecycleScope.launch { doSignOut() } }//signOut
                .setNegativeButton(getString(R.string.no)
                ) { p0, _ -> p0.dismiss() }// dismiss dialog
                .show()
        }

        binding.favouriteHostsBtn.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java)
                .putExtra("loadAction", LoadAction.FAVE_HOSTS)
                .putExtra("user_twitter_id", userTwitterId.toString())
                .putExtra("user_firebase_id", userId.toString()))
        }

        binding.mySpacesBtn.setOnClickListener {
            startActivity(Intent(this, UserSpacesActivity::class.java)
                .putExtra("loadAction", LoadAction.MY_SPACES)
                .putExtra("user_twitter_id", userTwitterId.toString())
                .putExtra("user_firebase_id", userId.toString()))
        }

        binding.favouriteHostsSpacesBtn.setOnClickListener {
            startActivity(Intent(this, UserSpacesActivity::class.java)
                .putExtra("loadAction", LoadAction.FAVE_HOSTS_SPACES)
                .putExtra("user_twitter_id", userTwitterId.toString())
                .putExtra("user_firebase_id", userId.toString()))
        }


        lifecycleScope.launchWhenStarted {
            viewModel.findUserById.collect {
                when (it) {
                    is UserSingleEventListener.Success -> {
                        Log.d(TAG, "onCreate: got user stuff " + it.data?.data?.name)
                        displayName = it.data?.data?.name
                        profileUrl = it.data?.data?.profileImageUrl.toString().getOriginalTwitterAvi()
                        if ((displayName.toString() == "null").not()) {// set or update display name
                            viewModel.saveOrUpdateDatastore("user_display_name", displayName.toString())
                        }
                        if ((profileUrl.toString() == "null").not()) {// set or update display photo
                            viewModel.saveOrUpdateDatastore("user_display_photo", profileUrl.toString())
                        }
                    }
                    is UserSingleEventListener.Empty -> {
                        Log.d(TAG, "onCreate: user stuff is empty")
                    }
                    is UserSingleEventListener.Failure -> {
                        Log.d(TAG, "onCreate: could not get user stuff" + it)
                    }
                    is UserSingleEventListener.Loading -> {
                        Log.d(TAG, "onCreate: user stuff is loading...")
                    }
                }
            }
        }
    }

    private suspend fun doSignOut() {
        val loadingDialog = LottieLoadingDialogFragment()
        val bundle = Bundle()
        bundle.putString("message", "Logging out...")
        loadingDialog.isCancelable = false
        loadingDialog.arguments = bundle
        loadingDialog.show(supportFragmentManager, "")

        delay(3000)
        loginViewModel.logout()
        finish()
    }

    private fun getUserById(id: String) {
        viewModel.getUserById(id = id)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}