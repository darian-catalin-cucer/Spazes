package com.mcdev.spazes

import com.mcdev.twitterapikit.response.SpaceSingleResponse


sealed class SpacesSingleEventListener {
    class Success(val data: SpaceSingleResponse?): SpacesSingleEventListener()
    class Failure(val error: String?): SpacesSingleEventListener()
    object Loading: SpacesSingleEventListener()
    object Empty: SpacesSingleEventListener()

}
