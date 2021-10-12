package com.mcdev.spazes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.mcdev.spazes.databinding.ActivitySplashScreenBinding
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