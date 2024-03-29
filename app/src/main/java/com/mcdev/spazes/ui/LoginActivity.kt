package com.mcdev.spazes.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.google.firebase.auth.FirebaseUser
import com.mcdev.spazes.*
import com.mcdev.spazes.databinding.ActivityLoginBinding
import com.mcdev.spazes.events.LoginEventListener
import com.mcdev.spazes.repository.FirebaseEventListener
import com.mcdev.spazes.theme.BaseTheme
import com.mcdev.spazes.theme.DefaultTheme
import com.mcdev.spazes.viewmodel.DatastoreViewModel
import com.mcdev.spazes.viewmodel.LoginViewModel
import com.mcdev.spazes.viewmodel.SpacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import spencerstudios.com.bungeelib.Bungee
import java.util.*

@AndroidEntryPoint
class LoginActivity : ThemeActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val viewModel: SpacesViewModel by viewModels()
    private val dataStoreViewModel: DatastoreViewModel by viewModels()
    private val loadingDialog = LottieLoadingDialogFragment()
    private var userTwitterId: String? = null
    private var userTwitterHandle: String? = null
    private var userId: String? = null
    private var userDisplayName: String? = null
    private var userDisplayPhoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        changeStatusBarColor(R.color.white)

        runBlocking {
            userTwitterId = dataStoreViewModel.readDatastore("user_twitter_id")
            userTwitterHandle = dataStoreViewModel.readDatastore("user_twitter_handle")
            userId = dataStoreViewModel.readDatastore("user_firebase_id")
            userDisplayName = dataStoreViewModel.readDatastore("user_display_name")
            userDisplayPhoto = dataStoreViewModel.readDatastore("user_display_photo")
        }


        //is user already logged in
        if (isUserLoggedIn()) {
            val currUser = loginViewModel.getCurrentUser()
            startActivity(goToProfileActivity(this, currUser, userTwitterId, userTwitterHandle))
            finish()
            Bungee.zoomIn(this)
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
                        val userFirebaseId = it.data.user!!.uid
                        val id = it.data.additionalUserInfo?.profile?.get("id").toString()
                        val handle = it.data.additionalUserInfo?.username.toString()
                        val displayname = it.data.user!!.displayName
                        val displayPhoto = it.data.user!!.photoUrl

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
                        dataStoreViewModel.saveOrUpdateDatastore("user_twitter_id", id)
                        dataStoreViewModel.saveOrUpdateDatastore("user_twitter_handle", handle)
                        dataStoreViewModel.saveOrUpdateDatastore("user_firebase_id", userFirebaseId)
                        dataStoreViewModel.saveOrUpdateDatastore("user_display_name", displayname!!)
                        dataStoreViewModel.saveOrUpdateDatastore("user_display_photo", displayPhoto!!.toString())


                        //save user to firebase fireStore
                        viewModel.addUser(it.data.user?.uid!!, userHashMap)
                    }
                    is LoginEventListener.SignedOut -> {
                        loadingDialog.dismiss()
                        //update datastore to remove signed in user twitter id
                        dataStoreViewModel.saveOrUpdateDatastore("user_twitter_id", "")
                        dataStoreViewModel.saveOrUpdateDatastore("user_twitter_handle", "")
                        dataStoreViewModel.saveOrUpdateDatastore("user_firebase_id", "")
                        dataStoreViewModel.saveOrUpdateDatastore("user_display_name", "")
                        dataStoreViewModel.saveOrUpdateDatastore("user_display_photo", "")
                        Log.d("TAG", "onCreate: user is signed out oh")
                    }
                    is LoginEventListener.Failure -> {
                        loadingDialog.dismiss()
                        Log.d("TAG", "onCreate: it failed oh")
                        Toast.makeText(this@LoginActivity, "Failed", Toast.LENGTH_SHORT).show()
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

                        userTwitterId = dataStoreViewModel.readDatastore("user_twitter_id")
                        userTwitterHandle = dataStoreViewModel.readDatastore("user_twitter_handle")
                        userId = dataStoreViewModel.readDatastore("user_firebase_id")
                        userDisplayName = dataStoreViewModel.readDatastore("user_display_name")
                        userDisplayPhoto = dataStoreViewModel.readDatastore("user_display_photo")
                        startActivity(goToProfileActivity(this@LoginActivity, curr, userTwitterId, userTwitterHandle))
                        finish()
                        Bungee.zoomIn(this@LoginActivity)
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
                    else -> {
                        Log.d("TAG", "onCreate: else branch called")
                    }
                }
            }
        }

        binding.loginBackBtn.setOnClickListener {
            finish()
            Bungee.zoomOut(this)
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val tt = appTheme as BaseTheme
        changeStatusBarColor(tt.statusBarColor())
        binding.root.setBackgroundColor(tt.activityBgColor(this))
        binding.loginFeaturesTitleTv.setTextColor(resources.getColor(tt.textColor(), theme))
        binding.loginFeatureContentTv.setTextColor(resources.getColor(tt.textColor(), theme))
    }

    private fun doLogin() {
        loginViewModel.login(this)
    }

    private fun isUserLoggedIn(): Boolean {
        return loginViewModel.isUserSignedIn()
    }

    override fun getStartTheme(): AppTheme {
        //TODO("Not yet implemented")
        return DefaultTheme()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        Bungee.zoomOut(this)
    }

    private fun goToProfileActivity(activity: Activity, currUser: FirebaseUser?, userTwitterId: String?, userTwitterHandle: String?): Intent {
        return Intent(applicationContext, ProfileActivity::class.java)
            .putExtra("userDisplayPhoto", userDisplayPhoto)
            .putExtra("userTwitterId", userTwitterId)
            .putExtra("userTwitterHandle", userTwitterHandle)
            .putExtra("username", userDisplayName)
            .putExtra("userFirebaseId", currUser?.uid)
    }

}