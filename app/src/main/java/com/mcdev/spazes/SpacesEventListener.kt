package com.mcdev.spazes

import com.mcdev.twitterapikit.response.SpaceResponseList


sealed class SpacesEventListener {
    class Success(val data: SpaceResponseList?): SpacesEventListener()
    class Failure(val error: String?): SpacesEventListener()
    object Loading: SpacesEventListener()
    object Empty: SpacesEventListener()
}
