package com.mcdev.spazes.events

import com.mcdev.twitterapikit.response.SpaceSingleResponse


sealed class SpacesSingleEventListener {
    class Success(val data: SpaceSingleResponse?): SpacesSingleEventListener()
    class Failure(val error: String?): SpacesSingleEventListener()
    object Loading: SpacesSingleEventListener()
    object Empty: SpacesSingleEventListener()

}
