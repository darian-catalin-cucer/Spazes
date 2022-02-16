package com.mcdev.spazes.repository

import com.google.firebase.firestore.QuerySnapshot
import com.mcdev.spazes.LoginEventListener

sealed class FirebaseEventListener {
    class Success( val data: QuerySnapshot? = null): FirebaseEventListener()
    class Failure(val error: String?): FirebaseEventListener()
    class Empty(val message: String?): FirebaseEventListener()
    object Loading: FirebaseEventListener()
}