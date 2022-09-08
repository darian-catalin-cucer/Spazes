package com.mcdev.spazes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.mcdev.spazes.R
import com.mcdev.spazes.changeStatusBarColor
import com.mcdev.spazes.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    lateinit var binding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this@SettingsActivity))
        setContentView(binding.root)

        changeStatusBarColor(R.color.white)

        binding.settingsThemeBtn.setOnClickListener { startActivity(Intent(this@SettingsActivity, SettingsThemeActivity::class.java)) }
    }
}