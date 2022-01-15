package com.mcdev.spazes.repository

import com.mcdev.spazes.dto.SpacesResponse
import com.mcdev.spazes.service.SpacesApiService
import com.mcdev.spazes.util.Resource
import java.lang.Exception
import javax.inject.Inject

class SpacesRepository @Inject constructor(private val spacesApiService: SpacesApiService) : MainRepository {
    /*search spaces by title*/
    override suspend fun getSpacesByTitle(token: String, query: String, spaceFields: String, userFields: String, expansions: String): Resource<SpacesResponse> {
        return try {
            val response = spacesApiService.getSpacesByTitle(token, query, spaceFields, userFields ,expansions)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occured")
        }
    }
}