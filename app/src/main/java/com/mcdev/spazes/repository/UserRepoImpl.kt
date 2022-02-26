package com.mcdev.spazes.repository

import com.mcdev.spazes.service.TwitterApiService
import com.mcdev.spazes.util.Resource
import com.mcdev.twitterapikit.response.UserListResponse
import com.mcdev.twitterapikit.response.UserSingleResponse
import java.lang.Exception
import javax.inject.Inject

class UserRepoImpl @Inject constructor(private val twitterApiService: TwitterApiService) :
    UsersMainRepository {
    override suspend fun getUsersByUsername(
        token: String,
        username: String,
        expansions: String,
        tweetFields: String,
        userFields: String
    ): Resource<UserSingleResponse> {
        return try {
            val response = twitterApiService.getUsersByUsername(token, username, expansions, tweetFields, userFields)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else if (response.isSuccessful && body?.meta?.resultCount == 0) {
                Resource.Empty(body)
            } else {
                Resource.Error(response.body()?.detail!!)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getUsersByUsernames(
        token: String,
        usernames: String,
        expansions: String,
        tweetFields: String,
        userFields: String
    ): Resource<UserListResponse> {
        return try {
            val response = twitterApiService.getUsersByUsernames(token, usernames, expansions, tweetFields, userFields)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else if (response.isSuccessful && body?.meta?.resultCount == 0) {
                Resource.Empty(body)
            } else {
                Resource.Error(response.body()?.detail!!)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

}