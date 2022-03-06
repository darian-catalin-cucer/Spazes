package com.mcdev.spazes.events

import com.mcdev.twitterapikit.response.UserSingleResponse


sealed class UserSingleEventListener {
    class Success(val data: UserSingleResponse?): UserSingleEventListener()
    class Failure(val error: String?): UserSingleEventListener()
    object Loading: UserSingleEventListener()
    class Empty(val message: Int?): UserSingleEventListener()
}