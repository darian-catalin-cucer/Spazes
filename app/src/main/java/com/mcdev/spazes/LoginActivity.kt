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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val viewModel: SpacesViewModel by viewModels()
    private val loadingDialog = LottieLoadingDialogFragment()
    private var userTwitterId: String? = null
    private var userTwitterHandle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        changeStatusBarColor(R.color.white)

        runBlocking {
            userTwitterId = viewModel.readDatastore("user_twitter_id")
            userTwitterHandle = viewModel.readDatastore("user_twitter_handle")
        }


        //is user already logged in
        if (isUserLoggedIn()) {
            val currUser = loginViewModel.getCurrentUser()
            startActivity(goToProfileActivity(this, currUser, userTwitterId, userTwitterHandle))
            finish()
        }

        binding.loginWithTwitterTV.setOnClickListener {
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
                        val id = it.data.additionalUserInfo?.profile?.get("id").toString()
                        val handle = it.data.additionalUserInfo?.username.toString()

                        val userHashMap = hashMapOf(
                            "user_id" to it.data.user?.uid,
                            "photo_url" to it.data.user?.photoUrl.toString(),
                            "added_at" to Date().toString(),
                            "updated_at" to Date().toString(),
                            "user_handle" to handle,
                            "user_twitter_id" to id
                        )

                        //save to datastore to store the user's twitter id coz it cannot be
                        // accessed when user is already signed in..unless a request is made to firebase which i do not want to do
                        viewModel.saveOrUpdateDatastore("user_twitter_id", id)
                        viewModel.saveOrUpdateDatastore("user_twitter_handle", handle)

                        //save user to firebase fireStore
                        viewModel.addUser(it.data.user?.uid!!, userHashMap)
                    }
                    is LoginEventListener.SignedOut -> {
                        loadingDialog.dismiss()
                        //update datastore to remove signed in user twitter id
                        viewModel.saveOrUpdateDatastore("user_twitter_id", "")
                        viewModel.saveOrUpdateDatastore("user_twitter_handle", "")
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
                        val curr = loginViewModel.getCurrentUser()

                        userTwitterId = viewModel.readDatastore("user_twitter_id")
                        userTwitterHandle = viewModel.readDatastore("user_twitter_handle")
                        startActivity(goToProfileActivity(this@LoginActivity, curr, userTwitterId, userTwitterHandle))
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

    private fun goToProfileActivity(activity: Activity, currUser: FirebaseUser?, userTwitterId: String?, userTwitterHandle: String?): Intent {
        return Intent(applicationContext, ProfileActivity::class.java)
            .putExtra("profile_url", currUser?.photoUrl)
            .putExtra("userTwitterId", userTwitterId)
            .putExtra("userTwitterHandle", userTwitterHandle)
            .putExtra("username", currUser?.displayName)
    }

}