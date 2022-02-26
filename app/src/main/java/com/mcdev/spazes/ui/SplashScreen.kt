package com.mcdev.spazes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.mcdev.spazes.databinding.ActivitySplashScreenBinding
import com.mcdev.spazes.makeStatusBarTransparent
import java.util.*

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        makeStatusBarTransparent()
        Timer().schedule(object: TimerTask(){
            override fun run() {
                startActivity(Intent(this@SplashScreen, HomeActivity::class.java))
                finish()
            }
        },1500)

    }
}