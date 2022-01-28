package com.mcdev.spazes

import com.mcdev.twitterapikit.response.SpaceResponseSingle


sealed class SpacesSingleEventListener {
    class Success(val data: SpaceResponseSingle?): SpacesSingleEventListener()
    class Failure(val error: String?): SpacesSingleEventListener()
    object Loading: SpacesSingleEventListener()
    object Empty: SpacesSingleEventListener()

}
