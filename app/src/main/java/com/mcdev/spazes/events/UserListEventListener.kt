package com.mcdev.spazes.events

import com.mcdev.twitterapikit.response.UserListResponse

sealed class UserListEventListener {
    class Success(val data: UserListResponse?): UserListEventListener()
    class Failure(val error: String?): UserListEventListener()
    object Loading: UserListEventListener()
    class Empty(val message: Int?): UserListEventListener()
}