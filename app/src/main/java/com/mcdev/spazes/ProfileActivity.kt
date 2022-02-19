package com.mcdev.spazes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mcdev.spazes.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val viewModel: SpacesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        val profileUrl = intent.extras?.get("profile_url")
        val displayName = intent.extras?.get("username")
        val userTwitterId = intent.extras?.get("userTwitterId")
        val userTwitterHandle = intent.extras?.get("userTwitterHandle")

        Log.d("TAG", "onCreate: profile id is $userTwitterId")
        Log.d("TAG", "onCreate: profile handle is $userTwitterHandle")
        binding.profileAvi.setImageURI(profileUrl.toString().getOriginalTwitterAvi())
        binding.displayName.text = displayName.toString()


        binding.profileBackBtn.setOnClickListener {
            finish()
        }

        binding.signOutBtn.setOnClickListener {
            loginViewModel.logout()
            finish()
        }

        binding.favouriteHostsBtn.setOnClickListener {
            Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show()
        }

        binding.mySpacesBtn.setOnClickListener {
            startActivity(Intent(this, UserSpacesActivity::class.java)
                .putExtra("loadAction", LoadAction.MY_SPACES)
                .putExtra("user_twitter_id", userTwitterId.toString()))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}