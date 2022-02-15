package com.mcdev.spazes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mcdev.spazes.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        val profileUrl = intent.extras?.get("profile_url")
        val displayName = intent.extras?.get("username")
        Log.d("TAG", "onCreate: profile url is $displayName")
        binding.profileAvi.setImageURI(profileUrl.toString().getOriginalTwitterAvi())
        binding.displayName.text = displayName.toString()


        binding.profileBackBtn.setOnClickListener {
            finish()
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