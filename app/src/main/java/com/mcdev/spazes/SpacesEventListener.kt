package com.mcdev.spazes

import com.mcdev.spazes.dto.SpacesResponse

sealed class SpacesEventListener {
    class Success(val data: SpacesResponse): SpacesEventListener()
    class Failure(val errorMessage: String): SpacesEventListener()
    object Loading: SpacesEventListener()
    object Empty: SpacesEventListener()
}
