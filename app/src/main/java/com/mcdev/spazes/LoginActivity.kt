package com.mcdev.spazes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseUser
import com.mcdev.spazes.databinding.ActivityLoginBinding
import com.mcdev.spazes.repository.FirebaseEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val viewModel: SpacesViewModel by viewModels()
    private val loadingDialog = LottieLoadingDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        //is user already logged in
        if (isUserLoggedIn()) {
            val currUser = loginViewModel.getCurrentUser()
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
            loginViewModel.signIn.collect {
                when (it) {
                    is LoginEventListener.SignedIn -> {
                        val userHashMap = hashMapOf(
                            "user_id" to it.data.uid,
                            "photo_url" to it.data.photoUrl.toString(),
                            "added_at" to Date().toString(),
                            "updated_at" to Date().toString()
                        )
                        viewModel.addUser(it.data.uid, userHashMap)
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

        lifecycleScope.launchWhenCreated {
            viewModel.fireStoreListener.collect{
                when (it) {
                    is FirebaseEventListener.Success -> {
                        Log.d("TAG", "onCreate: successsss")
                        val curr = loginViewModel.getCurrentUser()
                        startActivity(goToProfileActivity(this@LoginActivity, curr))
                        finish()
                        loadingDialog.dismiss()
                    }
                    is FirebaseEventListener.Failure -> {
                        loadingDialog.dismiss()
                        Toast.makeText(this@LoginActivity, "Failed. Try again!", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseEventListener.Empty -> {
                        loadingDialog.dismiss()
                        Toast.makeText(this@LoginActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseEventListener.Loading -> {
//                        loadingDialog.show(supportFragmentManager, "")
                    }
                }
            }
        }

        binding.loginBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun doLogin() {
        loginViewModel.login(this)
    }

    private fun isUserLoggedIn(): Boolean {
        return loginViewModel.isUserSignedIn()
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