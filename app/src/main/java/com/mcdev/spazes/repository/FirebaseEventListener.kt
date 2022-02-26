package com.mcdev.spazes.repository

import com.google.firebase.firestore.QuerySnapshot

sealed class FirebaseEventListener {
    class Success( val data: QuerySnapshot? = null): FirebaseEventListener()
    class Failure(val error: String?): FirebaseEventListener()
    class Empty(val message: Int?): FirebaseEventListener()
    object Loading: FirebaseEventListener()
}