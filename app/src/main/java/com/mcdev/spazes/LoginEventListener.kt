package com.mcdev.spazes

import com.google.firebase.auth.AuthResult

sealed class LoginEventListener {
    class SignedIn(val data: AuthResult): LoginEventListener()
    object SignedOut: LoginEventListener()
    class Failure(val error: String?): LoginEventListener()
    object Loading : LoginEventListener()
    object PreLoad: LoginEventListener()
}
