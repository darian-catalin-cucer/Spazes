package com.mcdev.spazes.repository

import com.mcdev.spazes.util.Resource
import com.mcdev.twitterapikit.response.UserListResponse
import com.mcdev.twitterapikit.response.UserSingleResponse


interface UsersMainRepository {

    suspend fun getUsersByUsername(
        token: String,
        username: String,
        expansions: String,
        tweetFields: String,
        userFields: String
    ): Resource<UserSingleResponse>


    suspend fun getUsersByUsernames(
        token: String,
        usernames: String,
        expansions: String,
        tweetFields: String,
        userFields: String
    ): Resource<UserListResponse>
}