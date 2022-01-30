package com.mcdev.spazes

import com.mcdev.twitterapikit.response.SpaceResponseList


sealed class SpacesListEventListener {
    class Success(val data: SpaceResponseList?): SpacesListEventListener()
    class Failure(val error: String?): SpacesListEventListener()
    object Loading: SpacesListEventListener()
    class Empty(val message: Int?): SpacesListEventListener()
}
