package com.mcdev.spazes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseUser
import com.mcdev.spazes.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val loadingDialog = LottieLoadingDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        //is user already logged in
        if (isUserLoggedIn()) {
            val currUser = viewModel.getCurrentUser()
            startActivity(goToProfileActivity(this, currUser))
            finish()
        }

        binding.loginClicker.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("message", "Logging in...")
            loadingDialog.isCancelable = false
            loadingDialog.arguments = bundle

            //start login
            doLogin()
        }


        //collect login
        lifecycleScope.launchWhenStarted {
            viewModel.signIn.collect {
                when (it) {
                    is LoginEventListener.SignedIn -> {
                        startActivity(goToProfileActivity(this@LoginActivity, it.data))
                        finish()
                        loadingDialog.dismiss()
                        Log.d("TAG", "onCreate: signed in oh")
                    }
                    is LoginEventListener.SignedOut -> {
                        loadingDialog.dismiss()
                        Log.d("TAG", "onCreate: user is signed out oh")
                    }
                    is LoginEventListener.Failure -> {
                        loadingDialog.dismiss()
                        Log.d("TAG", "onCreate: it failed oh")
                    }
                    is LoginEventListener.PreLoad -> {
                        Log.d("TAG", "onCreate: preload oh")
                    }
                    is LoginEventListener.Loading -> {
                        loadingDialog.show(supportFragmentManager, "")
                        Log.d("TAG", "onCreate: it os loading oh")
                    }
                }
            }
        }


        binding.loginBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun doLogin() {
        viewModel.login(this)
    }

    private fun isUserLoggedIn(): Boolean {
        return viewModel.isUserSignedIn()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun goToProfileActivity(activity: Activity, currUser: FirebaseUser?): Intent {
        return Intent(applicationContext, ProfileActivity::class.java)
            .putExtra("profile_url", currUser?.photoUrl)
            .putExtra("username", currUser?.displayName)
    }

}