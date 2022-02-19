package com.mcdev.spazes.repository

import com.google.firebase.firestore.QuerySnapshot
import com.mcdev.spazes.util.DBCollections
import com.mcdev.spazes.util.Resource

interface FirebaseRepository {
    /*featured spaces*/
    suspend fun getFeaturedSpaces(
        featuredCollection: DBCollections
    ): Resource<QuerySnapshot>

    /*trending spaces*/
    suspend fun getTrendingSpaces(
        trendingCollection: DBCollections
    ): Resource<QuerySnapshot>
}