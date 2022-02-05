package com.mcdev.spazes

import com.mcdev.twitterapikit.response.SpaceListResponse


sealed class SpacesListEventListener {
    class Success(val data: SpaceListResponse?): SpacesListEventListener()
    class Failure(val error: String?): SpacesListEventListener()
    object Loading: SpacesListEventListener()
    class Empty(val message: Int?): SpacesListEventListener()
}
