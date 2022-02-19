package com.mcdev.spazes.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.mcdev.spazes.service.FirebaseApiService
import com.mcdev.spazes.util.DBCollections
import com.mcdev.spazes.util.Resource
import java.lang.Exception
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(private val firesStore: FirebaseFirestore): FirebaseApiService {

    override suspend fun getFeaturedSpaces(featuredCollection: DBCollections): Resource<QuerySnapshot?> {
        return try {
            val response = firesStore.collection(featuredCollection.toString()).get()
            return if (response.isSuccessful) {
                Resource.Success(response.result)
            } else {
                Resource.Error("Error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getTrendingSpaces(trendingCollection: DBCollections): Resource<QuerySnapshot?> {
        return try {
            val response = firesStore.collection(trendingCollection.toString()).get()
            return if (response.isSuccessful) {
                Resource.Success(response.result)
            } else {
                Resource.Error("Error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

}