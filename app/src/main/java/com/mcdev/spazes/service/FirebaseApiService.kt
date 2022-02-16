package com.mcdev.spazes.service

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.mcdev.spazes.util.DBCollections
import com.mcdev.spazes.util.Resource

interface FirebaseApiService {
    suspend fun getFeaturedSpaces(
        featuredCollection: DBCollections
    ): Resource<QuerySnapshot?>

    suspend fun getTrendingSpaces(
        trendingCollection: DBCollections
    ): Resource<QuerySnapshot?>
}