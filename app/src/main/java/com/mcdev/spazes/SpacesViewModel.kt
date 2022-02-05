package com.mcdev.spazes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mcdev.spazes.repository.MainRepository
import com.mcdev.spazes.util.DBCollections
import com.mcdev.spazes.util.DispatchProvider
import com.mcdev.spazes.util.Resource
import com.mcdev.twitterapikit.`object`.Space
import com.mcdev.twitterapikit.response.SpaceListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpacesViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dispatchProvider: DispatchProvider
) : ViewModel() {

    /*using state flow*/
    private val mutableListStateFlow = MutableStateFlow<SpacesListEventListener>(SpacesListEventListener.Loading)
    private val mutableSingleStateFlow = MutableStateFlow<SpacesSingleEventListener>(SpacesSingleEventListener.Loading)
    private val db = Firebase.firestore

    val search: StateFlow<SpacesListEventListener> = mutableListStateFlow


    /*search spaces by query*/
    fun searchSpaces(
        token: String,
        query: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ) {

        viewModelScope.launch(dispatchProvider.io) {
            mutableListStateFlow.value = SpacesListEventListener.Loading
            when (val spacesListResponse = mainRepository.getSpacesByTitle(
                token,
                query,
                spaceFields,
                userFields,
                expansions,
                topicFields
            )) {
                is Resource.Success -> {
                    val spaces = spacesListResponse.data

                    if (spaces?.meta?.resultCount == 0) {
                        mutableListStateFlow.value =
                            SpacesListEventListener.Empty(R.string.no_spaces_found)
                    } else {
                        mutableListStateFlow.value = SpacesListEventListener.Success(spaces)
                    }
                }
                is Resource.Error -> {
                    mutableListStateFlow.value =
                        SpacesListEventListener.Failure(spacesListResponse.data?.detail)
                }
                else -> {
                    mutableListStateFlow.value = SpacesListEventListener.Failure(spacesListResponse.error)
                }
            }
        }
    }


    /*search spaces by list of ids*/
    fun searchSpacesByIds(
        token: String,
        ids: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String,
        firestoreCollection: String
    ) {

        viewModelScope.launch(dispatchProvider.io) {
            mutableListStateFlow.value = SpacesListEventListener.Loading

            when (val spacesListResponse = mainRepository.getSpacesByIds(
                token,
                ids,
                spaceFields,
                userFields,
                expansions,
                topicFields
            )) {
                is Resource.Success -> {
                    val spaces = spacesListResponse.data

                    if (spaces?.meta?.resultCount == 0) {
                        mutableListStateFlow.value =
                            SpacesListEventListener.Empty(R.string.no_featured_spaces)
                    } else {
                        val validFeatured = isSpaceExpired(spaceResponseList = spaces, firestoreCollection = firestoreCollection)
                        if (validFeatured.data.isNullOrEmpty()) {
                            mutableListStateFlow.value = SpacesListEventListener.Empty(R.string.no_featured_spaces)
                        } else {
                            mutableListStateFlow.value = SpacesListEventListener.Success(validFeatured)
                        }
                    }
                }
                is Resource.Error -> {
                    mutableListStateFlow.value =
                        SpacesListEventListener.Failure(spacesListResponse.data?.detail)
                }
                else -> {
                    mutableListStateFlow.value = SpacesListEventListener.Failure(spacesListResponse.error)
                }
            }
        }
    }

    /*search spaces by id*/
    fun searchSpacesById(
        token: String,
        id: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ) {
        viewModelScope.launch(dispatchProvider.io) {
            mutableSingleStateFlow.value = SpacesSingleEventListener.Loading
            when (val spacesSingleResponse = mainRepository.getSpacesById(
                token, id, spaceFields, userFields, expansions, topicFields
            )) {
                is Resource.Success -> {
                    val space = spacesSingleResponse.data

                    if (space?.meta?.resultCount == 0) {
                        mutableSingleStateFlow.value = SpacesSingleEventListener.Empty
                    } else {
                        mutableSingleStateFlow.value = SpacesSingleEventListener.Success(space)
                    }
                }

                is Resource.Error -> {
                    mutableSingleStateFlow.value =
                        SpacesSingleEventListener.Failure(spacesSingleResponse.data?.detail)
                }
                else -> {
                    mutableSingleStateFlow.value = SpacesSingleEventListener.Failure(spacesSingleResponse.error)
                }

            }
        }
    }

    private fun isSpaceExpired(spaceResponseList: SpaceListResponse?, firestoreCollection: String): SpaceListResponse {
        val validSpaces = ArrayList<Space>()
        if (spaceResponseList?.data != null) {
            for (space in spaceResponseList.data!!) {
                if (space.state.equals("ended").not()) {
                    validSpaces.add(space)
                } else {
                    //delete ended featured spaces. The delete option was chosen so that there will not be multiple reads to the database
                    db.collection(firestoreCollection)
                        .document(space.id!!)
                        .delete()
//                        .update("is_ended", true)
                        .addOnSuccessListener {
                            Log.d("TAG", "isSpaceExpired: delete success for ${space.id}")
                        }
                        .addOnFailureListener {
                            Log.d("TAG", "isSpaceExpired: delete failure")
                        }
                }
            }
            Log.d("TAG", "valid spaces are : $validSpaces")
        }

        spaceResponseList?.data = validSpaces
        return spaceResponseList!!
    }
}