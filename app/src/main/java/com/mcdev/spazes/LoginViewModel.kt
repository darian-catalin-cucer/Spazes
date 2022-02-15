package com.mcdev.spazes

import android.app.Activity
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.mcdev.spazes.util.DispatchProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dispatchProvider: DispatchProvider,
    private val oathProvider: OAuthProvider
): ViewModel(){
    private val twitterAuth = FirebaseAuth.getInstance()
    private val mutableLoginStateFlow =
        MutableStateFlow<LoginEventListener>(LoginEventListener.PreLoad)

    val signIn: StateFlow<LoginEventListener> = mutableLoginStateFlow


    fun login(activity: Activity){
        viewModelScope.launch(dispatchProvider.unconfined) {
            mutableLoginStateFlow.value = LoginEventListener.Loading

            val pendingResultTask = twitterAuth.pendingAuthResult
            var firebaseUser : FirebaseUser? = null

            if (pendingResultTask != null) {
                pendingResultTask
                    .addOnSuccessListener {
                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                        // The OAuth secret can be retrieved by calling:
                        // authResult.getCredential().getSecret().

                        it.apply {
                            additionalUserInfo?.isNewUser
                            additionalUserInfo?.providerId
                            additionalUserInfo?.username

                            user?.displayName
                            user?.email
                            user?.isAnonymous
                            user?.metadata?.creationTimestamp
                            user?.providerData?.get(0)?.displayName
                            user?.providerId
                            user?.tenantId
                            user?.photoUrl
                            user?.uid
                            user?.isEmailVerified
                        }

                        if (it.user != null) {
                            mutableLoginStateFlow.value = LoginEventListener.SignedIn(it.user!!)
                        } else {
                            mutableLoginStateFlow.value = LoginEventListener.Failure("User is null")
                        }
                    }
                    .addOnFailureListener {
                        mutableLoginStateFlow.value = LoginEventListener.Failure("Failed logging user in")
                    }
            } else {
                Log.d("TAG", "Starting the login flow")
                twitterAuth.startActivityForSignInWithProvider(activity, oathProvider)
                    .addOnSuccessListener {
                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                        // The OAuth secret can be retrieved by calling:
                        // authResult.getCredential().getSecret().

                        it.apply {
                            additionalUserInfo?.isNewUser
                            additionalUserInfo?.providerId
                            additionalUserInfo?.username

                            user?.displayName
                            user?.email
                            user?.isAnonymous
                            user?.metadata?.creationTimestamp
                            user?.providerData?.get(0)?.displayName
                            user?.providerId
                            user?.tenantId
                            user?.photoUrl
                            user?.uid
                            user?.isEmailVerified
                        }
                        if (it.user != null) {
                            mutableLoginStateFlow.value = LoginEventListener.SignedIn(it.user!!)
                        } else {
                            mutableLoginStateFlow.value = LoginEventListener.Failure("User is null")
                        }                     }
                    .addOnFailureListener {
                        mutableLoginStateFlow.value = LoginEventListener.Failure("Failed logging user in")
                        Log.d("TAG", "doLogin: failed oh")
                    }
            }
        }
    }

    fun isUserSignedIn(): Boolean {
        val currUser = twitterAuth.currentUser
        return currUser != null
    }

    fun getCurrentUser(): FirebaseUser? {
        return twitterAuth.currentUser
    }
}