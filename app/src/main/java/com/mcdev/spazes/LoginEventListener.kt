package com.mcdev.spazes

import com.google.firebase.auth.FirebaseUser

sealed class LoginEventListener {
    class SignedIn(val data: FirebaseUser): LoginEventListener()
    class SignedOut(val data: FirebaseUser): LoginEventListener()
    class Failure(val error: String?): LoginEventListener()
    object Loading : LoginEventListener()
    object PreLoad: LoginEventListener()
}
